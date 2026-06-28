package com.windnah.feature.profile

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.windnah.core.designsystem.components.WindNahScreenHeader
import com.windnah.core.model.AuthUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val isLocationUsageEnabled by viewModel.isLocationUsageEnabled.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val showLiveOutputMetric by viewModel.showLiveOutputMetric.collectAsStateWithLifecycle()
    val showCo2SavingsMetric by viewModel.showCo2SavingsMetric.collectAsStateWithLifecycle()
    val showHouseholdsMetric by viewModel.showHouseholdsMetric.collectAsStateWithLifecycle()
    var showLoginSheet by remember { mutableStateOf(false) }

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.setLocationUsageEnabled(granted)
    }

    LaunchedEffect(isLocationUsageEnabled) {
        if (isLocationUsageEnabled && !hasLocationPermission()) {
            viewModel.setLocationUsageEnabled(false)
        }
    }

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
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WindNahScreenHeader(
                title = "Profil",
                subtitle = "Einstellungen & Konto",
                onBackClick = onNavigateToMap,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 24.dp,
                top = innerPadding.calculateTopPadding() + 24.dp,
                end = 24.dp,
                bottom = innerPadding.calculateBottomPadding() + 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                ProfileHeroCard(
                    currentUser = currentUser,
                    onEditClick = { showLoginSheet = true },
                )
            }

            item { AppInfoCard() }

            item {
                ProfileSection(title = "Berechtigungen") {
                    ProfileSwitchRow(
                        icon = Icons.Outlined.LocationOn,
                        title = "Standort",
                        subtitle = if (isLocationUsageEnabled) {
                            "Windraeder in Ihrer Naehe finden"
                        } else {
                            "Standortnutzung in WindNah ist deaktiviert"
                        },
                        checked = isLocationUsageEnabled,
                        onCheckedChange = { enabled ->
                            if (!enabled) {
                                viewModel.setLocationUsageEnabled(false)
                            } else if (hasLocationPermission()) {
                                viewModel.setLocationUsageEnabled(true)
                            } else {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                            }
                        },
                    )
                    SectionDivider()
                    ProfileSwitchRow(
                        icon = Icons.Outlined.Notifications,
                        title = "Benachrichtigungen",
                        subtitle = "Updates zu gespeicherten Anlagen",
                        checked = false,
                        onCheckedChange = {},
                    )
                }
            }

            item {
                ProfileSection(title = "Darstellung") {
                    ProfileActionRow(
                        icon = Icons.Outlined.Language,
                        title = "Sprache",
                        subtitle = "Deutsch",
                        onClick = {},
                    )
                }
            }

            item {
                ProfileSection(title = "Datenansicht anpassen") {
                    ProfileSwitchRow(
                        title = "Live-Stromproduktion",
                        subtitle = "Aktueller Output und Kapazitaet",
                        checked = showLiveOutputMetric,
                        onCheckedChange = viewModel::setShowLiveOutputMetric,
                    )
                    SectionDivider()
                    ProfileSwitchRow(
                        title = "CO2-Einsparung",
                        subtitle = "Umweltwirkung in Tonnen",
                        checked = showCo2SavingsMetric,
                        onCheckedChange = viewModel::setShowCo2SavingsMetric,
                    )
                    SectionDivider()
                    ProfileSwitchRow(
                        title = "Versorgte Haushalte",
                        subtitle = "Anzahl versorgter Haushalte/Jahr",
                        checked = showHouseholdsMetric,
                        onCheckedChange = viewModel::setShowHouseholdsMetric,
                    )
                }
            }

            item {
                ProfileSection(title = "Allgemein") {
                    ProfileActionRow(
                        icon = Icons.Outlined.Security,
                        title = "Datenschutz",
                        subtitle = "Datenschutzerklaerung & Einstellungen",
                        onClick = {},
                    )
                    SectionDivider()
                    ProfileActionRow(
                        icon = Icons.Outlined.Storage,
                        title = "Datenquellen",
                        subtitle = "DWD, MaStR",
                        onClick = {},
                    )
                    SectionDivider()
                    ProfileActionRow(
                        icon = Icons.Outlined.Info,
                        title = "Ueber diese App",
                        subtitle = "Version 1.2.0 · Mai 2026",
                        onClick = {},
                    )
                    SectionDivider()
                    ProfileActionRow(
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        title = "Hilfe & Support",
                        subtitle = "Haeufige Fragen & Kontakt",
                        onClick = {},
                    )
                    SectionDivider()
                    ProfileActionRow(
                        icon = Icons.Outlined.Feedback,
                        title = "Feedback geben",
                        subtitle = "Teilen Sie uns Ihre Meinung mit",
                        onClick = {},
                    )
                }
            }

            item {
                ProfileSection(title = "Kontakt") {
                    ContactRow(
                        icon = Icons.Outlined.Email,
                        title = "info@uba.bund.de",
                    )
                    SectionDivider()
                    ContactRow(
                        icon = Icons.Outlined.Phone,
                        title = "+49 340 2103-0",
                        subtitle = "Mo-Fr 8-16 Uhr",
                    )
                }
            }

            item { PrivacyNoticeCard() }

            item {
                OutlinedButton(
                    onClick = {
                        if (currentUser == null) {
                            showLoginSheet = true
                        } else {
                            viewModel.signOut()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(36.dp),
                ) {
                    Icon(
                        if (currentUser == null) Icons.Outlined.AccountCircle else Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (currentUser == null) "Anmelden" else "Abmelden")
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "WindNah v1.2.0 · UBA 2026",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "Entwickelt im Auftrag des Umweltbundesamts ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeroCard(
    currentUser: AuthUser?,
    onEditClick: () -> Unit,
) {
    val displayName = currentUser?.displayName
        ?.takeIf { it.isNotBlank() }
        ?: currentUser?.email
        ?: "Gast"
    val email = currentUser?.email ?: "Nicht angemeldet"
    val initial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "G"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(36.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Profil bearbeiten",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(onClick = onEditClick)
                        .padding(8.dp),
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ProfileStat(value = "12", label = "Besuche")
                ProfileStat(value = "2", label = "Favoriten")
                ProfileStat(value = "2", label = "Mitgliedstage")
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun AppInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
           containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Air,
                        contentDescription = "WindNah Logo",
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(36.dp),
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "WindNah",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Version 1.2.0 · Mai 2026",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.78f),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Text(
                text = "HERAUSGEGEBEN VON",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.72f),
                fontWeight = FontWeight.Bold,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Umweltbundesamt (UBA)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Universität Leipzig",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                )
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.22f),
            )

            Text(
                text = "Diese App verfolgt keine kommerziellen Interessen. Ziel ist die neutrale, faktbasierte Information der Öffentlichkeit über Windenergie in Deutschland.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.94f),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 12.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = profileSectionCardColor()),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
            ),
            elevation = profileSectionCardElevation(),
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun ProfileSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            // Toggle the whole row as one element so TalkBack reads it as a single switch.
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(56.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            // null: the toggleable Row above owns the click + semantics
            onCheckedChange = null,
            colors = windNahSwitchColors(),
        )
    }
}

