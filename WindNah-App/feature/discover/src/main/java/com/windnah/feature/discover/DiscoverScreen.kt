package com.windnah.feature.discover

import android.Manifest
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.windnah.core.designsystem.components.StatusChip
import com.windnah.core.designsystem.components.StatusFilterChip
import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.compose.foundation.layout.widthIn

private val GermanyCenter = GeoPoint(51.1657, 10.4515)
private const val DefaultMapZoom = 6.0
private const val DetailMapZoom = 11.2

private enum class DiscoverMapLayer(val label: String) {
    STANDARD("Standardkarte"),
    TOPOGRAPHIC("Topografisch"),
}

private val OsmTileSource = XYTileSource(
    "OpenStreetMap",
    0, 19, 256, ".png",
    arrayOf(
        "https://a.tile.openstreetmap.org/",
        "https://b.tile.openstreetmap.org/",
        "https://c.tile.openstreetmap.org/",
    ),
    "© OpenStreetMap contributors",
)
private val OpenTopoMapTileSource = XYTileSource(
    "OpenTopoMap",
    0, 17, 256, ".png",
    arrayOf(
        "https://a.tile.opentopomap.org/",
        "https://b.tile.opentopomap.org/",
        "https://c.tile.opentopomap.org/",
    ),
    "Map data: Â© OpenStreetMap contributors, SRTM | Map style: Â© OpenTopoMap",
)

private fun DiscoverMapLayer.tileSource(): XYTileSource = when (this) {
    DiscoverMapLayer.STANDARD -> OsmTileSource
    DiscoverMapLayer.TOPOGRAPHIC -> OpenTopoMapTileSource
}

// TODO: Add satellite/orthophoto layers only when a legal tile provider is available; do not use Google tile URLs directly.
private val FigmaSearchTop = 16.dp
private val FigmaHorizontalStart = 20.dp
private val FigmaHorizontalEnd = 13.dp
private val FigmaSearchHeight = 56.dp
private val FigmaFilterSpacing = 13.dp
private val FigmaFilterHeight = 32.dp
private val FigmaSnackbarBottom = 30.dp

