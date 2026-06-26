package com.windnah.feature.facts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.windnah.core.designsystem.components.FactCard
import com.windnah.core.designsystem.components.WindNahScreenHeader
import com.windnah.core.model.FactCategory

private val FactsPrimary = Color(0xFF3F6836)
private val FactsTextSecondary = Color(0xFF49454F)

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
        modifier = modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WindNahScreenHeader(
                title = "Fakten",
                subtitle = "Mythen klären, Wissen vertiefen",
                onBackClick = onNavigateToMap,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 24.dp,
                bottom = innerPadding.calculateBottomPadding() + 32.dp,
            ),
        ) {
            item {
                FactsCategoryTabs(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = onCategorySelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }

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
    Row(
        modifier = modifier
            .background(Color.White)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryTab(
            text = "Alle",
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
        )
        FactCategory.values().forEach { category ->
            CategoryTab(
                text = category.label,
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
            )
        }
    }
}

@Composable
private fun CategoryTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (selected) FactsPrimary else Color.Transparent
    val contentColor = if (selected) Color.White else FactsTextSecondary

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge.copy(
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp,
            ),
        )
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
        CircularProgressIndicator(color = FactsPrimary)
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
                color = FactsTextSecondary,
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
                "Wir streben Neutralität und wissenschaftliche Genauigkeit an.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = FactsTextSecondary,
                lineHeight = 18.sp,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Umweltbundesamt 2026",
            style = MaterialTheme.typography.labelSmall.copy(
                color = FactsPrimary,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
