package com.windnah.feature.myturbines

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.windnah.core.designsystem.components.WindNahScreenHeader
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun MyTurbinesScreen(
    onWindFarmClick: (windFarmId: String) -> Unit = {},
    modifier: Modifier = Modifier,
    onNavigateToMap: () -> Unit = {},
    viewModel: MyTurbinesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WindNahScreenHeader(
                title = "Meine Anlagen",
                subtitle = "Favoriten & zuletzt angesehen",
                onBackClick = onNavigateToMap,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            item {
                FavoritesSection(
                    favorites = uiState.favorites,
                    onWindFarmClick = onWindFarmClick,
                    onRemoveFavorite = viewModel::removeFavorite,
                )
            }

            item {
                RecentlyViewedSection(
                    recentlyViewed = uiState.recentlyViewed,
                    onWindFarmClick = onWindFarmClick,
                )
            }

            item {
                DiscoverButton(onClick = onNavigateToMap)
            }
        }
    }
}

@Composable
private fun FavoritesSection(
    favorites: List<WindFarmPreview>,
    onWindFarmClick: (String) -> Unit,
    onRemoveFavorite: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = FavoriteYellow,
                    modifier = Modifier.size(20.dp),
                )
            },
            title = "Gespeichert",
            count = favorites.size,
        )

        if (favorites.isEmpty()) {
            EmptyStateCard(text = "Noch keine Favoriten gespeichert.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                favorites.forEach { preview ->
                    FavoriteWindFarmCard(
                        preview = preview,
                        onClick = { onWindFarmClick(preview.windFarm.id) },
                        onRemoveFavorite = { onRemoveFavorite(preview.windFarm.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentlyViewedSection(
    recentlyViewed: List<WindFarmPreview>,
    onWindFarmClick: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            },
            title = "Zuletzt angesehen",
            count = null,
        )

        if (recentlyViewed.isEmpty()) {
            EmptyStateCard(text = "Noch keine Windparks angesehen.")
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = myTurbinesCardColor()),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column {
                    recentlyViewed.forEachIndexed { index, preview ->
                        RecentlyViewedRow(
                            preview = preview,
                            onClick = { onWindFarmClick(preview.windFarm.id) },
                        )
                        if (index != recentlyViewed.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: @Composable () -> Unit,
    title: String,
    count: Int?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MyTurbinesTextPrimary,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (count != null) {
            Surface(
                color = Color(0xFFF0FAF3),
                shape = CircleShape,
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D6A4F),
                )
            }
        }
    }
}

@Composable
private fun FavoriteWindFarmCard(
    preview: WindFarmPreview,
    onClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
) {
    val windFarm = preview.windFarm
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.elevatedCardColors(containerColor = myTurbinesCardColor()),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    StatusLabel(status = windFarm.status)
                    Text(
                        text = windFarm.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MyTurbinesTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    LocationLabel(text = "${windFarm.municipality}, ${windFarm.federalState}")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onRemoveFavorite,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Aus Favoriten entfernen",
                            tint = FavoriteYellow,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CompactMetricChip(
                    icon = Icons.Outlined.FlashOn,
                    label = "CO2",
                    value = "${formatKilotonnes(preview.energyMetrics.co2SavingsTonnesPerYear)}kt/a",
                    containerColor = Color(0xFFF0FAF3),
                )
                CompactMetricChip(
                    icon = Icons.Outlined.Home,
                    label = "Haushalte",
                    value = formatCompactNumber(preview.energyMetrics.householdsSupplied),
                    containerColor = Color(0xFFEFF6FF),
                )
                CompactMetricChip(
                    icon = Icons.Outlined.Air,
                    label = "Leistung",
                    value = formatMegawatts(windFarm.totalCapacityKw),
                    containerColor = Color(0xFFF5F8F6),
                )
            }
        }
    }
}

@Composable
private fun RecentlyViewedRow(
    preview: WindFarmPreview,
    onClick: () -> Unit,
) {
    val windFarm = preview.windFarm
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFF0FAF3), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Air,
                contentDescription = null,
                tint = Color(0xFF2D6A4F),
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = windFarm.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MyTurbinesTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${windFarm.municipality} · ${formatMegawatts(windFarm.totalCapacityKw)}",
                style = MaterialTheme.typography.bodySmall,
                color = MyTurbinesTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(statusColor(windFarm.status), CircleShape),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun StatusLabel(status: WindFarmStatus) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(statusColor(status), CircleShape),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = status.label,
            style = MaterialTheme.typography.bodySmall,
            color = MyTurbinesTextSecondary,
        )
    }
}

@Composable
private fun LocationLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = MyTurbinesTextSecondary,
            modifier = Modifier.size(12.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MyTurbinesTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CompactMetricChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    containerColor: Color,
) {
    Row(
        modifier = Modifier
            .height(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(containerColor)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF2D6A4F),
            modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MyTurbinesTextSecondary,
                maxLines = 1,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MyTurbinesTextPrimary,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun EmptyStateCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = myTurbinesCardColor()),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DiscoverButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(36.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Explore,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Neue Windräder entdecken",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun myTurbinesCardColor(): Color =
    if (MaterialTheme.colorScheme.background.luminance() > 0.5f) {
        Color.White
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

private fun formatKilotonnes(tonnes: Double): String =
    String.format(Locale.GERMANY, "%.1f", tonnes / 1_000.0)

private fun formatCompactNumber(value: Int): String =
    if (value >= 1_000) {
        String.format(Locale.GERMANY, "%.1fk", value / 1_000.0)
    } else {
        value.toString()
    }

private fun formatMegawatts(totalCapacityKw: Double): String =
    String.format(Locale.GERMANY, "%.1f MW", totalCapacityKw / 1_000.0)

private fun statusColor(status: WindFarmStatus): Color = when (status) {
    WindFarmStatus.IN_BETRIEB -> Color(0xFF059669)
    WindFarmStatus.IN_WARTUNG -> Color(0xFFF9CD55)
    WindFarmStatus.IN_PLANUNG -> Color(0xFF386569)
    WindFarmStatus.STILLGELEGT -> Color(0xFF8E8E8E)
}

private val WindFarmStatus.label: String
    get() = when (this) {
        WindFarmStatus.IN_BETRIEB -> "in Betrieb"
        WindFarmStatus.IN_WARTUNG -> "in Wartung"
        WindFarmStatus.IN_PLANUNG -> "in Planung"
        WindFarmStatus.STILLGELEGT -> "stillgelegt"
    }

private val FavoriteYellow = Color(0xFFF9CD55)
private val MyTurbinesTextPrimary = Color(0xFF1A2E24)
private val MyTurbinesTextSecondary = Color(0xFF5A7068)
