package com.windnah.feature.myturbines

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.windnah.core.designsystem.components.WindNahScreenHeader

private val MyTurbinesFavoriteAccent = Color(0xFFF9CD55)

@Composable
fun MyTurbinesScreen(
    onWindFarmClick: (windFarmId: String) -> Unit = {},
    modifier: Modifier = Modifier,
    onNavigateToMap: () -> Unit = {},
    viewModel: MyTurbinesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyTurbinesScreenContent(
        uiState = uiState,
        modifier = modifier,
        onEvent = { event ->
            when (event) {
                is MyTurbinesUiEvent.WindFarmClicked -> onWindFarmClick(event.windFarmId)
                is MyTurbinesUiEvent.RemoveFavoriteClicked -> viewModel.onEvent(event)
                MyTurbinesUiEvent.DiscoverClicked -> onNavigateToMap()
                MyTurbinesUiEvent.BackClicked -> onNavigateToMap()
                MyTurbinesUiEvent.RetryClicked -> viewModel.onEvent(event)
            }
        },
    )
}

@Composable
private fun MyTurbinesScreenContent(
    uiState: MyTurbinesUiState,
    onEvent: (MyTurbinesUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WindNahScreenHeader(
                title = "Meine Anlagen",
                subtitle = "Favoriten & zuletzt angesehen",
                onBackClick = { onEvent(MyTurbinesUiEvent.BackClicked) },
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                MyTurbinesLoadingState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            uiState.errorMessage != null -> {
                MyTurbinesErrorState(
                    message = uiState.errorMessage,
                    onRetry = { onEvent(MyTurbinesUiEvent.RetryClicked) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            }

            else -> {
                MyTurbinesContent(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun MyTurbinesContent(
    uiState: MyTurbinesUiState,
    onEvent: (MyTurbinesUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        if (uiState.favorites.isEmpty() && uiState.recentlyViewed.isEmpty()) {
            item {
                MyTurbinesEmptyOverviewState(
                    onDiscoverClick = { onEvent(MyTurbinesUiEvent.DiscoverClicked) },
                )
            }
        } else {
            item {
                MyTurbinesSection(
                    title = "Gespeichert",
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = MyTurbinesFavoriteAccent,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    count = uiState.favorites.size,
                    isEmpty = uiState.favorites.isEmpty(),
                    emptyText = "Noch keine Favoriten gespeichert.",
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        uiState.favorites.forEach { item ->
                            FavoriteWindFarmCard(
                                item = item,
                                onClick = { onEvent(MyTurbinesUiEvent.WindFarmClicked(item.id)) },
                                onRemoveFavorite = {
                                    onEvent(MyTurbinesUiEvent.RemoveFavoriteClicked(item.id))
                                },
                            )
                        }
                    }
                }
            }

            item {
                MyTurbinesSection(
                    title = "Zuletzt angesehen",
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    count = null,
                    isEmpty = uiState.recentlyViewed.isEmpty(),
                    emptyText = "Noch keine Windparks angesehen.",
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        uiState.recentlyViewed.forEach { item ->
                            RecentlyViewedCard(
                                item = item,
                                onClick = { onEvent(MyTurbinesUiEvent.WindFarmClicked(item.id)) },
                            )
                        }
                    }
                }
            }
        }

        item {
            MyTurbinesDiscoverButton(
                onClick = { onEvent(MyTurbinesUiEvent.DiscoverClicked) },
            )
        }
    }
}

@Composable
private fun MyTurbinesSection(
    title: String,
    icon: @Composable () -> Unit,
    count: Int?,
    isEmpty: Boolean,
    emptyText: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(
            title = title,
            icon = icon,
            count = count,
        )
        content()
        if (isEmpty) {
            EmptyStateCard(text = emptyText)
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: @Composable () -> Unit,
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
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (count != null) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape,
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun FavoriteWindFarmCard(
    item: MyTurbinesWindFarmItemUiModel,
    onClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
) {
    WindFarmItemCard(
        item = item,
        onClick = onClick,
        trailing = {
            IconButton(onClick = onRemoveFavorite) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Aus Favoriten entfernen",
                    tint = MyTurbinesFavoriteAccent,
                )
            }
        },
    )
}

@Composable
private fun RecentlyViewedCard(
    item: MyTurbinesWindFarmItemUiModel,
    onClick: () -> Unit,
) {
    WindFarmItemCard(
        item = item,
        onClick = onClick,
        trailing = {
            Icon(
                imageVector = Icons.Outlined.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    )
}

@Composable
private fun WindFarmItemCard(
    item: MyTurbinesWindFarmItemUiModel,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                    StatusLabel(
                        label = item.statusLabel,
                        status = item.status,
                    )
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    LocationLabel(text = item.location)
                }

                trailing()
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                MetricChip(
                    icon = Icons.Outlined.FlashOn,
                    label = "CO2",
                    value = item.co2Savings,
                )
                MetricChip(
                    icon = Icons.Outlined.Home,
                    label = "Haushalte",
                    value = item.households,
                )
                MetricChip(
                    icon = Icons.Outlined.Air,
                    label = "Leistung",
                    value = item.capacity,
                )
            }
        }
    }
}

@Composable
private fun StatusLabel(
    label: String,
    status: MyTurbinesStatusUiModel,
) {
    val statusColor = when (status) {
        MyTurbinesStatusUiModel.InOperation -> MaterialTheme.colorScheme.primary
        MyTurbinesStatusUiModel.Maintenance -> MyTurbinesFavoriteAccent
        MyTurbinesStatusUiModel.Planned -> MaterialTheme.colorScheme.tertiary
        MyTurbinesStatusUiModel.Decommissioned -> MaterialTheme.colorScheme.outline
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(statusColor, CircleShape),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LocationLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(12.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RowScope.MetricChip(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
        modifier = Modifier
            .weight(1f)
            .height(60.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
private fun MyTurbinesEmptyOverviewState(
    onDiscoverClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Noch keine gespeicherten Anlagen",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Speichere Windparks als Favorit oder entdecke neue Anlagen auf der Karte.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = onDiscoverClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Explore,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Windparks entdecken")
            }
        }
    }
}

@Composable
private fun MyTurbinesLoadingState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun MyTurbinesErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(text = "Erneut versuchen")
                }
            }
        }
    }
}

@Composable
private fun MyTurbinesDiscoverButton(
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Icon(
            imageVector = Icons.Outlined.Explore,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Neue Windraeder entdecken",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
