package com.windnah.core.model

/**
 * Wind farm previews together with where they came from, so the UI can show a subtle
 * "offline — cached data" hint when a background refresh failed but cached data is shown.
 */
data class WindFarmPreviewsResult(
    val previews: List<WindFarmPreview>,
    val isStale: Boolean,
)
