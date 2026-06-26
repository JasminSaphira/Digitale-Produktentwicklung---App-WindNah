package com.windnah.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavoriteIds(): Flow<List<String>>
    fun observeIsFavorite(windFarmId: String): Flow<Boolean>
    suspend fun addFavorite(windFarmId: String)
    suspend fun removeFavorite(windFarmId: String)
    suspend fun toggleFavorite(windFarmId: String)
}
