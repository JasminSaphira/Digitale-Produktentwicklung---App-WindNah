package com.windnah.core.domain.usecase

import com.windnah.core.domain.repository.WindFarmRepository
import com.windnah.core.model.WindFarmDetail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWindFarmDetailUseCase @Inject constructor(
    private val repository: WindFarmRepository,
) {
    operator fun invoke(windFarmId: String): Flow<WindFarmDetail?> =
        repository.observeWindFarmDetail(windFarmId)
}
