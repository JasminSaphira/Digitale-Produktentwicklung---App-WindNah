package com.windnah.feature.windparkdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.windnah.core.designsystem.components.TransparencyInfoUiModel
import com.windnah.core.designsystem.components.WindNahTransparencyBottomSheet
import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WeatherData
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmDetail
import com.windnah.core.model.WindFarmStatus
import com.windnah.core.model.WindTurbine
import com.windnah.core.domain.usecase.WindFarmMetricTransparency
import kotlin.math.log10
import kotlin.math.roundToInt

@Composable
fun WindFarmDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: WindFarmDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is WindFarmDetailUiState.Loading -> WindFarmDetailLoading()
        is WindFarmDetailUiState.NotFound -> WindFarmDetailNotFound(onNavigateBack)
        is WindFarmDetailUiState.Success -> WindFarmDetailContent(
            detail = state.detail,
            metricVisibility = state.metricVisibility,
            isFavorite = state.isFavorite,
            selectedTransparencyInfo = state.selectedTransparencyInfo,
            onEvent = viewModel::onEvent,
            onFavoriteClick = viewModel::toggleFavorite,
            onNavigateBack = onNavigateBack,
        )
    }
}

@Composable
private fun WindFarmDetailLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color(0xFF3F6836))
    }
}

@Composable
private fun WindFarmDetailNotFound(onNavigateBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Windpark nicht gefunden", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Zurück")
            }
        }
    }
}

@Composable
private fun WindFarmDetailContent(
    detail: WindFarmDetail,
    metricVisibility: MetricVisibilityPreferences,
    isFavorite: Boolean,
    selectedTransparencyInfo: TransparencyInfoUiModel?,
    onEvent: (WindFarmDetailUiEvent) -> Unit,
    onFavoriteClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Übersicht", "Windräder Details")

    // Edge-to-edge: no Scaffold insets on the header, we handle them manually
    Scaffold(
        containerColor = Color(0xFFF8FBF1),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            // Header extends behind status bar
            WindFarmHeader(
                windFarm = detail.windFarm,
                isFavorite = isFavorite,
                onFavoriteClick = onFavoriteClick,
                onNavigateBack = onNavigateBack,
            )

            // White tab row with divider, exactly like Figma
            Column {
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF3F6836),
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                )
                            },
                            selectedContentColor = Color(0xFF3F6836),
                            unselectedContentColor = Color(0xFF43483F),
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> UebersichtTab(
                    windFarm = detail.windFarm,
                    metrics = detail.energyMetrics,
                    weather = detail.weather,
                    metricVisibility = metricVisibility,
                    onInfoClick = { metric ->
                        onEvent(WindFarmDetailUiEvent.TransparencyInfoClicked(metric))
                    },
                )
                1 -> WindraederDetailsTab(
                    turbines = detail.turbines,
                    onSizeComparisonInfoClick = {
                        onEvent(WindFarmDetailUiEvent.TransparencyInfoClicked(WindFarmDetailMetric.SizeComparison))
                    },
                )
            }
        }
    }

    selectedTransparencyInfo?.let { info ->
        WindNahTransparencyBottomSheet(
            info = info,
            onDismiss = { onEvent(WindFarmDetailUiEvent.TransparencyInfoDismissed) },
        )
    }
}

