package com.windnah.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Co2
import androidx.compose.material.icons.outlined.EnergySavingsLeaf
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WindPower
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WindFarm
import java.util.Locale

@Composable
fun WindFarmPreviewCard(
    windFarm: WindFarm,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier,
    energyMetrics: EnergyMetrics? = null,
) {
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = windFarm.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "${windFarm.municipality}, ${windFarm.federalState}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                StatusChip(
                    status = windFarm.status,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PreviewMetricRow(
                    icon = {
                        Icon(
                            Icons.Outlined.WindPower,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    text = "${windFarm.turbineCount} Anlagen",
                )
                PreviewMetricRow(
                    icon = {
                        Icon(
                            Icons.Outlined.EnergySavingsLeaf,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    text = "${String.format(Locale.GERMANY, "%.1f", windFarm.totalCapacityKw / 1000)} MW installiert",
                )
                if (energyMetrics != null) {
                    PreviewMetricRow(
                        icon = {
                            Icon(
                                Icons.Outlined.Home,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        text = "${String.format(Locale.GERMANY, "%,d", energyMetrics.householdsSupplied)} Haushalte versorgt",
                    )
                    PreviewMetricRow(
                        icon = {
                            Icon(
                                Icons.Outlined.Co2,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        text = "${String.format(Locale.GERMANY, "%,.0f", energyMetrics.co2SavingsTonnesPerYear)} t CO2/Jahr eingespart",
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDetailsClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Details ansehen")
            }
        }
    }
}

@Composable
private fun PreviewMetricRow(
    icon: @Composable () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
