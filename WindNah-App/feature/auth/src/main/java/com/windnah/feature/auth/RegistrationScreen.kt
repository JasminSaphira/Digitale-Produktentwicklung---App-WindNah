package com.windnah.feature.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = onNavigateBack,
    onAuthSuccess: () -> Unit = onNavigateBack,
    viewModel: RegistrationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthSuccess()
        }
    }

    RegistrationScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier,
    )
}

@Composable
private fun RegistrationScreenContent(
    uiState: RegistrationUiState,
    onEvent: (RegistrationUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        RegistrationHeader(
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 34.dp),
        )

        RegistrationFormCard(
            uiState = uiState,
            onEvent = onEvent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp),
        )
        FooterLoginLink(
            onNavigateToLogin = onNavigateToLogin,
            modifier = Modifier.padding(top = 28.dp, bottom = 32.dp),
        )
    }
}

@Composable
private fun RegistrationHeader(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilledTonalIconButton(
            onClick = onNavigateBack,
            modifier = Modifier.size(42.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Zur\u00fcck",
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = "Konto erstellen",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Werde Teil der WindNah Community",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun RegistrationFormCard(
    uiState: RegistrationUiState,
    onEvent: (RegistrationUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AuthErrorText(message = uiState.authErrorMessage)

            RegistrationTextField(
                value = uiState.name,
                onValueChange = { onEvent(RegistrationUiEvent.NameChanged(it)) },
                enabled = !uiState.isLoading,
                label = "Benutzername",
                placeholder = "Benutzername eingeben",
                leadingIcon = {
                    Icon(Icons.Outlined.Person, contentDescription = null)
                },
                onClear = if (uiState.name.isNotEmpty()) {
                    { onEvent(RegistrationUiEvent.NameChanged("")) }
                } else {
                    null
                },
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            )

            RegistrationTextField(
                value = uiState.email,
                onValueChange = { onEvent(RegistrationUiEvent.EmailChanged(it)) },
                enabled = !uiState.isLoading,
                label = "E-Mail",
                placeholder = "E-Mail eingeben",
                error = uiState.emailError,
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null)
                },
                onClear = if (uiState.email.isNotEmpty()) {
                    { onEvent(RegistrationUiEvent.EmailChanged("")) }
                } else {
                    null
                },
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            )

            RegistrationTextField(
                value = uiState.password,
                onValueChange = { onEvent(RegistrationUiEvent.PasswordChanged(it)) },
                enabled = !uiState.isLoading,
                label = "Passwort",
                placeholder = "Passwort eingeben",
                error = uiState.passwordError,
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null)
                },
                trailingIcon = {
                    PasswordVisibilityButton(
                        isPasswordVisible = uiState.isPasswordVisible,
                        onClick = { onEvent(RegistrationUiEvent.TogglePasswordVisibilityClicked) },
                    )
                },
                visualTransformation = if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
            )

            PasswordRequirementChecklist(
                requirements = uiState.passwordRequirements,
                modifier = Modifier.fillMaxWidth(),
            )

            RegistrationTextField(
                value = uiState.confirmPassword,
                onValueChange = { onEvent(RegistrationUiEvent.ConfirmPasswordChanged(it)) },
                enabled = !uiState.isLoading,
                label = "Passwort best\u00e4tigen",
                placeholder = "Passwort best\u00e4tigen",
                error = uiState.confirmPasswordError,
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null)
                },
                trailingIcon = {
                    PasswordVisibilityButton(
                        isPasswordVisible = uiState.isConfirmPasswordVisible,
                        onClick = { onEvent(RegistrationUiEvent.ToggleConfirmPasswordVisibilityClicked) },
                    )
                },
                visualTransformation = if (uiState.isConfirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            )

            TermsAgreementField(
                hasAcceptedTerms = uiState.hasAcceptedTerms,
                error = uiState.termsError,
                enabled = !uiState.isLoading,
                onCheckedChange = {
                    onEvent(RegistrationUiEvent.TermsAcceptanceChanged(it))
                },
            )

            Button(
                onClick = { onEvent(RegistrationUiEvent.SubmitClicked) },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(if (uiState.isLoading) "Konto wird erstellt..." else "Konto erstellen")
            }
        }
    }
}

@Composable
private fun RegistrationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon ?: onClear?.let { clear ->
            {
                IconButton(onClick = clear) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = "$label leeren",
                    )
                }
            }
        },
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        isError = error != null,
        supportingText = error?.let { message ->
            { Text(message) }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun PasswordVisibilityButton(
    isPasswordVisible: Boolean,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isPasswordVisible) {
                Icons.Outlined.VisibilityOff
            } else {
                Icons.Outlined.Visibility
            },
            contentDescription = if (isPasswordVisible) {
                "Passwort ausblenden"
            } else {
                "Passwort anzeigen"
            },
        )
    }
}

@Composable
private fun PasswordRequirementChecklist(
    requirements: List<PasswordRequirementUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(start = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        requirements.forEach { requirement ->
            val color = if (requirement.isMet) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (requirement.isMet) {
                        Icons.Outlined.CheckCircle
                    } else {
                        Icons.Outlined.RadioButtonUnchecked
                    },
                    contentDescription = if (requirement.isMet) {
                        "Anforderung erf\u00fcllt"
                    } else {
                        "Anforderung offen"
                    },
                    tint = color,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = requirement.label,
                    color = color,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun TermsAgreementField(
    hasAcceptedTerms: Boolean,
    error: String?,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { onCheckedChange(!hasAcceptedTerms) },
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = if (error != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.outline
                },
            ),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = hasAcceptedTerms,
                    enabled = enabled,
                    onCheckedChange = onCheckedChange,
                )
                Text(
                    text = buildAnnotatedString {
                        append("Ich stimme den ")
                        withStyle(
                            SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                            ),
                        ) {
                            append("Datenschutzbestimmungen und Nutzungsbedingungen")
                        }
                        append(" zu")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }
}

@Composable
private fun FooterLoginLink(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Bereits ein Konto?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = onNavigateToLogin) {
            Text(
                text = "Jetzt anmelden",
                style = MaterialTheme.typography.bodySmall,
                textDecoration = TextDecoration.Underline,
            )
        }
    }
}

@Composable
private fun AuthErrorText(
    message: String?,
    modifier: Modifier = Modifier,
) {
    if (message != null) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Start,
            modifier = modifier.fillMaxWidth(),
        )
    }
}