@Composable
private fun WindFarmHeader(
    windFarm: WindFarm,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(208.dp),
    ) {
        // Background image
        Image(
            painter = painterResource(R.drawable.windpark_header),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        // Gradient overlay — subtle at top, darker at bottom for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color(0x22000000),
                            0.6f to Color(0x44000000),
                            1f to Color(0xBB000000),
                        ),
                    ),
                ),
        )

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp),
        ) {
            // 32dp tinted circle (Figma), but the IconButton keeps its 48dp touch target.
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0x66191D17), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Zurueck",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onFavoriteClick) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (isFavorite) Color(0xCC191D17) else Color(0x66191D17),
                            shape = RoundedCornerShape(16.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (isFavorite) {
                            "Aus Favoriten entfernen"
                        } else {
                            "Als Favorit speichern"
                        },
                        tint = if (isFavorite) Color(0xFFF9CD55) else Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            IconButton(onClick = {}) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0x66191D17), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Teilen",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        // Name and location at bottom-left of header
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 16.dp, end = 80.dp),
        ) {
            Text(
                text = windFarm.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = "${windFarm.municipality}, ${windFarm.federalState}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun UebersichtTab(
    windFarm: WindFarm,
    metrics: EnergyMetrics,
    weather: WeatherData?,
    metricVisibility: MetricVisibilityPreferences,
    onInfoClick: (WindFarmDetailMetric) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBF1))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (metricVisibility.showLiveOutput) {
            OutputCard(
                windFarm = windFarm,
                metrics = metrics,
                onInfoClick = { onInfoClick(WindFarmDetailMetric.CurrentOutput) },
            )
        }
        WindstaerkeCard(
            weather = weather,
            onInfoClick = { onInfoClick(WindFarmDetailMetric.WindSpeed) },
        )
        MetricsGrid(
            metrics = metrics,
            metricVisibility = metricVisibility,
            onInfoClick = onInfoClick,
        )
        KommunaleEinnahmenCard(
            windFarm = windFarm,
            metrics = metrics,
            onInfoClick = { onInfoClick(WindFarmDetailMetric.MunicipalRevenue) },
        )
        NoiseEstimateCard(
            metrics = metrics,
            onInfoClick = { onInfoClick(WindFarmDetailMetric.NoiseEstimate) },
        )
    }
}

