package com.windnah.core.data.repository

import com.windnah.core.data.mapper.toWindFarmPreviews
import com.windnah.core.data.mapper.toWindTurbines
import com.windnah.core.data.mapper.unitsForWindFarmId
import com.windnah.core.domain.repository.WindFarmRepository
import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmDetail
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import com.windnah.core.model.WindTurbine
import com.windnah.core.network.mastr.MastrRemoteDataSource
import com.windnah.core.network.mastr.MastrWindUnitDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WindFarmRepositoryImpl @Inject constructor(
    private val mastr: MastrRemoteDataSource,
) : WindFarmRepository {

    private var cachedUnits: List<MastrWindUnitDto>? = null
    private var cachedPreviews: List<WindFarmPreview>? = null
    private val fetchMutex = Mutex()

    override fun observeWindFarmPreviews(): Flow<List<WindFarmPreview>> = flow {
        val previews = cachedPreviews ?: fetchAndCacheWithFallback()
        emit(previews)
    }

    override fun observeWindFarmDetail(windFarmId: String): Flow<WindFarmDetail?> = flow {
        val previews = cachedPreviews ?: fetchAndCacheWithFallback()
        val allUnits = cachedUnits

        val decodedId = runCatching { java.net.URLDecoder.decode(windFarmId, "UTF-8") }.getOrElse { windFarmId }
        val preview = previews.firstOrNull { it.windFarm.id == decodedId }
            ?: run { emit(null); return@flow }

        // If we have real MaStR units, find matching turbines using the same grouping as previews
        val turbines = if (allUnits != null) {
            allUnits.unitsForWindFarmId(decodedId).toWindTurbines(decodedId)
        } else {
            // Fallback: generate mock turbines from wind farm aggregate data
            FakeWindFarmRepository.mockTurbinesFor(preview)
        }

        emit(WindFarmDetail(
            windFarm = preview.windFarm,
            energyMetrics = preview.energyMetrics,
            turbines = turbines,
        ))
    }

    private suspend fun fetchAndCacheWithFallback(): List<WindFarmPreview> =
        fetchMutex.withLock {
            // Re-check inside lock — another coroutine may have already fetched
            cachedPreviews?.let { return@withLock it }
            runCatching {
                val units = mastr.getWindUnits()
                cachedUnits = units
                val previews = units.toWindFarmPreviews()
                cachedPreviews = previews
                previews
            }.getOrElse { error ->
                // Never swallow coroutine cancellation — rethrow so the caller can cancel cleanly
                // instead of falling back to mock data when a load is simply superseded.
                if (error is kotlinx.coroutines.CancellationException) throw error
                android.util.Log.e("WindFarmRepo", "fetch/map failed, falling back to mock data", error)
                // MaStR not reachable — use built-in mock data so the app stays functional
                val fallback = FakeWindFarmRepository.mockPreviews
                cachedPreviews = fallback
                cachedUnits = null
                fallback
            }
        }
}
