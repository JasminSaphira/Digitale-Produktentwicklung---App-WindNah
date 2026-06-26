package com.windnah.core.data.repository

import com.windnah.core.database.favorite.FavoriteDao
import com.windnah.core.database.favorite.FavoriteEntity
import com.windnah.core.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
) : FavoriteRepository {

    override fun observeFavoriteIds(): Flow<List<String>> =
        favoriteDao.observeFavoriteIds()

    override fun observeIsFavorite(windFarmId: String): Flow<Boolean> =
        favoriteDao.observeIsFavorite(windFarmId)

    override suspend fun addFavorite(windFarmId: String) {
        favoriteDao.insertFavorite(
            FavoriteEntity(
                windFarmId = windFarmId,
                createdAtMillis = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun removeFavorite(windFarmId: String) {
        favoriteDao.deleteFavorite(windFarmId)
    }

    override suspend fun toggleFavorite(windFarmId: String) {
        if (favoriteDao.isFavorite(windFarmId)) {
            removeFavorite(windFarmId)
        } else {
            addFavorite(windFarmId)
        }
    }
}