@Composable
private fun OutputCard(
    windFarm: WindFarm,
    metrics: EnergyMetrics,
    onInfoClick: () -> Unit,
) {
    val ratio = if (windFarm.totalCapacityKw > 0.0) {
        (metrics.estimatedCurrentOutputKw / windFarm.totalCapacityKw).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    val pct = if (windFarm.totalCapacityKw > 0.0) {
        (metrics.estimatedCurrentOutputKw / windFarm.totalCapacityKw * 100).roundToInt().coerceIn(0, 100)
    } else {
        0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 17.dp, vertical = 17.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Status chip + info icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusChip(status = windFarm.status)
                MetricInfoButton(
                    contentDescription = "Transparenz zur aktuellen Leistung anzeigen",
                    tint = Color(0xFF73796E),
                    onClick = onInfoClick,
                )
            }

            // MW value + progress bar side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        text = formatMwLarge(metrics.estimatedCurrentOutputKw),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191D17),
                    )
                    Text(
                        text = "aktueller Output · $pct% der Kapazität",
                        fontSize = 12.sp,
                        color = Color(0xFF73796E),
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .width(104.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF3F6836),
                        trackColor = Color(0x293F6836),
                    )
                    Text(
                        text = "${formatDecimal(windFarm.totalCapacityKw / 1000.0, 1)} MW max.",
                        fontSize = 10.sp,
                        color = Color(0xFFC3C8BC),
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

@Composable
private fun WindstaerkeCard(
    weather: WeatherData?,
    onInfoClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x1A386569), RoundedCornerShape(16.dp))
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header row: icon + title + info icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Air,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF386569),
                    )
                    Text(
                        text = "Aktuelle Windstärke",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Color(0xFF386569),
                    )
                }
                MetricInfoButton(
                    contentDescription = "Transparenz zur Windgeschwindigkeit anzeigen",
                    tint = Color(0xFF386569),
                    onClick = onInfoClick,
                )
            }

            // Wind value left, classification right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (weather != null)
                        formatDecimal(weather.windSpeedMs, 1) + " m/s"
                    else "– m/s",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E4D51),
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (weather != null)
                            windClassification(weather.windSpeedMs)
                        else "Keine Winddaten",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF386569),
                    )
                    Text(
                        text = if (weather != null)
                            "Ø ${formatDecimal(weather.windSpeedMs, 1)} m/s pro Jahr"
                        else "Ø 5,5 m/s pro Jahr",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFF386569),
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricsGrid(
    metrics: EnergyMetrics,
    metricVisibility: MetricVisibilityPreferences,
    onInfoClick: (WindFarmDetailMetric) -> Unit,
) {
    val metricCards = buildList {
        add(
            MetricCardData(
                metric = WindFarmDetailMetric.AnnualProduction,
                icon = Icons.Outlined.Bolt,
                label = "Stromproduktion/\nJahr",
                value = "${formatGwh(metrics.estimatedAnnualProductionKwh)} GWh",
                subtext = "${formatMwh(metrics.estimatedAnnualProductionKwh)} MWh",
            ),
        )
        if (metricVisibility.showHouseholds) {
            add(
                MetricCardData(
                    metric = WindFarmDetailMetric.Households,
                    icon = Icons.Outlined.Home,
                    label = "Haushalte\nversorgt",
                    value = formatNumber(metrics.householdsSupplied),
                    subtext = "Haushalte/Jahr",
                ),
            )
        }
        if (metricVisibility.showCo2Savings) {
            add(
                MetricCardData(
                    metric = WindFarmDetailMetric.Co2Savings,
                    icon = Icons.Outlined.Eco,
                    label = "CO2 gespart",
                    value = "${formatNumber(metrics.co2SavingsTonnesPerYear.roundToInt())} t",
                    subtext = "ca. ${formatNumber((metrics.co2SavingsTonnesPerYear / 4.7).roundToInt())} Pkw, die ein Jahr lang nicht fahren",
                ),
            )
        }
        add(
            MetricCardData(
                metric = WindFarmDetailMetric.LocalEnergyContribution,
                icon = Icons.Outlined.BarChart,
                label = "Lokaler Anteil",
                value = metrics.localEnergyContributionPercent
                    ?.let { "${it.roundToInt()} %" }
                    ?: "-",
                subtext = "des kommunalen\nVerbrauchs",
            ),
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        metricCards.chunked(2).forEach { rowCards ->
            Row(horizontalArrangement = Arrangement.spacedBy(21.dp)) {
                rowCards.forEach { card ->
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        icon = card.icon,
                        label = card.label,
                        value = card.value,
                        subtext = card.subtext,
                        onInfoClick = { onInfoClick(card.metric) },
                    )
                }
                if (rowCards.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

private data class MetricCardData(
    val metric: WindFarmDetailMetric,
    val icon: ImageVector,
    val label: String,
    val value: String,
    val subtext: String,
)

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    subtext: String,
    onInfoClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Icon + label row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0x29C0EFB0), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF3F6836),
                    )
                }
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    color = Color(0xFF73796E),
                    lineHeight = 16.sp,
                )
                MetricInfoButton(
                    contentDescription = "Transparenz zu ${label.replace("\n", " ")} anzeigen",
                    tint = Color(0xFF73796E),
                    onClick = onInfoClick,
                )
            }
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191D17),
            )
            Text(
                text = subtext,
                fontSize = 11.sp,
                color = Color(0xFF73796E),
                lineHeight = 14.sp,
            )
        }
    }
}

