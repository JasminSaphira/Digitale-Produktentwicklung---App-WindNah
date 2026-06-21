package com.windnah.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val isDarkMode by viewModel.isDarkModeEnabled.collectAsStateWithLifecycle()
    var showLoginSheet by remember { mutableStateOf(false) }

    if (showLoginSheet) {
        LoginBottomSheet(
            onDismiss = { showLoginSheet = false },
            onEmailLoginClick = {
                showLoginSheet = false
                onLoginClick()
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Profil", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Einstellungen & Konto",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {

            // ── Erscheinungsbild ─────────────────────────────────────────────
            item { SectionHeader("Erscheinungsbild") }
            item {
                ListItem(
                    headlineContent = { Text("Dark Mode") },
                    supportingContent = { Text("Dunkles Farbschema verwenden") },
                    leadingContent = {
                        Icon(
                            if (isDarkMode) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Switch(checked = isDarkMode, onCheckedChange = { viewModel.setDarkModeEnabled(it) })
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Sprache") },
                    supportingContent = { Text("Deutsch") },
                    leadingContent = { Icon(Icons.Outlined.Language, contentDescription = null) },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp),
                        )
                    },
                    modifier = Modifier.clickable { },
                )
            }

            // ── Berechtigungen ───────────────────────────────────────────────
            item {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                SectionHeader("Berechtigungen")
            }
            item {
                ListItem(
                    headlineContent = { Text("Standort") },
                    supportingContent = { Text("Windparks in deiner Nähe anzeigen") },
                    leadingContent = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                    trailingContent = { Switch(checked = false, onCheckedChange = { }) },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Benachrichtigungen") },
                    supportingContent = { Text("Neuigkeiten zu Windparks erhalten") },
                    leadingContent = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
                    trailingContent = { Switch(checked = false, onCheckedChange = { }) },
                )
            }

            // ── Datenansicht anpassen ────────────────────────────────────────
            item {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                SectionHeader("Datenansicht anpassen")
            }
            item {
                ListItem(
                    headlineContent = { Text("Live-Stromproduktion") },
                    leadingContent = { Icon(Icons.Outlined.Bolt, contentDescription = null) },
                    trailingContent = { Switch(checked = true, onCheckedChange = { }) },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("CO₂-Einsparung") },
                    leadingContent = { Icon(Icons.Outlined.Eco, contentDescription = null) },
                    trailingContent = { Switch(checked = true, onCheckedChange = { }) },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Versorgte Haushalte") },
                    leadingContent = { Icon(Icons.Outlined.Home, contentDescription = null) },
                    trailingContent = { Switch(checked = true, onCheckedChange = { }) },
                )
            }

            // ── Allgemein ────────────────────────────────────────────────────
            item {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                SectionHeader("Allgemein")
            }
            item {
                ListItem(
                    headlineContent = { Text("Datenschutz") },
                    leadingContent = { Icon(Icons.Outlined.Security, contentDescription = null) },
                    trailingContent = {
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.clickable { },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Datenquellen") },
                    supportingContent = { Text("MaStR · DWD Deutscher Wetterdienst") },
                    leadingContent = { Icon(Icons.Outlined.Storage, contentDescription = null) },
                    trailingContent = {
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.clickable { },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Über diese App") },
                    leadingContent = { Icon(Icons.Outlined.Info, contentDescription = null) },
                    trailingContent = {
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.clickable { },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Hilfe & Support") },
                    leadingContent = { Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null) },
                    trailingContent = {
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.clickable { },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Feedback geben") },
                    leadingContent = { Icon(Icons.Outlined.Feedback, contentDescription = null) },
                    trailingContent = {
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.clickable { },
                )
            }

            // ── Konto ────────────────────────────────────────────────────────
            item {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                SectionHeader("Konto")
            }
            item {
                ListItem(
                    headlineContent = { Text("Anmelden") },
                    supportingContent = { Text("Melde dich an, um Windparks zu speichern und zu synchronisieren.") },
                    leadingContent = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    modifier = Modifier.clickable { showLoginSheet = true },
                )
            }

            // ── App-Info Card ────────────────────────────────────────────────
            item {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x1A3F6836)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("WindNah", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Version 1.0.0 · Juni 2026", style = MaterialTheme.typography.bodySmall, color = Color(0xFF53634E))
                        Text("Entwickelt im Rahmen der Digitalen Produktentwicklung", style = MaterialTheme.typography.bodySmall, color = Color(0xFF53634E))
                    }
                }
            }

            // ── Abmelden ────────────────────────────────────────────────────
            item {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                ) {
                    Text("Abmelden")
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginBottomSheet(
    onDismiss: () -> Unit,
    onEmailLoginClick: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Anmelden oder registrieren",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "Melde dich an, um Windparks zu speichern und auf allen Geräten zu synchronisieren.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.AccountCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mit Google anmelden")
            }
            Button(
                onClick = onEmailLoginClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.MailOutline, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mit E-Mail anmelden")
            }
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Nicht jetzt")
            }
        }
    }
}
