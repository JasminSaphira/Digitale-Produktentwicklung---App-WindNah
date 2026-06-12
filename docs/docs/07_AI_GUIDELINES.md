# WindNah – AI Development Guidelines

Version: 1.0
Status: Draft

Related Documents:

* 01_PRD.md
* 02_INFORMATION_ARCHITECTURE.md
* 03_USER_STORIES.md
* 04_ARCHITECTURE.md
* 05_DESIGN_SYSTEM.md
* 06_API_SPEC.md

---

# 1. Purpose

This document defines how AI assistants must support the development of WindNah.

Supported tools:

* Cursor
* GitHub Copilot
* ChatGPT
* Figma MCP Server
* Android Studio AI Assistant

The goal is to ensure:

* architectural consistency
* maintainable code
* predictable outputs
* adherence to project requirements

---

# 2. Source of Truth Hierarchy

AI must always follow the project documentation hierarchy.

```text
01_PRD.md
↓
02_INFORMATION_ARCHITECTURE.md
↓
03_USER_STORIES.md
↓
06_API_SPEC.md
↓
04_ARCHITECTURE.md
↓
05_DESIGN_SYSTEM.md
↓
Implementation
```

If conflicts occur:

The higher document takes precedence.

---

# 3. General AI Rules

AI must:

* follow Android best practices
* follow Material Design 3
* follow Clean Architecture
* follow MVVM
* prefer official Android recommendations
* generate maintainable code
* prefer readability over cleverness

AI must not:

* introduce undocumented features
* invent new user flows
* change navigation without request
* introduce unnecessary dependencies
* violate module boundaries

---

# 4. Technology Stack

All generated code must use:

```text
Kotlin

Jetpack Compose

Material Design 3

Navigation Compose

Hilt

Room

Retrofit

Coroutines

Flow

StateFlow

Firebase Authentication

Firebase Firestore
```

Avoid alternative frameworks unless explicitly requested.

---

# 5. Architecture Rules

WindNah uses:

```text
MVVM
+
Clean Architecture
```

---

## Allowed Flow

```text
Composable
↓
ViewModel
↓
UseCase
↓
Repository Interface
↓
Repository Implementation
↓
Remote / Local Data Source
```

---

## Forbidden

```text
Composable → API

Composable → Room

ViewModel → Retrofit

ViewModel → Firebase

UI → Database
```

Business logic must never be placed in UI components.

---

# 6. Module Rules

Current module structure:

```text
app

core:common
core:model
core:designsystem
core:network
core:database
core:data
core:domain

feature:onboarding
feature:discover
feature:windpark-detail
feature:facts
feature:my-turbines
feature:profile
feature:auth
```

---

## Feature Rule

Feature modules must never depend directly on another feature module.

Example:

❌

```text
feature:discover
↓
feature:profile
```

Allowed:

✅

```text
feature:discover
↓
core:domain
```

---

# 7. UI Rules

All UI must follow:

* Material Design 3
* WindNah Design System
* Compose best practices

---

## Mandatory Components

Use:

* Material 3 Cards
* Material 3 Navigation Bar
* Material 3 Bottom Sheets
* Material 3 Search Bar
* Material 3 Tabs
* Material 3 Filter Chips

before creating custom components.

---

## Custom Components

Prefer reusable components from:

```text
core:designsystem
```

Examples:

```text
WindNahMetricCard

WindFarmPreviewCard

FactCard

StatusChip
```

---

# 8. Figma MCP Rules

Figma is not the source of truth.

Figma provides:

* visual specifications
* spacing
* sizing
* layout details

Figma must not override:

* architecture
* navigation
* business logic
* design system decisions

---

## Figma Workflow

```text
User Story
↓
Design System
↓
Figma Screen
↓
Compose Implementation
```

Never generate UI directly from Figma without checking the Design System.

---

# 9. State Management Rules

Each screen must have:

```kotlin
UiState
UiEvent
ViewModel
```

---

## State Requirements

State must be:

* immutable
* observable
* predictable

Use:

```kotlin
StateFlow
```

for screen state.

---

# 10. ViewModel Rules

ViewModels:

* expose UI state
* coordinate use cases
* handle UI events

ViewModels must not:

* contain Retrofit code
* contain Room code
* contain Firebase code

---

# 11. Repository Rules

Repositories:

* abstract data sources
* coordinate cache behavior
* expose domain models

Repositories must not expose DTOs.

Always map:

```text
DTO
↓
Domain Model
```

before returning data.

---

# 12. Calculation Rules

All calculations belong to:

```text
core:domain
```

Never calculate inside:

* Composables
* ViewModels
* Repositories

---

## Examples

```text
CalculateCurrentOutputUseCase

CalculateCo2SavingsUseCase

CalculateHouseholdsSuppliedUseCase

CalculateMunicipalRevenueUseCase

CalculateNoiseEstimateUseCase
```

---

# 13. API Rules

All API integrations must follow:

```text
Remote DTO
↓
Mapper
↓
Domain Model
```

---

## Required APIs

MaStR

DWD

Firebase

---

## Cache Rules

Wind Farms:

```text
30 days
```

Weather:

```text
15 minutes
```

Facts:

```text
Permanent
```

---

# 14. Transparency Rules

Transparency is a core product principle.

Every calculated metric must expose:

```text
Description

Calculation

Source

Update Frequency
```

through the Transparency Overlay.

---

## Examples

Current Output

CO₂ Savings

Households

Municipal Revenue

Noise Estimation

Local Contribution

---

# 15. Accessibility Rules

All generated UI must support:

* TalkBack
* Dynamic Font Sizes
* Dark Mode
* Minimum touch targets

---

## Minimum Touch Target

```text
48dp
```

---

## Contrast

WCAG AA minimum.

---

# 16. Error Handling Rules

Every screen must support:

```text
Loading

Success

Error

Empty
```

states.

---

## Example Messages

Weather unavailable:

```text
Winddaten aktuell nicht verfügbar.
```

Internet unavailable:

```text
Keine Internetverbindung.
```

---

# 17. Testing Rules

Every feature should include:

---

## Unit Tests

For:

* Use Cases
* Calculations
* ViewModels

---

## Integration Tests

For:

* Repositories
* Room
* API Integration

---

## UI Tests

For:

* Navigation
* Login Flow
* Settings
* Detail Screens

---

# 18. Code Style Rules

Prefer:

* small functions
* meaningful names
* constructor injection
* immutable data classes

Avoid:

* large ViewModels
* god classes
* nested logic
* duplicated code

---

# 19. Documentation Rules

When generating new features:

AI should update relevant documentation if requested.

Affected documents may include:

* PRD
* Information Architecture
* User Stories
* API Spec
* Architecture

---

# 20. Definition of Good AI Output

A generated solution is considered acceptable when:

* it compiles
* it follows Clean Architecture
* it follows MVVM
* it follows Material 3
* it respects module boundaries
* it supports loading/error states
* it supports accessibility
* it aligns with project documentation
* it can be maintained by the development team

---

# 21. AI Prompting Strategy

When implementing a feature:

1. Read User Story
2. Read API Specification
3. Read Architecture
4. Read Design System
5. Read Figma Screen
6. Generate Code
7. Validate against this document

Never skip steps.
