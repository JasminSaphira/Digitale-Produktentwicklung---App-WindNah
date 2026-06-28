package com.windnah.feature.facts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.windnah.core.designsystem.components.FactCard
import com.windnah.core.designsystem.components.WindNahScreenHeader
import com.windnah.core.model.FactCategory

@Composable
fun FactsScreen(
    modifier: Modifier = Modifier,
    onNavigateToMap: () -> Unit = {},
    viewModel: FactsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    FactsScreenContent(
        uiState = uiState,
        onCategorySelected = viewModel::onCategorySelected,
        onRetry = viewModel::retry,
        onNavigateToMap = onNavigateToMap,
        modifier = modifier,
    )
}

@Composable
private fun FactsScreenContent(
    uiState: FactsUiState,
    onCategorySelected: (FactCategory?) -> Unit,
    onRetry: () -> Unit,
    onNavigateToMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                WindNahScreenHeader(
                    title = "Fakten",
                    subtitle = "Mythen kl\u00e4ren, Wissen vertiefen",
                    onBackClick = onNavigateToMap,
                )
                FactsCategoryTabs(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = onCategorySelected,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding() + 32.dp,
            ),
        ) {
            when {
                uiState.isLoading -> {
                    item {
                        FactsLoadingState(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                        )
                    }
                }

                uiState.errorMessage != null -> {
                    item {
                        FactsErrorState(
                            message = uiState.errorMessage,
                            onRetry = onRetry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 48.dp),
                        )
                    }
                }

                else -> {
                    items(
                        items = uiState.facts,
                        key = { it.id },
                    ) { fact ->
                        FactCard(
                            myth = fact.myth,
                            explanation = fact.explanation,
                            sources = fact.sources,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 28.dp, vertical = 12.dp),
                        )
                    }
                }
            }

            item {
                FactsFooter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(top = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun FactsCategoryTabs(
    selectedCategory: FactCategory?,
    onCategorySelected: (FactCategory?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = listOf(null to "Alle") + FactCategory.values().map { it to it.label }
    val selectedTabIndex = tabs
        .indexOfFirst { (category, _) -> category == selectedCategory }
        .coerceAtLeast(0)

    PrimaryScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 0.dp,
        divider = {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        },
    ) {
        tabs.forEachIndexed { index, (category, label) ->
            val selected = index == selectedTabIndex
            Tab(
                selected = selected,
                onClick = { onCategorySelected(category) },
                modifier = Modifier.height(48.dp),
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    )
                },
            )
        }
    }
}

@Composable
private fun FactsLoadingState(
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
private fun FactsErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp,
            ),
        )
        Button(onClick = onRetry) {
            Text("Erneut versuchen")
        }
    }
}

@Composable
private fun FactsFooter(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Diese App soll Ihnen helfen, eine informierte eigene Meinung zu bilden. " +
                "Wir streben Neutralit\u00e4t und wissenschaftliche Genauigkeit an.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Umweltbundesamt 2026",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