@Composable
private fun KommunaleEinnahmenCard(
    windFarm: WindFarm,
    metrics: EnergyMetrics,
    onInfoClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x1A3F6836), RoundedCornerShape(16.dp))
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            // Title row with icon + info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Apartment,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF53634E),
                    )
                    Text(
                        text = "Kommunale Einnahmen / Jahr",
                        fontSize = 12.sp,
                        color = Color(0xFF53634E),
                        fontWeight = FontWeight.Medium,
                    )
                }
                MetricInfoButton(
                    contentDescription = "Transparenz zu kommunalen Einnahmen anzeigen",
                    tint = Color(0xFF53634E),
                    onClick = onInfoClick,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = metrics.municipalRevenueEurPerYear
                    ?.let { "${formatEur(it.roundToInt())} €" }
                    ?: "–",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF285021),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "für die Gemeinde ${windFarm.municipality} · durch Gewerbesteuer und Pachteinnahmen",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF53634E),
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun NoiseEstimateCard(
    metrics: EnergyMetrics,
    onInfoClick: () -> Unit,
) {
    var selectedDistanceIndex by remember { mutableIntStateOf(DEFAULT_NOISE_DISTANCE_INDEX) }
    val selectedDistanceM = noiseSimulationDistancesM[selectedDistanceIndex]
    val estimatedNoiseDbA = metrics.estimatedNoiseLevelDbA?.let {
        estimateNoiseForDistance(referenceNoiseDbA = it, distanceM = selectedDistanceM)
    }
    val displayedNoiseDbA = estimatedNoiseDbA?.roundToInt()
    val comparisonText = estimatedNoiseDbA?.let(::noiseComparisonText)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0x29C0EFB0), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Air,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF3F6836),
                        )
                    }
                    Text(
                        text = "Lärmschätzung",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF73796E),
                    )
                }
                MetricInfoButton(
                    contentDescription = "Transparenz zur Laermschaetzung anzeigen",
                    tint = Color(0xFF53634E),
                    onClick = onInfoClick,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = displayedNoiseDbA?.let { "$it dB(A)" } ?: "– dB(A)",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191D17),
                    lineHeight = 30.sp,
                )
                Text(
                    text = comparisonText ?: "Wähle eine Entfernung, sobald aktuelle Wetterdaten verfügbar sind.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF53634E),
                    lineHeight = 16.sp,
                )
          
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Slider(
                    value = selectedDistanceIndex.toFloat(),
                    onValueChange = { value ->
                        selectedDistanceIndex = value.roundToInt()
                            .coerceIn(0, noiseSimulationDistancesM.lastIndex)
                    },
                    valueRange = 0f..noiseSimulationDistancesM.lastIndex.toFloat(),
                    steps = noiseSimulationDistancesM.size - 2,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    noiseSimulationDistancesM.forEach { distanceM ->
                        Text(
                            text = "$distanceM m",
                            fontSize = 10.sp,
                            color = if (distanceM == selectedDistanceM) {
                                Color(0xFF3F6836)
                            } else {
                                Color(0xFF9AA290)
                            },
                            fontWeight = if (distanceM == selectedDistanceM) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            },
                        )
                    }
                }
            }

            Text(
                text = WindFarmMetricTransparency.NOISE,
                fontSize = 10.sp,
                color = Color(0xFF9AA290),
                lineHeight = 13.sp,
            )
        }
    }
}

@Composable
private fun TransparencySummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Transparenz",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191D17),
            )
            Text(
                text = "Jede Kennzahl ist ein geschatzter Wert mit klarer Formel und Quelle.",
                fontSize = 11.sp,
                color = Color(0xFF73796E),
                lineHeight = 14.sp,
            )
            TransparencyRow("Aktueller Output", WindFarmMetricTransparency.CURRENT_OUTPUT)
            TransparencyRow("Jahresproduktion", WindFarmMetricTransparency.ANNUAL_PRODUCTION)
            TransparencyRow("Haushalte", WindFarmMetricTransparency.HOUSEHOLDS_SUPPLIED)
            TransparencyRow("CO2", WindFarmMetricTransparency.CO2_SAVINGS)
            TransparencyRow("Lokaler Anteil", WindFarmMetricTransparency.LOCAL_ENERGY)
            TransparencyRow("Kommunale Einnahmen", WindFarmMetricTransparency.MUNICIPAL_REVENUE)
            TransparencyRow("Windstarke", WindFarmMetricTransparency.WIND_SPEED)
            TransparencyRow("Larm", WindFarmMetricTransparency.NOISE)
        }
    }
}

@Composable
private fun TransparencyRow(
    title: String,
    note: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF3F6836),
        )
        Text(
            text = note,
            fontSize = 10.sp,
            color = Color(0xFF73796E),
            lineHeight = 13.sp,
        )
    }
}

@Composable
private fun MetricInfoButton(
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(32.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
            tint = tint,
        )
    }
}

