package com.windnah.feature.onboarding

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.windnah.core.designsystem.components.WindNahFooter
import kotlinx.coroutines.launch

private data class OnboardingPageData(
    val imageRes: Int,
    val headline: String,
    val body: String,
)

private val pages = listOf(
    OnboardingPageData(
        imageRes = R.drawable.onboarding_media_1,
        headline = "Windkraft in deiner Nähe",
        body = "Entdecke Windkraftanlagen in ganz Deutschland auf einer interaktiven Karte.",
    ),
    OnboardingPageData(
        imageRes = R.drawable.onboarding_media_2,
        headline = "Mythen klären, Fakten kennen",
        body = "Finde heraus, welche Aussagen über Windenergie zutreffen und wo Missverständnisse entstehen.",
    ),
    OnboardingPageData(
        imageRes = R.drawable.onboarding_media_3,
        headline = "Windenergie verstehen. Zukunft gestalten.",
        body = "Verständliche Daten und Nachhaltigkeitsmetriken auf einen Blick. Wir räumen mit Vorurteilen auf und schaffen Klarheit.",
    ),
)

private val PillShape = RoundedCornerShape(100.dp)

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsStateWithLifecycle()

    LaunchedEffect(onboardingCompleted) {
        if (onboardingCompleted) onOnboardingComplete()
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> viewModel.completeOnboarding(locationUsageEnabled = granted) },
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        // Header: 72 dp – logo (40 dp) + „WindNah“ titleMedium (Figma node I120:2154;100:3962)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_windnah_logo),
                contentDescription = "WindNah Logo",
                modifier = Modifier.size(40.dp),
            )
            Text(
                text = "WindNah",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { pageIndex ->
            OnboardingPageContent(page = pages[pageIndex])
        }

        val isLastPage = pagerState.currentPage == pages.size - 1

        if (!isLastPage) {
            // Screens 1 & 2: right-aligned „Weiter“ pill button (Figma node I120:2154;100:3976)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Weiter")
                }
            }
        } else {
            // Screen 3: buttons + footer hint anchored at bottom (Figma node 120:2139)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Primary: request coarse location permission, then complete onboarding
                Button(
                    onClick = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    },
                    modifier = Modifier.width(241.dp),
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Standort freigeben & starten")
                }

                // Secondary: skip location, complete onboarding directly
                Button(
                    onClick = { viewModel.completeOnboarding(locationUsageEnabled = false) },
                    modifier = Modifier.width(241.dp),
                    shape = PillShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp,
                    ),
                ) {
                    Text("ohne Standort starten")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Aktivieren Sie den Standortzugriff, um sofort Windparks in Ihrer Nähe zu finden. Die Standortfreigabe ist weiterhin optional und kann jederzeit geändert werden.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPageData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 32.dp, bottom = 16.dp),
    ) {
        // Media image – 24 dp rounded corners. Height capped relative to viewport so the
        // headline + body always fit (and stay scrollable on very small screens).
        Image(
            painter = painterResource(page.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 330.dp)
                .aspectRatio(1.5f)
                .clip(RoundedCornerShape(24.dp)),
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Headline + body
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = page.headline,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = page.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            WindNahFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            )
        }
    }
}
