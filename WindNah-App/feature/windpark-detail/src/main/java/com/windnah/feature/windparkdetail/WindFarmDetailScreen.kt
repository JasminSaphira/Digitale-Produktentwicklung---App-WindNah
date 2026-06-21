package com.windnah.feature.windparkdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmDetail
import com.windnah.core.model.WindFarmStatus
import com.windnah.core.model.WindTurbine
import com.windnah.feature.windparkdetail.R
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
    onNavigateBack: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Übersicht", "Windräder Details")

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            WindFarmHeader(
                windFarm = detail.windFarm,
                onNavigateBack = onNavigateBack,
            )

            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
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

            when (selectedTab) {
                0 -> UebersichtTab(windFarm = detail.windFarm, metrics = detail.energyMetrics)
                1 -> WindraederDetailsTab(
                    turbines = detail.turbines,
                    avgHubHeightM = 220.0,
                )
            }
        }
    }
}

@Composable
private fun WindFarmHeader(
    windFarm: WindFarm,
    onNavigateBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(208.dp)
            .background(Color(0xFF1B4332)),
    ) {
        Image(
            painter = painterResource(R.drawable.windpark_header),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        // Dark gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x33000000), Color(0xBB000000)),
                    ),
                ),
        )

        // Back button top-left
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 4.dp, top = 4.dp)
                .size(40.dp)
                .background(Color(0x66191D17), RoundedCornerShape(20.dp)),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Zurück",
                tint = Color.White,
            )
        }

        // Favorite + Share top-right
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0x66191D17), RoundedCornerShape(16.dp)),
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorit",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0x66191D17), RoundedCornerShape(16.dp)),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Teilen",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // Name and location bottom-left
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 16.dp),
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
                horizontalArrangement = Arrangement.spacedBy(4.dp),
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
private fun UebersichtTab(windFarm: WindFarm, metrics: EnergyMetrics) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MetricsRow(windFarm = windFarm, metrics = metrics)
    }
}

@Composable
private fun StatusRow(windFarm: WindFarm) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatusChip(status = windFarm.status)
        Text(
            text = "${windFarm.turbineCount} Windräder",
            color = Color(0xFF73796E),
            fontSize = 12.sp,
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

@Composable
private fun MetricsRow(windFarm: WindFarm, metrics: EnergyMetrics) {
    val nennleistungMw = windFarm.totalCapacityKw / 1_000.0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        MetricItem(
            value = if (nennleistungMw > 0) "${String.format("%.1f", nennleistungMw)} MW" else "–",
            label = "Nennleistung",
        )
        MetricItem(
            value = metrics.householdsSupplied.let {
                if (it > 0) formatNumber(it) else "–"
            },
            label = "Haushalte\nversorgt",
        )
        MetricItem(
            value = if (metrics.co2SavingsTonnesPerYear > 0)
                "${formatNumber(metrics.co2SavingsTonnesPerYear.roundToInt())} t"
            else "–",
            label = "CO₂ gespart/Jahr",
        )
    }
}

@Composable
private fun MetricItem(value: String, label: String) {
    Box(
        modifier = Modifier
            .size(width = 106.dp, height = 70.dp)
            .background(
                color = Color(0x1AC0EFB0),
                shape = RoundedCornerShape(24.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191D17),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF73796E),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

private data class SilhouetteItem(
    val labelLine1: String,
    val labelLine2: String?,
    val heightM: Double,
    val drawableRes: Int,
)

@Composable
private fun GroessenvergleichCard(avgHubHeightM: Double) {
    val maxHeight = 400.0
    val chartHeight = 142.dp
    val items = listOf(
        SilhouetteItem("Windrad", "(Ø)", avgHubHeightM, R.drawable.windrad_silhouette),
        SilhouetteItem("Kölner", "Dom", 157.0, R.drawable.koelner_dom_silhouette),
        SilhouetteItem("Berliner", "Fernsehturm", 368.0, R.drawable.fernsehturm_silhouette),
        SilhouetteItem("Dresdner", "Frauenkirche", 91.0, R.drawable.frauenkirche_silhouette),
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(17.dp)) {
            Text(
                text = "Größenvergleich",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191D17),
            )
            Text(
                text = "Höhe",
                fontSize = 12.sp,
                color = Color(0xFF3C4B37),
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                // Y-axis labels (400m top → 0 bottom)
                Column(
                    modifier = Modifier
                        .width(32.dp)
                        .height(chartHeight),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End,
                ) {
                    listOf("400m", "300m", "200m", "100m", "0").forEach { label ->
                        Text(
                            text = label,
                            fontSize = 9.sp,
                            color = Color(0xFF3C4B37),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Silhouettes
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    items.forEach { item ->
                        val fraction = (item.heightM / maxHeight).toFloat().coerceIn(0.05f, 1f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                        ) {
                            Image(
                                painter = painterResource(item.drawableRes),
                                contentDescription = item.labelLine1,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .width(52.dp)
                                    .height(chartHeight * fraction),
                                alignment = Alignment.BottomCenter,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.labelLine1,
                                fontSize = 9.sp,
                                color = Color(0xFF3C4B37),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                            if (item.labelLine2 != null) {
                                Text(
                                    text = item.labelLine2,
                                    fontSize = 9.sp,
                                    color = Color(0xFF3C4B37),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                )
                            }
                            Text(
                                text = "${item.heightM.roundToInt()}m",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3C4B37),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WindraederDetailsTab(turbines: List<WindTurbine>, avgHubHeightM: Double) {
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
            GroessenvergleichCard(avgHubHeightM = avgHubHeightM)
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
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: StatusChip + Name/Model inline, Info icon at end
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

            // Specs on left, silhouette on right
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
                        TurbineSpecRow(value = it, label = "Betreiber", valueBold = true)
                    }
                }

                Image(
                    painter = painterResource(R.drawable.windrad_silhouette),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 4.dp)
                        .size(width = 70.dp, height = 120.dp),
                    alignment = Alignment.TopCenter,
                )
            }
        }
    }
}

@Composable
private fun TurbineSpecRow(value: String, label: String, valueBold: Boolean = false) {
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