@Composable
private fun StatusChip(status: WindFarmStatus) {
    val (bg, fg, label) = when (status) {
        WindFarmStatus.IN_BETRIEB -> Triple(Color(0xFFC0EFB0), Color(0xFF3F6836), "In Betrieb")
        WindFarmStatus.IN_WARTUNG -> Triple(Color(0xFFF9CD55), Color(0xFF5A4000), "In Wartung")
        WindFarmStatus.IN_PLANUNG -> Triple(Color(0xFF386569), Color.White, "In Planung")
        WindFarmStatus.STILLGELEGT -> Triple(Color(0xFFD8DBD2), Color(0xFF3C4B37), "Stillgelegt")
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(text = label, color = fg, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}


private data class HeightComparisonItem(
    val label: String,
    val heightM: Double?,
    val approximate: Boolean,
    val drawableRes: Int,
)

private data class HeightMeasurement(
    val heightM: Double,
    val approximate: Boolean,
)

@Composable
private fun GroessenvergleichCard(
    turbines: List<WindTurbine>,
    onInfoClick: () -> Unit,
) {
    val windFarmHeight = remember(turbines) { calculateAverageTurbineComparisonHeight(turbines) }
    val items = listOf(
        HeightComparisonItem(
            label = "Windrad",
            heightM = windFarmHeight?.heightM,
            approximate = windFarmHeight?.approximate == true,
            drawableRes = R.drawable.windrad_silhouette,
        ),
        HeightComparisonItem(
            label = "Kölner Dom",
            heightM = 157.0,
            approximate = false,
            drawableRes = R.drawable.koelner_dom_silhouette,
        ),
        HeightComparisonItem(
            label = "Berliner Fernsehturm",
            heightM = 368.0,
            approximate = false,
            drawableRes = R.drawable.fernsehturm_silhouette,
        ),
        HeightComparisonItem(
            label = "Frauenkirche Dresden",
            heightM = 91.0,
            approximate = false,
            drawableRes = R.drawable.frauenkirche_silhouette,
        ),
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Größenvergleich",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF191D17),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Höhe",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5B6654),
                )
            }

            MetricInfoButton(
                contentDescription = "Transparenz zum Groessenvergleich anzeigen",
                tint = Color(0xFF5B6654),
                onClick = onInfoClick,
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val axisWidth = 46.dp
                val plotHeight = 172.dp
                val labelBandHeight = 68.dp
                val itemWidth = 94.dp
                val itemSpacing = 12.dp
                val chartHeight = plotHeight + labelBandHeight
                val chartContentWidth = itemWidth * items.size + itemSpacing * (items.size - 1)
                val availableWidth = (maxWidth - axisWidth - 10.dp).coerceAtLeast(0.dp)
                val chartWidth = if (chartContentWidth > availableWidth) chartContentWidth else availableWidth
                val axisLabels = listOf(400, 300, 200, 100, 0)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier
                            .width(axisWidth)
                            .height(chartHeight),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Column(
                            modifier = Modifier.height(plotHeight),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.End,
                        ) {
                            axisLabels.forEach { value ->
                                Text(
                                    text = "$value m",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF5B6654),
                                    textAlign = TextAlign.End,
                                    maxLines = 1,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(labelBandHeight))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(chartHeight)
                            .horizontalScroll(rememberScrollState()),
                    ) {
                        Box(
                            modifier = Modifier
                                .width(chartWidth)
                                .height(chartHeight),
                        ) {
                            ComparisonChartGrid(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(plotHeight)
                                    .align(Alignment.TopStart),
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(plotHeight)
                                    .align(Alignment.TopStart),
                                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                items.forEach { item ->
                                    HeightComparisonPlotItem(
                                        item = item,
                                        plotHeight = plotHeight,
                                        itemWidth = itemWidth,
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(labelBandHeight)
                                    .align(Alignment.BottomStart),
                                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                                verticalAlignment = Alignment.Top,
                            ) {
                                items.forEach { item ->
                                    HeightComparisonLabel(
                                        item = item,
                                        labelBandHeight = labelBandHeight,
                                        itemWidth = itemWidth,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonChartGrid(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val lineColor = Color(0xFFB9C2B0).copy(alpha = 0.42f)
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(7f, 6f), 0f)
        listOf(0f, 0.25f, 0.5f, 0.75f, 1f).forEach { fraction ->
            val y = size.height * fraction
            drawLine(
                color = lineColor,
                start = androidx.compose.ui.geometry.Offset(0f, y),
                end = androidx.compose.ui.geometry.Offset(size.width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = pathEffect,
            )
        }
    }
}

@Composable
private fun HeightComparisonPlotItem(
    item: HeightComparisonItem,
    plotHeight: androidx.compose.ui.unit.Dp,
    itemWidth: androidx.compose.ui.unit.Dp,
) {
    val axisMaxHeightM = 400.0
    val heightFraction = ((item.heightM ?: 0.0) / axisMaxHeightM).coerceIn(0.0, 1.0).toFloat()
    val barHeight = if (item.heightM != null) {
        (plotHeight * heightFraction).coerceAtLeast(20.dp)
    } else {
        20.dp
    }

    Column(
        modifier = Modifier
            .width(itemWidth)
            .height(plotHeight),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(plotHeight),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Image(
                painter = painterResource(item.drawableRes),
                contentDescription = item.label,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(54.dp)
                    .height(barHeight),
                alignment = Alignment.BottomCenter,
            )
        }
    }
}

@Composable
private fun HeightComparisonLabel(
    item: HeightComparisonItem,
    labelBandHeight: androidx.compose.ui.unit.Dp,
    itemWidth: androidx.compose.ui.unit.Dp,
) {
    val barAlpha = if (item.approximate) 0.78f else 1f
    val valueText = item.heightM?.let {
        val prefix = if (item.approximate) "ca. " else ""
        "$prefix${it.roundToInt()} m"
    } ?: "nicht verfügbar"

    Column(
        modifier = Modifier
            .width(itemWidth)
            .height(labelBandHeight)
            .padding(top = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF43483F),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = valueText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF191D17).copy(alpha = barAlpha),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun WindTurbine.comparisonHeight(): HeightMeasurement? {
    val hubHeight = hubHeightM
    val rotorDiameter = rotorDiameterM
    return when {
        hubHeight != null && rotorDiameter != null -> HeightMeasurement(
            heightM = hubHeight + rotorDiameter / 2.0,
            approximate = false,
        )
        hubHeight != null -> HeightMeasurement(
            heightM = hubHeight,
            approximate = true,
        )
        rotorDiameter != null -> HeightMeasurement(
            heightM = rotorDiameter / 2.0,
            approximate = true,
        )
        else -> null
    }
}

private fun calculateAverageTurbineComparisonHeight(turbines: List<WindTurbine>): HeightMeasurement? {
    val measurements = turbines.mapNotNull { it.comparisonHeight() }
    if (measurements.isEmpty()) return null

    return HeightMeasurement(
        heightM = measurements.map { it.heightM }.average(),
        approximate = measurements.any { it.approximate },
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 420)
@Composable
private fun GroessenvergleichCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FBF1))
                .padding(16.dp),
        ) {
            GroessenvergleichCard(
                turbines = previewComparisonTurbines,
                onInfoClick = {},
            )
        }
    }
}

private val previewComparisonTurbines = listOf(
    WindTurbine(
        id = "preview-1",
        windFarmId = "preview-farm",
        manufacturer = "Vestas",
        model = "V150",
        ratedPowerKw = 5000.0,
        rotorDiameterM = 150.0,
        hubHeightM = 105.0,
        commissioningYear = 2021,
        status = WindFarmStatus.IN_BETRIEB,
        operator = "WindNah",
    ),
    WindTurbine(
        id = "preview-2",
        windFarmId = "preview-farm",
        manufacturer = "Nordex",
        model = "N149",
        ratedPowerKw = 4500.0,
        rotorDiameterM = 149.0,
        hubHeightM = 105.0,
        commissioningYear = 2020,
        status = WindFarmStatus.IN_BETRIEB,
        operator = "WindNah",
    ),
)

@Composable
private fun WindraederDetailsTab(
    turbines: List<WindTurbine>,
    onSizeComparisonInfoClick: () -> Unit,
) {
    if (turbines.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Keine Windräder verfügbar",
                color = Color(0xFF73796E),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        return
    }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidthDp - 32.dp

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBF1)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(turbines.size) { index ->
                    TurbineCard(
                        turbine = turbines[index],
                        index = index,
                        total = turbines.size,
                        cardWidth = cardWidth,
                    )
                }
            }
        }
        item {
            GroessenvergleichCard(
                turbines = turbines,
                onInfoClick = onSizeComparisonInfoClick,
            )
        }
    }
}

