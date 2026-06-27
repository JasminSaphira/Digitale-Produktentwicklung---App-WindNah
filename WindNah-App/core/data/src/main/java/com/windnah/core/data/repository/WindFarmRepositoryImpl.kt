package com.windnah.core.data.repository

import com.windnah.core.data.mapper.toCachedEntity
import com.windnah.core.data.mapper.toWindFarmPreview
import com.windnah.core.data.mapper.toWindFarmPreviews
import com.windnah.core.data.mapper.toWindTurbine
import com.windnah.core.data.mapper.toWindTurbines
import com.windnah.core.data.mapper.unitsForWindFarmId
import com.windnah.core.database.cache.CachedWindFarmDao
import com.windnah.core.domain.repository.WindFarmRepository
import com.windnah.core.model.WindFarmDetail
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmPreviewsResult
import com.windnah.core.network.mastr.MastrRemoteDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WindFarmRepositoryImpl @Inject constructor(
    private val mastr: MastrRemoteDataSource,
    private val cacheDao: CachedWindFarmDao,
) : WindFarmRepository {

    private val refreshMutex = Mutex()

    /**
     * Cache-first: emits the persisted parks immediately (works offline), then always refreshes
     * from MaStR on subscription. On a successful fetch the cache is replaced and re-emitted.
     * If the network fails the cached data is kept; no mock fallback.
     */
    override fun observeWindFarmPreviews(): Flow<WindFarmPreviewsResult> = flow {
        val cached = cacheDao.observeWindFarms().first()
        if (cached.isNotEmpty()) {
            // Show cached data right away; mark fresh optimistically while the refresh runs.
            emit(WindFarmPreviewsResult(cached.map { it.toWindFarmPreview() }, isStale = false))
        }

        val refreshed = refreshFromNetwork()
        when {
            refreshed != null -> emit(WindFarmPreviewsResult(refreshed, isStale = false))
            cached.isNotEmpty() -> // refresh failed but cache exists → flag as stale (offline)
                emit(WindFarmPreviewsResult(cached.map { it.toWindFarmPreview() }, isStale = true))
            else -> // neither cache nor network — surface the failure for an error/empty state
                throw IllegalStateException("No cached wind farms and MaStR is unreachable")
        }
    }

    override fun observeWindFarmDetail(windFarmId: String): Flow<WindFarmDetail?> = flow {
        val decodedId = runCatching { java.net.URLDecoder.decode(windFarmId, "UTF-8") }.getOrElse { windFarmId }

        // Make sure the cache is populated (first launch, or detail opened before the map).
        if (cacheDao.count() == 0) {
            refreshFromNetwork()
        }

        val farmEntity = cacheDao.getWindFarm(decodedId)
            ?: run { emit(null); return@flow }
        val turbines = cacheDao.getTurbines(decodedId).map { it.toWindTurbine() }
        val preview = farmEntity.toWindFarmPreview()

        emit(
            WindFarmDetail(
                windFarm = preview.windFarm,
                energyMetrics = preview.energyMetrics,
                turbines = turbines,
            ),
        )
    }

    /**
     * Fetches from MaStR and replaces the cache. Returns the fresh previews on success, or null
     * if the network/parse failed (cancellation is rethrown so superseded loads cancel cleanly).
     */
    private suspend fun refreshFromNetwork(): List<WindFarmPreview>? =
        refreshMutex.withLock {
            runCatching {
                val units = mastr.getWindUnits()
                val previews = units.toWindFarmPreviews()
                val now = System.currentTimeMillis()
                val farmEntities = previews.map { it.toCachedEntity(now) }
                val turbineEntities = previews.flatMap { preview ->
                    units.unitsForWindFarmId(preview.windFarm.id)
                        .toWindTurbines(preview.windFarm.id)
                        .map { it.toCachedEntity() }
                }
                cacheDao.replaceAll(farmEntities, turbineEntities)
                previews
            }.getOrElse { error ->
                if (error is CancellationException) throw error
                android.util.Log.e("WindFarmRepo", "MaStR refresh failed, keeping cached data", error)
                null
            }
        }
}