@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(56.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (subtitle == null) 48.dp else 72.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(56.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PrivacyNoticeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x29386569)),
        shape = RoundedCornerShape(36.dp),
        border = BorderStroke(1.dp, Color(0xFFA0CFD2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.padding(17.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.PrivacyTip,
                contentDescription = null,
                tint = Color(0xFF386569),
                modifier = Modifier.size(24.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Datenschutz-Hinweis",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF386569),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Diese App erhebt keine persoenlichen Daten ohne Ihre Zustimmung. Umfragedaten werden anonym und aggregiert gespeichert.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1E4D51),
                )
            }
        }
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
}

@Composable
private fun profileSectionCardColor(): Color {
    val colorScheme = MaterialTheme.colorScheme
    return if (colorScheme.background.luminance() > 0.5f) {
        Color.White
    } else {
        colorScheme.surfaceContainer
    }
}

@Composable
private fun profileSectionCardElevation(): CardElevation =
    CardDefaults.cardElevation(defaultElevation = 1.dp)

@Composable
private fun windNahSwitchColors(): SwitchColors {
    val colorScheme = MaterialTheme.colorScheme
    return SwitchDefaults.colors(
        checkedThumbColor = colorScheme.onPrimary,
        checkedTrackColor = colorScheme.primary,
        checkedBorderColor = colorScheme.primary,
        uncheckedThumbColor = colorScheme.outline,
        uncheckedTrackColor = colorScheme.surfaceContainerHighest,
        uncheckedBorderColor = colorScheme.outlineVariant,
        disabledCheckedThumbColor = colorScheme.onSurface.copy(alpha = 0.38f),
        disabledCheckedTrackColor = colorScheme.onSurface.copy(alpha = 0.12f),
        disabledCheckedBorderColor = colorScheme.onSurface.copy(alpha = 0.12f),
        disabledUncheckedThumbColor = colorScheme.onSurface.copy(alpha = 0.38f),
        disabledUncheckedTrackColor = colorScheme.onSurface.copy(alpha = 0.12f),
        disabledUncheckedBorderColor = colorScheme.onSurface.copy(alpha = 0.12f),
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
                text = "Melde dich an, um Windparks zu speichern und auf allen Geraeten zu synchronisieren.",
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