@Composable
fun DiscoverScreen(
    onWindFarmClick: (windFarmId: String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val locationProvider = remember(context) { DiscoverLocationProvider(context) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            viewModel.setLocationUsageEnabled(true)
        }
        viewModel.onEvent(DiscoverUiEvent.LocationPermissionUpdated(granted))
    }

    LaunchedEffect(locationProvider) {
        viewModel.onEvent(
            DiscoverUiEvent.LocationPermissionUpdated(
                granted = locationProvider.hasLocationPermission(),
            ),
        )
    }

    var handledLocationRequestToken by remember { mutableIntStateOf(0) }
    LaunchedEffect(uiState.pendingLocationRequestToken, uiState.hasLocationPermission) {
        val requestToken = uiState.pendingLocationRequestToken
        if (!uiState.hasLocationPermission || requestToken == 0 || requestToken == handledLocationRequestToken) {
            return@LaunchedEffect
        }

        handledLocationRequestToken = requestToken
        locationProvider.requestCurrentLocation(
            onResolved = { latitude, longitude ->
                viewModel.onEvent(DiscoverUiEvent.CurrentLocationResolved(latitude, longitude))
            },
            onUnavailable = {
                viewModel.onEvent(DiscoverUiEvent.CurrentLocationUnavailable)
            },
        )
    }

    DiscoverContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onWindFarmClick = onWindFarmClick,
        onEnableLocationUsageAndRecenter = viewModel::enableLocationUsageAndRecenter,
        onRequestLocationPermission = {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiscoverContent(
    uiState: DiscoverUiState,
    onEvent: (DiscoverUiEvent) -> Unit,
    onWindFarmClick: (windFarmId: String) -> Unit,
    onEnableLocationUsageAndRecenter: () -> Unit,
    onRequestLocationPermission: () -> Unit,
    modifier: Modifier = Modifier,
    showMapView: Boolean = true,
) {
    val hasActiveFilters = uiState.searchQuery.isNotBlank() ||
        uiState.selectedStatuses.isNotEmpty() ||
        uiState.selectedFederalState != null
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedMapLayer by remember { mutableStateOf(DiscoverMapLayer.STANDARD) }
    var showLayerSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        if (showMapView && !LocalInspectionMode.current) {
            OpenStreetMapSurface(
                windFarms = uiState.windFarms,
                selectedWindFarmId = uiState.selectedWindFarm?.windFarm?.id,
                mapRecenterRequest = uiState.mapRecenterRequest,
                selectedMapLayer = selectedMapLayer,
                modifier = Modifier.fillMaxSize(),
                onMarkerClick = { onEvent(DiscoverUiEvent.WindFarmSelected(it)) },
            )
        } else {
            PreviewMapSurface(modifier = Modifier.fillMaxSize())
        }

        DiscoverTopControls(
            uiState = uiState,
            onEvent = onEvent,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(
                    start = FigmaHorizontalStart,
                    top = FigmaSearchTop,
                    end = FigmaHorizontalEnd,
                ),
        )

        // Search suggestions float over the filters/map (not in the layout flow),
        // anchored just below the search field. Hidden once a park is selected.
        val showSuggestions = uiState.searchQuery.isNotBlank() &&
            uiState.selectedWindFarm == null &&
            uiState.windFarms.isNotEmpty()
        if (showSuggestions) {
            SearchSuggestions(
                results = uiState.windFarms,
                query = uiState.searchQuery,
                onSelect = { id -> onEvent(DiscoverUiEvent.WindFarmSelected(id, recenter = true)) },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(
                        start = FigmaHorizontalStart,
                        end = FigmaHorizontalEnd,
                        top = FigmaSearchTop + FigmaSearchHeight + 8.dp,
                    ),
            )
        }

        when {
            uiState.isLoading -> {
                LoadingOverlay(modifier = Modifier.align(Alignment.Center))
            }

            uiState.errorMessage != null -> {
                ErrorOverlay(
                    text = uiState.errorMessage,
                    onRetry = { onEvent(DiscoverUiEvent.RetryClicked) },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                )
            }

            uiState.windFarms.isEmpty() -> {
                EmptyResultsOverlay(
                    hasActiveFilters = hasActiveFilters,
                    onResetFilters = { onEvent(DiscoverUiEvent.ClearFiltersClicked) },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                )
            }
        }

        FloatingMapAction(
            icon = Icons.Outlined.Layers,
            contentDescription = "Kartenansicht wechseln",
            onClick = { showLayerSheet = true },
            containerColor = Color(0xFF3C4B37),
            contentColor = Color.White,
            size = 40.dp,
            shadowElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 190.dp, end = FigmaHorizontalStart),
        )

        BottomOverlay(
            uiState = uiState,
            onRecenter = { onEvent(DiscoverUiEvent.RecenterRequested) },
            onEnableLocationUsageAndRecenter = onEnableLocationUsageAndRecenter,
            onRequestLocationPermission = onRequestLocationPermission,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (uiState.selectedWindFarm != null) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(DiscoverUiEvent.WindFarmSelectionCleared) },
            sheetState = sheetState,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 4.dp)
                            .background(Color(0xFFC3C8BC), RoundedCornerShape(2.dp)),
                    )
                }
            },
            containerColor = Color.White,
            tonalElevation = 8.dp,
        ) {
            SelectedWindFarmSheetContent(
                preview = uiState.selectedWindFarm,
                onClose = { onEvent(DiscoverUiEvent.WindFarmSelectionCleared) },
                onDetailsClick = { onWindFarmClick(uiState.selectedWindFarm.windFarm.id) },
            )
        }
    }

    if (showLayerSheet) {
        MapLayerBottomSheet(
            selectedLayer = selectedMapLayer,
            onLayerSelected = { layer ->
                selectedMapLayer = layer
                showLayerSheet = false
            },
            onDismiss = { showLayerSheet = false },
        )
    }
}

