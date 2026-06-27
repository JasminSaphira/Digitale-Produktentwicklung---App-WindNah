package com.windnah.core.domain.repository

import com.windnah.core.model.WindFarmDetail
import com.windnah.core.model.WindFarmPreviewsResult
import kotlinx.coroutines.flow.Flow

interface WindFarmRepository {
    fun observeWindFarmPreviews(): Flow<WindFarmPreviewsResult>
    fun observeWindFarmDetail(windFarmId: String): Flow<WindFarmDetail?>
}
