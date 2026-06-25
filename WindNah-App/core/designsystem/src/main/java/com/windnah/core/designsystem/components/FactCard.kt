package com.windnah.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FactPrimary = Color(0xFF3F6836)
private val FactMythChip = Color(0xFFDFE4D8)
private val FactMythChipText = Color(0xFF285021)
private val FactText = Color(0xFF1C1B1F)
private val FactBodyText = Color(0xFF53634E)

@Composable
fun FactCard(
    myth: String,
    explanation: String,
    sources: List<String> = emptyList(),
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White,
            contentColor = FactText,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 3.dp,
            focusedElevation = 6.dp,
            hoveredElevation = 8.dp,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    LabelPill(
                        text = "MYTHOS",
                        containerColor = FactMythChip,
                        contentColor = FactMythChipText,
                    )
                    Text(
                        text = myth,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = FactText,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = if (expanded) "Fakt einklappen" else "Fakt ausklappen",
                    tint = FactPrimary,
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(18.dp))
                    FactDetail(
                        explanation = explanation,
                        sources = sources,
                    )
                }
            }
        }
    }
}

@Composable
private fun FactDetail(
    explanation: String,
    sources: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = FactPrimary.copy(alpha = 0.10f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LabelPill(
            text = "FAKT",
            containerColor = FactPrimary.copy(alpha = 0.16f),
            contentColor = FactPrimary,
        )
        Text(
            text = explanation,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = FactBodyText,
                fontSize = 14.sp,
                lineHeight = 22.sp,
            ),
        )
        if (sources.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                sources.forEach { source ->
                    SourcePill(source = "Quelle: $source")
                }
            }
        }
    }
}

@Composable
private fun LabelPill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = contentColor,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.7.sp,
                lineHeight = 12.sp,
            ),
        )
    }
}

@Composable
private fun SourcePill(
    source: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.widthIn(max = 280.dp),
        color = FactPrimary.copy(alpha = 0.08f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, FactPrimary.copy(alpha = 0.10f)),
    ) {
        Text(
            text = source,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = FactPrimary,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