@Composable
private fun TurbineCard(
    turbine: WindTurbine,
    index: Int,
    total: Int,
    cardWidth: androidx.compose.ui.unit.Dp,
) {
    Card(
        modifier = Modifier.width(cardWidth),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    StatusChip(status = turbine.status)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Windrad ${index + 1}/$total",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191D17),
                            lineHeight = 22.sp,
                        )
                        if (turbine.manufacturer != null && turbine.model != null) {
                            Text(
                                text = "${turbine.manufacturer} ${turbine.model}",
                                fontSize = 14.sp,
                                color = Color(0xFF43483F),
                            )
                        }
                    }
                }
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Info",
                    tint = Color(0xFF3F6836),
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TurbineSpecRow(
                        value = "${formatDecimal(turbine.ratedPowerKw / 1_000.0, 2)} MW",
                        label = "Nennleistung",
                    )
                    turbine.rotorDiameterM?.let {
                        TurbineSpecRow(value = "${it.roundToInt()} m", label = "Rotordurchmesser")
                    }
                    turbine.hubHeightM?.let {
                        TurbineSpecRow(value = "${it.roundToInt()} m", label = "Nabenhöhe")
                    }
                    turbine.commissioningYear?.let {
                        TurbineSpecRow(value = "$it", label = "Baujahr")
                    }
                    turbine.operator?.let {
                        TurbineSpecRow(value = it, label = "Betreiber")
                    }
                }

                Image(
                    painter = painterResource(R.drawable.windrad_carousel),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 4.dp)
                        .size(width = 88.dp, height = 132.dp),
                    alignment = Alignment.TopCenter,
                )
            }
        }
    }
}

