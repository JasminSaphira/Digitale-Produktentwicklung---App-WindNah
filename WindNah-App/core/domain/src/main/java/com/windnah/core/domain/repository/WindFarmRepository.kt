package com.windnah.core.domain.repository

import com.windnah.core.model.WindFarmPreview
import kotlinx.coroutines.flow.Flow

interface WindFarmRepository {
    fun observeWindFarmPreviews(): Flow<List<WindFarmPreview>>
}
