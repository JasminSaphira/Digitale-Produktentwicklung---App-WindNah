package com.windnah.feature.discover

import com.windnah.core.model.WindFarmPreview
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Spatial clustering of wind-farm previews for the map. Pure geometry (no Android/Compose deps) so
 * it lives apart from the map UI. At close zoom every park is its own marker; zooming out merges
 * nearby parks into a single cluster marker whose radius grows with distance.
 */
internal data class WindFarmCluster(
    val previews: List<WindFarmPreview>,
    val latitude: Double,
    val longitude: Double,
)

internal fun clusterWindFarms(
    windFarms: List<WindFarmPreview>,
    zoomLevel: Double,
): List<WindFarmCluster> {
    val radiusKm = when {
        zoomLevel >= 10.0 -> 0.0
        zoomLevel >= 8.0 -> 20.0
        zoomLevel >= 6.0 -> 60.0
        else -> 150.0
    }

    if (radiusKm == 0.0) {
        return windFarms.map { WindFarmCluster(listOf(it), it.windFarm.latitude, it.windFarm.longitude) }
    }

    val remaining = windFarms.toMutableList()
    val clusters = mutableListOf<WindFarmCluster>()

    while (remaining.isNotEmpty()) {
        val seed = remaining.removeAt(0)
        val members = mutableListOf(seed)
        val iterator = remaining.iterator()
        while (iterator.hasNext()) {
            val candidate = iterator.next()
            if (haversineKm(seed.windFarm.latitude, seed.windFarm.longitude,
                    candidate.windFarm.latitude, candidate.windFarm.longitude) <= radiusKm) {
                members.add(candidate)
                iterator.remove()
            }
        }
        val centerLat = members.map { it.windFarm.latitude }.average()
        val centerLon = members.map { it.windFarm.longitude }.average()
        clusters.add(WindFarmCluster(members, centerLat, centerLon))
    }

    return clusters
}

internal fun zoomToSeparateCluster(cluster: WindFarmCluster): Double {
    if (cluster.previews.size <= 1) return DetailMapZoom
    val lats = cluster.previews.map { it.windFarm.latitude }
    val lons = cluster.previews.map { it.windFarm.longitude }
    val spanKm = haversineKm(lats.min(), lons.min(), lats.max(), lons.max())
    return when {
        spanKm < 5.0 -> 12.0
        spanKm < 20.0 -> 10.0
        spanKm < 60.0 -> 8.0
        else -> 7.0
    }
}

internal fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).let { it * it } +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).let { it * it }
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}
