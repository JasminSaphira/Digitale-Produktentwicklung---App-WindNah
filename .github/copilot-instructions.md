# WindNah Development Rules

This project follows:

- 04_ARCHITECTURE.md
- 05_DESIGN_SYSTEM.md
- 06_API_SPEC.md
- 07_AI_GUIDELINES.md
- 09_CALCULATION_SPEC.md

Always follow:

- Kotlin
- Jetpack Compose
- Material Design 3
- MVVM
- Clean Architecture
- Multi Module Architecture

Never:

- Call APIs directly from Composables
- Put business logic in ViewModels
- Access Room from UI

Always:

- Use StateFlow
- Use Hilt
- Use Repository Pattern
- Use UseCases for calculations

All calculations must be implemented in:

core:domain

All reusable UI components belong in:

core:designsystem