package com.windnah.core.network.mastr

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MastrRemoteDataSource @Inject constructor(
    private val soapClient: MastrSoapClient,
) {
    suspend fun getWindUnits(): List<MastrWindUnitDto> =
        withContext(Dispatchers.IO) { soapClient.getWindEinheiten() }
}
