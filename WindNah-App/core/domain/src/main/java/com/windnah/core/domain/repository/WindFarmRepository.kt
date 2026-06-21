package com.windnah.core.domain.repository

import com.windnah.core.model.WindFarmDetail
import com.windnah.core.model.WindFarmPreview
import kotlinx.coroutines.flow.Flow

interface WindFarmRepository {
    fun observeWindFarmPreviews(): Flow<List<WindFarmPreview>>
    fun observeWindFarmDetail(windFarmId: String): Flow<WindFarmDetail?>
}
