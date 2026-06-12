# WindNah Copilot Instructions

Always read and follow the documentation in `/docs`.

Important documents:

- `01_PRD.md`
- `02_INFORMATION_ARCHITECTURE.md`
- `03_USER_STORIES.md`
- `04_ARCHITECTURE.md`
- `05_DESIGN_SYSTEM.md`
- `06_API_SPEC.md`
- `07_AI_GUIDELINES.md`
- `08_DEVELOPMENT_ROADMAP.md`
- `09_CALCULATION_SPEC.md`

## Tech Stack

- Kotlin
- Jetpack Compose
- Material Design 3
- MVVM
- Clean Architecture
- Multi-Module Architecture
- Hilt
- Room
- Retrofit
- Firebase Authentication
- Firebase Firestore
- Google Maps Compose

## Rules

- Do not put business logic in Composables.
- Do not call APIs directly from Composables or ViewModels.
- Use UseCases for calculations.
- Use Repository interfaces.
- Use immutable UI state.
- Use StateFlow.
- Use Material 3 components.
- Use the WindNah design system.
- Put reusable UI in `core:designsystem`.
- Put calculations in `core:domain`.
- Put API clients in `core:network`.
- Put repository implementations in `core:data`.
- Put Room logic in `core:database`.

## Features

Main navigation:

- Entdecken
- Fakten
- Meine Anlagen
- Profil

Authentication is contextual only. Never show login directly after onboarding.