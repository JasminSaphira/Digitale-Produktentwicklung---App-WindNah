package com.windnah.feature.discover

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.cos
import kotlin.math.sin

/**
 * Renders wind-farm markers (and cluster badges) onto an osmdroid [MapView]. Kept separate from the
 * Discover Compose UI: this is imperative Android-canvas drawing, not @Composable code.
 */
internal fun syncMapMarkers(
    context: Context,
    mapView: MapView,
    windFarms: List<WindFarmPreview>,
    selectedWindFarmId: String?,
    onMarkerClick: (String) -> Unit,
) {
    mapView.overlays.removeAll { it is Marker }

    val zoom = mapView.zoomLevelDouble
    val clusters = clusterWindFarms(windFarms, zoom)

    clusters.forEach { cluster ->
        val isSingle = cluster.previews.size == 1
        val primary = cluster.previews.first()
        val windFarm = primary.windFarm
        val isSelected = isSingle && windFarm.id == selectedWindFarmId

        val badgeText = if (isSingle) {
            windFarm.turbineCount.toString()
        } else {
            cluster.previews.size.toString()
        }

        val marker = Marker(mapView).apply {
            position = GeoPoint(cluster.latitude, cluster.longitude)
            title = if (isSingle) windFarm.name else "${cluster.previews.size} Windparks"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = createMarkerBitmapDrawable(
                context = context,
                status = if (isSingle) windFarm.status else WindFarmStatus.IN_BETRIEB,
                badgeText = badgeText,
                selected = isSelected,
                isCluster = !isSingle,
            )
            setOnMarkerClickListener { _, _ ->
                if (isSingle) {
                    onMarkerClick(windFarm.id)
                } else {
                    val targetZoom = zoomToSeparateCluster(cluster)
                    mapView.controller.animateTo(GeoPoint(cluster.latitude, cluster.longitude))
                    mapView.controller.setZoom(targetZoom)
                }
                true
            }
        }
        mapView.overlays.add(marker)
    }

    mapView.invalidate()
}

private fun createMarkerBitmapDrawable(
    context: Context,
    status: WindFarmStatus,
    badgeText: String,
    selected: Boolean,
    isCluster: Boolean = false,
): android.graphics.drawable.BitmapDrawable {
    val size = when {
        isCluster -> 144
        selected -> 152
        else -> 128
    }
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)

    if (isCluster) {
        val centerX = size / 2f
        val centerY = size / 2f

        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color(0xFF3F6836).toArgb()
            style = Paint.Style.STROKE
            strokeWidth = 6f
            alpha = 80
        }
        canvas.drawCircle(centerX, centerY, centerX - 3f, ringPaint)

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color(0xFF3F6836).toArgb()
        }
        canvas.drawCircle(centerX, centerY, centerX - 10f, fillPaint)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawCircle(centerX, centerY, centerX - 10f, strokePaint)

        val countRect = Rect()
        val countPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = 46f
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
        countPaint.getTextBounds(badgeText, 0, badgeText.length, countRect)
        canvas.drawText(badgeText, centerX, centerY + countRect.height() / 2f, countPaint)
    } else {
        val (fillColor, textColor) = markerColors(status, selected)
        val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fillColor }
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = if (selected) 6f else 4f
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = if (selected) 48f else 40f
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }
        val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            alpha = 235
        }
        val badgeTextColor = fillColor
        val badgeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = badgeTextColor
            textSize = if (selected) 28f else 26f
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }

        val radius = if (selected) 40f else 34f
        val centerX = size / 2f
        val centerY = size / 2f - 8f
        canvas.drawCircle(centerX, centerY, radius, outerPaint)
        canvas.drawCircle(centerX, centerY, radius, strokePaint)
        drawWindTurbineIcon(canvas, centerX, centerY, radius * 0.62f, textColor)

        val badgeRect = Rect()
        badgeTextPaint.getTextBounds(badgeText, 0, badgeText.length, badgeRect)
        val badgeRadius = if (selected) 20f else 18f
        // Badge oben links wie in Figma
        val badgeCenterX = centerX - radius * 0.7f
        val badgeCenterY = centerY - radius * 0.65f
        canvas.drawCircle(badgeCenterX, badgeCenterY, badgeRadius, badgePaint)
        canvas.drawText(badgeText, badgeCenterX, badgeCenterY + badgeRect.height() / 2f, badgeTextPaint)
    }

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

private fun markerColors(status: WindFarmStatus, selected: Boolean): Pair<Int, Int> {
    val (fillColor, textColor) = when (status) {
        WindFarmStatus.IN_BETRIEB -> Color(0xFF4F7650) to Color.White
        WindFarmStatus.IN_WARTUNG -> Color(0xFFF9CD55) to Color(0xFF2E2A12)
        WindFarmStatus.IN_PLANUNG -> Color(0xFF386569) to Color.White
        WindFarmStatus.STILLGELEGT -> Color(0xFF9E9E9E) to Color(0xFF1F1F1F)
    }
    val fill = if (selected) fillColor else fillColor.copy(alpha = 0.92f)
    return fill.toArgb() to textColor.toArgb()
}

private fun drawWindTurbineIcon(canvas: Canvas, cx: Float, cy: Float, iconRadius: Float, color: Int) {
    val bladePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.FILL
    }
    val hubRadius = iconRadius * 0.18f
    val bladeLength = iconRadius * 0.82f
    val bladeWidth = iconRadius * 0.22f

    // 3 Rotorblätter bei 270°, 30°, 150° (oben + links-unten + rechts-unten)
    val angles = listOf(270.0, 30.0, 150.0)
    angles.forEach { angleDeg ->
        val rad = Math.toRadians(angleDeg)
        val perpRad = Math.toRadians(angleDeg + 90.0)
        val tipX = cx + (bladeLength * cos(rad)).toFloat()
        val tipY = cy + (bladeLength * sin(rad)).toFloat()
        val p1x = cx + (hubRadius * cos(perpRad)).toFloat()
        val p1y = cy + (hubRadius * sin(perpRad)).toFloat()
        val p2x = cx - (hubRadius * cos(perpRad)).toFloat()
        val p2y = cy - (hubRadius * sin(perpRad)).toFloat()
        val path = android.graphics.Path().apply {
            moveTo(p1x, p1y)
            lineTo(tipX + (bladeWidth * 0.1f * cos(perpRad)).toFloat(), tipY + (bladeWidth * 0.1f * sin(perpRad)).toFloat())
            lineTo(p2x, p2y)
            close()
        }
        canvas.drawPath(path, bladePaint)
    }

    // Nabe (Mittelpunkt)
    canvas.drawCircle(cx, cy, hubRadius, bladePaint)
}