@Composable
private fun TurbineSpecRow(value: String, label: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF191D17),
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF43483F),
        )
    }
}

private fun formatNumber(n: Int): String {
    return String.format("%,d", n).replace(',', '.')
}

private fun formatDecimal(value: Double, decimals: Int): String {
    val formatted = String.format("%.${decimals}f", value)
    return formatted.replace('.', ',')
}

private fun formatMwLarge(kw: Double): String =
    String.format("%.1f", kw / 1000.0).replace('.', ',') + " MW"

private val noiseSimulationDistancesM = listOf(100, 250, 500, 1000)
private const val DEFAULT_NOISE_DISTANCE_INDEX = 2
private const val NOISE_REFERENCE_DISTANCE_M = 500.0
private const val NOISE_MIN_DB = 30.0
private const val NOISE_MAX_DB = 90.0

private fun estimateNoiseForDistance(referenceNoiseDbA: Double, distanceM: Int): Double =
    (referenceNoiseDbA + 20.0 * log10(NOISE_REFERENCE_DISTANCE_M / distanceM.toDouble()))
        .coerceIn(NOISE_MIN_DB, NOISE_MAX_DB)

private fun noiseComparisonText(noiseDbA: Double): String {
    val roundedNoiseDbA = noiseDbA.roundToInt()
    val comparison = when {
        noiseDbA >= 50.0 -> "einem normalen Gespräch"
        noiseDbA >= 40.0 -> "einer ruhigen Wohnstraße"
        else -> "einer Bibliothek"
    }
    return "Etwa $roundedNoiseDbA dB(A), vergleichbar mit $comparison."
}

private fun formatGwh(kwh: Double): String =
    String.format("%.1f", kwh / 1_000_000.0).replace('.', ',')

private fun formatMwh(kwh: Double): String =
    formatNumber((kwh / 1_000.0).roundToInt())

private fun formatEur(value: Int): String =
    "%,d".format(value).replace(',', '.')

private fun windClassification(ms: Double): String = when {
    ms < 1 -> "Windstille"
    ms < 3 -> "leiser Wind"
    ms < 5 -> "leichte Brise"
    ms < 8 -> "mäßiger Wind"
    ms < 11 -> "frischer Wind"
    ms < 14 -> "starker Wind"
    else -> "stürmisch"
}
