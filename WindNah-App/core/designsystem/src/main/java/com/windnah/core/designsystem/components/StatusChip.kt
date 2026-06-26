package com.windnah.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.windnah.core.model.WindFarmStatus

private data class StatusColors(
    val containerColor: Color,
    val contentColor: Color,
)

@Composable
private fun activeStatusColors(status: WindFarmStatus): StatusColors = when (status) {
    WindFarmStatus.IN_BETRIEB -> StatusColors(Color(0xFF4F7650), Color.White)
    WindFarmStatus.IN_WARTUNG -> StatusColors(Color(0xFFF9CD55), Color(0xFF2E2A12))
    WindFarmStatus.IN_PLANUNG -> StatusColors(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
    WindFarmStatus.STILLGELEGT -> StatusColors(Color(0xFF9E9E9E), Color(0xFF1F1F1F))
}

private val WindFarmStatus.label: String
    get() = when (this) {
        WindFarmStatus.IN_BETRIEB -> "In Betrieb"
        WindFarmStatus.IN_WARTUNG -> "In Wartung"
        WindFarmStatus.IN_PLANUNG -> "In Planung"
        WindFarmStatus.STILLGELEGT -> "Stillgelegt"
    }

@Composable
fun StatusChip(
    status: WindFarmStatus,
    modifier: Modifier = Modifier,
) {
    val colors = activeStatusColors(status)
    SuggestionChip(
        onClick = {},
        label = { Text(status.label, style = MaterialTheme.typography.labelSmall) },
        modifier = modifier,
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = colors.containerColor,
            labelColor = colors.contentColor,
        ),
        border = null,
    )
}

@Composable
fun StatusFilterChip(
    status: WindFarmStatus,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val active = activeStatusColors(status)
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
            )
        },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        } else null,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = active.containerColor,
            selectedLabelColor = active.contentColor,
            selectedLeadingIconColor = active.contentColor,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        border = if (selected) {
            BorderStroke(0.dp, Color.Transparent)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        },
    )
}