@Composable
private fun DiscoverTopControls(
    uiState: DiscoverUiState,
    onEvent: (DiscoverUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FigmaFilterSpacing),
    ) {
        SearchCard(
            query = uiState.searchQuery,
            onQueryChange = { onEvent(DiscoverUiEvent.SearchQueryChanged(it)) },
            onClearQuery = { onEvent(DiscoverUiEvent.SearchQueryChanged("")) },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FederalStateDropdown(
                selectedFederalState = uiState.selectedFederalState,
                federalStates = uiState.federalStateFilters,
                onFederalStateSelected = { onEvent(DiscoverUiEvent.FederalStateFilterSelected(it)) },
            )

            uiState.statusFilters.forEach { status ->
                StatusFilterChip(
                    status = status,
                    selected = status in uiState.selectedStatuses,
                    onClick = { onEvent(DiscoverUiEvent.StatusFilterToggled(status)) },
                )
            }
        }

        if (uiState.isOfflineData) {
            OfflineDataBanner()
        }
    }
}

@Composable
private fun OfflineDataBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF53634E),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = "Offline – zwischengespeicherte Daten",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun SearchSuggestions(
    results: List<com.windnah.core.model.WindFarmPreview>,
    query: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Rank name-prefix matches first so short queries surface the obvious park,
    // then keep the contains-matches the use case already filtered.
    val q = query.trim().lowercase()
    val shown = results
        .sortedByDescending { it.windFarm.name.lowercase().startsWith(q) }
        .take(6)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.heightIn(max = 280.dp)) {
            shown.forEachIndexed { index, preview ->
                val farm = preview.windFarm
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(farm.id) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = farm.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${farm.municipality}, ${farm.federalState}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (index < shown.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun SearchCard(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(FigmaSearchHeight),
        singleLine = true,
        placeholder = {
            Text(
                text = "Ort oder Postleitzahl suchen",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon = if (query.isNotBlank()) {
            {
                IconButton(onClick = onClearQuery) {
                    Icon(Icons.Outlined.Close, contentDescription = "Suche leeren")
                }
            }
        } else {
            null
        },
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

@Composable
private fun FederalStateDropdown(
    selectedFederalState: String?,
    federalStates: List<String>,
    onFederalStateSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val isSelected = selectedFederalState != null

    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
            },
            border = if (isSelected) null else BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            ),
            shadowElevation = if (isSelected) 0.dp else 2.dp,
        ) {
            Row(
                modifier = Modifier.padding(
                    start = if (isSelected) 8.dp else 12.dp,
                    end = 8.dp,
                    top = 6.dp,
                    bottom = 6.dp,
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                }
                Text(
                    text = selectedFederalState ?: "Bundesland",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    maxLines = 1,
                )
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = null,
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 360.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Alle Bundesländer",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedFederalState == null) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selectedFederalState == null) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                },
                leadingIcon = if (selectedFederalState == null) {
                    {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                } else null,
                onClick = {
                    expanded = false
                    onFederalStateSelected(null)
                },
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            federalStates.forEach { federalState ->
                val isCurrentlySelected = selectedFederalState == federalState
                DropdownMenuItem(
                    text = {
                        Text(
                            text = federalState,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isCurrentlySelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isCurrentlySelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    },
                    leadingIcon = if (isCurrentlySelected) {
                        {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    } else null,
                    onClick = {
                        expanded = false
                        onFederalStateSelected(federalState)
                    },
                )
            }
        }
    }
}

@Composable
private fun LoadingOverlay(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        tonalElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
            Column {
                Text("Windparks werden geladen", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Die Karte wird mit aktuellen Filtern aktualisiert.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ErrorOverlay(
    text: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.96f),
        tonalElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Hinweis",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            if (onRetry != null) {
                androidx.compose.material3.TextButton(onClick = onRetry) {
                    Text(
                        text = "Erneut versuchen",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyResultsOverlay(
    hasActiveFilters: Boolean,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Explore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = "Keine Windparks gefunden",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = if (hasActiveFilters) {
                    "Passe Suche oder Filter an, um wieder Windparks auf der Karte zu sehen."
                } else {
                    "Aktuell liegen fuer diese Ansicht keine Windparks vor."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            if (hasActiveFilters) {
                Button(onClick = onResetFilters) {
                    Text("Filter zuruecksetzen")
                }
            }
        }
    }
}

@Composable
private fun BottomOverlay(
    uiState: DiscoverUiState,
    onRecenter: () -> Unit,
    onEnableLocationUsageAndRecenter: () -> Unit,
    onRequestLocationPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMarkerHint by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FigmaHorizontalStart, vertical = 20.dp),
    ) {
        if (uiState.windFarms.isNotEmpty() && showMarkerHint) {
            MarkerHintSnackbar(
                text = "Marker antippen für Details",
                onDismiss = { showMarkerHint = false },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = FigmaSnackbarBottom),
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End,
        ) {
            FloatingMapAction(
                icon = Icons.Outlined.MyLocation,
                contentDescription = "Auf meinen Standort zentrieren",
                onClick = {
                    if (!uiState.hasLocationPermission) {
                        onRequestLocationPermission()
                    } else if (!uiState.isLocationUsageEnabled) {
                        onEnableLocationUsageAndRecenter()
                    } else {
                        onRecenter()
                    }
                },
                enabled = !uiState.isResolvingCurrentLocation,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                isLoading = uiState.isResolvingCurrentLocation,
            )
        }
    }
}

@Composable
private fun MarkerHintSnackbar(
    text: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .widthIn(min = 181.dp, max = 280.dp)
            .height(32.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FloatingMapAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    size: androidx.compose.ui.unit.Dp = 56.dp,
    shadowElevation: androidx.compose.ui.unit.Dp = 8.dp,
) {
    Surface(
        modifier = modifier.alpha(if (enabled) 1f else 0.78f),
        shape = CircleShape,
        color = containerColor,
        shadowElevation = shadowElevation,
        tonalElevation = shadowElevation,
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled && !isLoading,
            modifier = Modifier.size(size),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.5.dp,
                    color = contentColor,
                )
            } else {
                Icon(icon, contentDescription = contentDescription, tint = contentColor)
            }
        }
    }
}

@Composable
private fun SelectedWindFarmSheetContent(
    preview: WindFarmPreview,
    onClose: () -> Unit,
    onDetailsClick: () -> Unit,
) {
    val windFarm = preview.windFarm
    val metrics = preview.energyMetrics

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                WindFarmHeroThumbnail()
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = windFarm.name,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = onClose) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "Vorschau schliessen",
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = "${windFarm.municipality}, ${windFarm.federalState}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        StatusChip(status = windFarm.status)
                        Text(
                            text = "${windFarm.turbineCount} Windräder",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(19.dp),
            ) {
                MetricCapsule(
                    title = "Nennleistung",
                    value = "${formatMw(windFarm.totalCapacityKw)} MW",
                    modifier = Modifier.weight(1f),
                )
                MetricCapsule(
                    title = "Haushalte versorgt",
                    value = formatInt(metrics.householdsSupplied),
                    modifier = Modifier.weight(1f),
                )
                MetricCapsule(
                    title = "CO2 gespart/Jahr",
                    value = "${formatInt(metrics.co2SavingsTonnesPerYear.roundToInt())} t",
                    modifier = Modifier.weight(1f),
                )
            }

        Button(
            onClick = onDetailsClick,
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(100.dp),
        ) {
            Text("Details ansehen")
        }
    }
}

@Composable
private fun WindFarmHeroThumbnail(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 118.dp, height = 106.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFB8D4A8), Color(0xFF6B9B50)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Air,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(48.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapLayerBottomSheet(
    selectedLayer: DiscoverMapLayer,
    onLayerSelected: (DiscoverMapLayer) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        tonalElevation = 3.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Kartentyp",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Wähle die passende Kartenansicht für Windparks in deiner Umgebung.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MapLayerOptionCard(
                    label = "Standard",
                    description = "Straßen & Orte",
                    selected = selectedLayer == DiscoverMapLayer.STANDARD,
                    icon = Icons.Outlined.Map,
                    onClick = { onLayerSelected(DiscoverMapLayer.STANDARD) },
                    modifier = Modifier.weight(1f),
                )

                MapLayerOptionCard(
                    label = "Topografisch",
                    description = "Gelände & Höhen",
                    selected = selectedLayer == DiscoverMapLayer.TOPOGRAPHIC,
                    icon = Icons.Outlined.Terrain,
                    onClick = { onLayerSelected(DiscoverMapLayer.TOPOGRAPHIC) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun MapLayerOptionCard(
    label: String,
    description: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val activeColor = colorScheme.primary
    val contentColor = if (selected) activeColor else colorScheme.onSurface

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = if (selected) activeColor.copy(alpha = 0.08f) else colorScheme.surface,
        contentColor = contentColor,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) activeColor.copy(alpha = 0.45f) else colorScheme.outlineVariant,
        ),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(28.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    maxLines = 1,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }

            if (selected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        tint = activeColor,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "Aktiv",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = activeColor,
                    )
                }
            } else {
                Text(
                    text = "Auswählen",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MetricCapsule(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color(0x1AC0EFB0),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private data class MapViewState(val recenterToken: Int, val windFarms: List<WindFarmPreview>, val selectedId: String?)

@Composable
private fun OpenStreetMapSurface(
    windFarms: List<WindFarmPreview>,
    selectedWindFarmId: String?,
    mapRecenterRequest: MapRecenterRequest?,
    selectedMapLayer: DiscoverMapLayer,
    onMarkerClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val windFarmsRef = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(windFarms) }
    val selectedIdRef = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(selectedWindFarmId) }
    windFarmsRef.value = windFarms
    selectedIdRef.value = selectedWindFarmId

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            Configuration.getInstance().apply {
                userAgentValue = "${ctx.packageName}/1.0 (Android; osmdroid)"
                osmdroidBasePath = java.io.File(ctx.cacheDir, "osmdroid")
                osmdroidTileCache = java.io.File(ctx.cacheDir, "osmdroid/tiles")
            }

            MapView(ctx).apply {
                setTileSource(selectedMapLayer.tileSource())
                setMultiTouchControls(true)
                controller.setZoom(DefaultMapZoom)
                controller.setCenter(GermanyCenter)
                minZoomLevel = 4.0
                maxZoomLevel = 18.0
                setTilesScaledToDpi(true)
                addMapListener(object : MapListener {
                    override fun onScroll(event: ScrollEvent?) = false
                    override fun onZoom(event: ZoomEvent?): Boolean {
                        syncMapMarkers(ctx, this@apply, windFarmsRef.value, selectedIdRef.value, onMarkerClick)
                        return false
                    }
                })
            }
        },
        update = { mapView ->
            val lastHandledRecenter = mapView.getTag() as? Int ?: 0
            mapView.setTileSource(selectedMapLayer.tileSource())

            syncMapMarkers(
                context = context,
                mapView = mapView,
                windFarms = windFarms,
                selectedWindFarmId = selectedWindFarmId,
                onMarkerClick = onMarkerClick,
            )

            when {
                selectedWindFarmId != null -> {
                    windFarms.firstOrNull { it.windFarm.id == selectedWindFarmId }?.windFarm?.let { windFarm ->
                        mapView.controller.animateTo(GeoPoint(windFarm.latitude, windFarm.longitude))
                        mapView.controller.setZoom(DetailMapZoom)
                    }
                }

                mapRecenterRequest != null && mapRecenterRequest.requestToken > lastHandledRecenter -> {
                    mapView.controller.animateTo(GeoPoint(mapRecenterRequest.latitude, mapRecenterRequest.longitude))
                    mapView.controller.setZoom(mapRecenterRequest.zoom)
                    mapView.setTag(mapRecenterRequest.requestToken)
                }

                windFarms.isNotEmpty() && mapView.mapCenter == GermanyCenter -> {
                    mapView.controller.setCenter(GermanyCenter)
                }
            }
        },
    )
}

private data class WindFarmCluster(
    val previews: List<WindFarmPreview>,
    val latitude: Double,
    val longitude: Double,
)

private fun clusterWindFarms(
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

private fun zoomToSeparateCluster(cluster: WindFarmCluster): Double {
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

private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).let { it * it } +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).let { it * it }
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}

private fun syncMapMarkers(
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

@Composable
private fun PreviewMapSurface(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFD7E9C7),
                    Color(0xFFC6E0B0),
                    Color(0xFFE9E7D1),
                ),
                start = Offset.Zero,
                end = Offset(1200f, 1800f),
            ),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color(0xFFCFE7BF))
                .align(Alignment.TopCenter),
        )
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .offset(x = (24 + index * 50).dp, y = (220 + (index % 3) * 86).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (index == 0) Color(0xFF2F5C2E) else Color(0xFF4F7D44)),
            )
        }
        Text(
            text = "OpenStreetMap Preview",
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
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

private fun formatMw(totalCapacityKw: Double): String = String.format("%.1f", totalCapacityKw / 1000.0)

private fun formatInt(value: Int): String = "%,d".format(value)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DiscoverContentPreview() {
    MaterialTheme {
        DiscoverContent(
            uiState = DiscoverUiState(
                isLoading = false,
                searchQuery = "Wendischbora",
                selectedStatuses = setOf(WindFarmStatus.IN_BETRIEB, WindFarmStatus.IN_WARTUNG),
                selectedFederalState = "Sachsen",
                windFarms = previewWindFarms,
                selectedWindFarm = previewWindFarms.first(),
                federalStateFilters = previewWindFarms.map { it.windFarm.federalState }.distinct(),
                hasLocationPermission = false,
            ),
            onEvent = {},
            onWindFarmClick = {},
            onEnableLocationUsageAndRecenter = {},
            onRequestLocationPermission = {},
            showMapView = false,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun DiscoverContentEmptyPreview() {
    MaterialTheme {
        DiscoverContent(
            uiState = DiscoverUiState(
                isLoading = false,
                searchQuery = "99999",
                selectedStatuses = setOf(WindFarmStatus.IN_PLANUNG),
                selectedFederalState = "Bayern",
                windFarms = emptyList(),
                selectedWindFarm = null,
                hasLocationPermission = true,
            ),
            onEvent = {},
            onWindFarmClick = {},
            onEnableLocationUsageAndRecenter = {},
            onRequestLocationPermission = {},
            showMapView = false,
        )
    }
}

private val previewWindFarms = listOf(
    WindFarmPreview(
        windFarm = WindFarm(
            id = "preview-windpark",
            name = "Windpark Wendischbora",
            municipality = "Wendischbora",
            federalState = "Sachsen",
            latitude = 51.0808,
            longitude = 13.2792,
            status = WindFarmStatus.IN_BETRIEB,
            turbineCount = 6,
            totalCapacityKw = 29_800.0,
            commissioningYear = 2019,
            postalCode = "01683",
        ),
        energyMetrics = EnergyMetrics(
            estimatedCurrentOutputKw = 18_500.0,
            estimatedAnnualProductionKwh = 92_000_000.0,
            householdsSupplied = 8_670,
            co2SavingsTonnesPerYear = 18_200.0,
            localEnergyContributionPercent = null,
            municipalRevenueEurPerYear = null,
        ),
    ),
    WindFarmPreview(
        windFarm = WindFarm(
            id = "preview-windpark-2",
            name = "Windpark Taucha",
            municipality = "Taucha",
            federalState = "Sachsen",
            latitude = 51.3862,
            longitude = 12.4938,
            status = WindFarmStatus.IN_WARTUNG,
            turbineCount = 4,
            totalCapacityKw = 18_200.0,
            commissioningYear = 2017,
            postalCode = "04425",
        ),
        energyMetrics = EnergyMetrics(
            estimatedCurrentOutputKw = 11_000.0,
            estimatedAnnualProductionKwh = 58_000_000.0,
            householdsSupplied = 5_460,
            co2SavingsTonnesPerYear = 11_900.0,
            localEnergyContributionPercent = null,
            municipalRevenueEurPerYear = null,
        ),
    ),
)
