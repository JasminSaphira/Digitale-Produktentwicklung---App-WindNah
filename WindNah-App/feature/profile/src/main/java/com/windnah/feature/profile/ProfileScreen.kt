package com.windnah.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Button
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
            TopAppBar(title = { Text("Einstellungen") })
        },
        modifier = modifier,
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {

            // ── Erscheinungsbild ─────────────────────────────────────────────
            item {
                SectionHeader("Erscheinungsbild")
            }
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
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.setDarkModeEnabled(it) },
                        )
                    },
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
                    supportingContent = {
                        Text("Melde dich an, um Windparks zu speichern und zu synchronisieren.")
                    },
                    leadingContent = {
                        Icon(Icons.Outlined.Person, contentDescription = null)
                    },
                    modifier = Modifier.clickable { showLoginSheet = true },
                )
            }

            // ── Datenquellen ─────────────────────────────────────────────────
            item {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                SectionHeader("Datenquellen")
            }
            item {
                ListItem(
                    headlineContent = { Text("Marktstammdatenregister (MaStR)") },
                    supportingContent = {
                        Text("Anlagendaten zur Windenergie in Deutschland werden vom Register der Bundesnetzagentur bereitgestellt.")
                    },
                    leadingContent = {
                        Icon(Icons.Outlined.Storage, contentDescription = null)
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("DWD – Deutscher Wetterdienst") },
                    supportingContent = {
                        Text("Winddaten und Wettermessungen werden vom Deutschen Wetterdienst bezogen.")
                    },
                    leadingContent = {
                        Icon(Icons.Outlined.Cloud, contentDescription = null)
                    },
                )
            }

            // ── Über die App ─────────────────────────────────────────────────
            item {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                SectionHeader("Über die App")
            }
            item {
                ListItem(
                    headlineContent = { Text("WindNah") },
                    supportingContent = {
                        Text("Version 1.0.0 · Entwickelt im Rahmen der Digitalen Produktentwicklung")
                    },
                    leadingContent = {
                        Icon(Icons.Outlined.Info, contentDescription = null)
                    },
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Impressum") },
                    supportingContent = {
                        Text("Angaben gemäß § 5 TMG · Wird in einer zukünftigen Version vervollständigt.")
                    },
                    leadingContent = {
                        Icon(Icons.Outlined.Description, contentDescription = null)
                    },
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
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
                onClick = { /* TODO: Google Sign-In – M5 */ },
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

