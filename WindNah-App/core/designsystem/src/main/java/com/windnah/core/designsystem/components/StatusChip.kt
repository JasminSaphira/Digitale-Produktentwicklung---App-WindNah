package com.windnah.core.designsystem.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.windnah.core.model.WindFarmStatus

@Composable
fun StatusChip(
    status: WindFarmStatus,
    modifier: Modifier = Modifier,
) {
    val (label, containerColor, contentColor) = when (status) {
        WindFarmStatus.IN_BETRIEB -> Triple("In Betrieb", Color(0xFF3F6836), Color.White)
        WindFarmStatus.IN_WARTUNG -> Triple("In Wartung", Color(0xFFF9CD55), Color.Black)
        WindFarmStatus.IN_PLANUNG -> Triple("In Planung", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
        WindFarmStatus.STILLGELEGT -> Triple("Stillgelegt", Color(0xFF9E9E9E), Color.White)
    }

    SuggestionChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        modifier = modifier,
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = containerColor,
            labelColor = contentColor,
        ),
        border = null,
    )
}
