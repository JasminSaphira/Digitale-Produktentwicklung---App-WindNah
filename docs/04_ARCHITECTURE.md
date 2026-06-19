# WindNah – Architecture Documentation

Version: 1.0
Status: Draft
Related Documents:

* 01_PRD.md
* 02_INFORMATION_ARCHITECTURE.md
* 03_USER_STORIES.md

---

# 1. Architecture Goals

WindNah uses a modular Android architecture based on Clean Architecture and MVVM.

The architecture is designed to support:

* clear separation of concerns
* scalability
* testability
* maintainability
* API integration
* offline availability
* privacy-first data handling
* Material Design 3 UI consistency

The application should remain understandable and maintainable for a small development team while still demonstrating professional Android architecture principles.

---

# 2. Technology Stack

## Platform

Android

## Language

Kotlin

## UI

Jetpack Compose

## Design System

Material Design 3

## Architecture Pattern

MVVM + Clean Architecture

## Dependency Injection

Hilt

## Networking

Retrofit
OkHttp
Kotlinx Serialization or Moshi

## Local Storage

Room

## Reactive State

Kotlin Coroutines
Flow
StateFlow

## Navigation

Navigation Compose

## Maps

OpenStreetMap / osmdroid

## Authentication

Firebase Authentication

## Cloud Storage / Sync

Firebase Firestore

## Build System

Gradle Kotlin DSL

---

# 3. Modularization Strategy

WindNah uses a pragmatic multi-module architecture.

The goal is to keep the application scalable and clean without overcomplicating the university project.

---

## Module Overview

```text
WindNah
│
├── app
│
├── core
│   ├── common
│   ├── model
│   ├── designsystem
│   ├── network
│   ├── database
│   ├── data
│   └── domain
│
└── feature
    ├── onboarding
    ├── discover
    ├── windpark-detail
    ├── facts
    ├── my-turbines
    ├── profile
    └── auth
```

---

# 4. Module Responsibilities

## app

Responsible for:

* application entry point
* app theme setup
* navigation host
* dependency initialization
* Firebase initialization
* root-level configuration

Should not contain:

* business logic
* API logic
* database logic

---

## core:common

Contains shared utilities.

Examples:

* Result wrappers
* error models
* date utilities
* number formatting
* coroutine dispatchers
* resource wrappers

---

## core:model

Contains shared domain models.

Examples:

* WindFarm
* WindTurbine
* WindFarmStatus
* WeatherData
* EnergyMetrics
* NoiseEstimate
* MunicipalityMetrics
* UserPreferences

---

## core:designsystem

Contains reusable UI components and design tokens.

Examples:

* WindNahTheme
* color scheme
* typography
* spacing
* buttons
* cards
* metric cards
* loading states
* error states

---

## core:network

Contains API clients and remote DTOs.

Examples:

* MaStR API client
* DWD API client
* Retrofit setup
* OkHttp setup
* network DTOs
* remote error handling

---

## core:database

Contains local database implementation.

Examples:

* Room database
* DAOs
* entities
* local mappers

Stores:

* favorites
* recently viewed wind farms
* facts
* cached wind farms
* cached weather data where useful

---

## core:data

Contains repository implementations and data source coordination.

Responsibilities:

* coordinate remote and local data sources
* map DTOs and entities to domain models
* expose repository implementations
* handle caching strategies
* handle fallback behavior

---

## core:domain

Contains business logic.

Responsibilities:

* repository interfaces
* use cases
* calculation logic
* domain validation

Examples:

* GetWindFarmsUseCase
* GetWindFarmDetailsUseCase
* CalculateCurrentOutputUseCase
* CalculateCo2SavingsUseCase
* CalculateHouseholdsSuppliedUseCase
* CalculateMunicipalRevenueUseCase
* CalculateNoiseEstimateUseCase
* GetFactsUseCase
* AddFavoriteUseCase
* RemoveFavoriteUseCase

---

# 5. Feature Modules

Each feature module contains its own UI, ViewModels and feature-specific state.

Feature modules should not depend on each other directly.

Shared functionality must be placed in core modules.

---

## feature:onboarding

Contains:

* Launch screen
* Onboarding screens
* location permission explanation

Screens:

* LaunchScreen
* OnboardingScreen

---

## feature:discover

Contains:

* map screen
* search
* filters
* wind farm markers
* wind farm preview overlay

Screens:

* DiscoverScreen
* WindFarmPreviewBottomSheet

---

## feature:windpark-detail

Contains:

* wind farm detail screen
* overview tab
* turbine detail tab
* metric cards
* production history
* noise simulation
* transparency overlays

Screens:

* WindFarmDetailScreen
* OverviewTab
* TurbineDetailsTab
* TransparencyOverlay

---

## feature:facts

Contains:

* fact category tabs
* fact list
* fact detail

Screens:

* FactsScreen
* FactDetailScreen

---

## feature:my-turbines

Contains:

* favorites
* recently viewed wind farms

Screens:

* MyTurbinesScreen

---

## feature:profile

Contains:

* user profile
* appearance settings
* language settings
* location settings
* privacy settings
* source information
* imprint

Screens:

* ProfileScreen

---

## feature:auth

Contains contextual authentication.

Screens:

* LoginBottomSheet
* LoginScreen
* RegistrationScreen

---

# 6. Clean Architecture Layers

WindNah follows three main layers:

```text
Presentation Layer
↓
Domain Layer
↓
Data Layer
```

Dependencies only point inward.

```text
Presentation depends on Domain

Data depends on Domain

Domain depends on nothing
```

---

# 7. Layer Responsibilities

## Presentation Layer

Contains:

* Composable screens
* ViewModels
* UI State
* UI Events
* navigation interactions

Responsibilities:

* render state
* forward user actions to ViewModel
* display loading, success and error states
* collect StateFlow from ViewModel

Must not contain:

* API calls
* database calls
* business calculations
* repository implementations

---

## Domain Layer

Contains:

* use cases
* repository interfaces
* domain models
* calculation logic

Responsibilities:

* define business rules
* calculate estimated values
* keep logic testable and independent from Android framework

Must not contain:

* Compose code
* Retrofit code
* Room code
* Firebase code

---

## Data Layer

Contains:

* repository implementations
* remote data sources
* local data sources
* DTO/entity mappers

Responsibilities:

* fetch data from APIs
* store data locally
* synchronize favorites
* provide offline fallback
* map technical data to domain models

---

# 8. Dependency Rules

Allowed dependencies:

```text
app → feature modules
app → core modules

feature:* → core:domain
feature:* → core:model
feature:* → core:designsystem
feature:* → core:common

core:data → core:domain
core:data → core:model
core:data → core:network
core:data → core:database

core:domain → core:model
core:domain → core:common

core:network → core:common
core:database → core:model
```

Not allowed:

```text
feature:discover → feature:windpark-detail

feature:profile → feature:auth

core:domain → core:data

core:domain → core:network

core:domain → core:database

core:model → any other module
```

Navigation between features should be coordinated by the app module.

---

# 9. Package Structure Example

```text
feature/discover
└── src/main/java/de/windnah/feature/discover
    ├── DiscoverScreen.kt
    ├── DiscoverViewModel.kt
    ├── DiscoverUiState.kt
    ├── DiscoverUiEvent.kt
    ├── components
    │   ├── WindFarmMarker.kt
    │   ├── SearchBar.kt
    │   └── FilterChips.kt
    └── navigation
        └── DiscoverNavigation.kt
```

```text
core/domain
└── src/main/java/de/windnah/core/domain
    ├── repository
    ├── usecase
    └── calculation
```

```text
core/data
└── src/main/java/de/windnah/core/data
    ├── repository
    ├── mapper
    ├── remote
    └── local
```

---

# 10. Data Flow

The standard data flow follows this pattern:

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

Example:

```text
DiscoverScreen
↓
DiscoverViewModel
↓
GetWindFarmsUseCase
↓
WindFarmRepository
↓
MaStRRemoteDataSource + WindFarmLocalDataSource
```

---

# 11. State Management

Each screen exposes one immutable UI state object.

Example:

```kotlin
data class DiscoverUiState(
    val isLoading: Boolean = false,
    val windFarms: List<WindFarm> = emptyList(),
    val selectedFederalState: FederalState? = null,
    val selectedStatuses: Set<WindFarmStatus> = emptySet(),
    val searchQuery: String = "",
    val errorMessage: String? = null
)
```

Rules:

* ViewModels expose StateFlow
* UI collects StateFlow
* UI events are forwarded to ViewModel
* State is immutable
* Loading, success and error states are explicit

---

# 12. Navigation Architecture

Navigation is handled in the app module using Navigation Compose.

Feature modules expose navigation routes but do not directly navigate to other features.

---

## Main Navigation

```text
Entdecken
Fakten
Meine Anlagen
Profil
```

---

## Route Examples

```text
discover
facts
my_turbines
profile
wind_farm_detail/{windFarmId}
fact_detail/{factId}
login
register
```

---

## Deep Links

Future-ready deep links:

```text
windnah://windpark/{id}
windnah://fact/{id}
windnah://profile
windnah://favorites
```

---

# 13. API Integration Strategy

WindNah should directly attempt to integrate real APIs.

However, all API access must be abstracted behind repository interfaces.

This allows replacing real APIs with mock or local JSON data if integration becomes too complex.

---

## API Sources

### MaStR

Used for:

* wind farm master data
* turbine master data
* installed capacity
* coordinates
* operator
* construction year
* turbine technical data

### DWD

Used for:

* current wind speed
* weather conditions
* wind-related live data

---

## API Rule

Presentation layer never calls APIs directly.

Only the data layer communicates with APIs.

---

# 14. Repository Strategy

## WindFarmRepository

Responsible for:

* loading wind farms
* loading wind farm details
* searching by location or ZIP code
* filtering by federal state and status
* caching wind farm data

---

## WeatherRepository

Responsible for:

* loading current weather data
* loading wind speed data
* providing error state if DWD data is unavailable

---

## FactRepository

Responsible for:

* loading fact categories
* loading fact articles
* caching facts offline

---

## FavoriteRepository

Responsible for:

* adding favorites
* removing favorites
* syncing favorites for authenticated users
* storing favorites locally

---

## UserPreferencesRepository

Responsible for:

* dark mode
* language setting
* visible metric cards
* location permission state where applicable

---

# 15. Offline Strategy

WindNah supports partial offline usage.

Offline available data:

* favorites
* recently viewed wind farms
* previously loaded wind farm details
* fact articles once loaded

---

## Local Storage

Room is used for persistent local storage.

DataStore may be used for lightweight preferences.

---

## Offline Behavior

If internet is unavailable:

* show cached wind farm details if available
* show cached facts if available
* show favorites from local database
* show recently viewed wind farms from local database
* display a clear offline message

---

# 16. Authentication Strategy

Authentication is contextual.

Users are never forced to log in during onboarding or app startup.

Login is only triggered when users try to access account-based functionality.

Triggers:

* save favorite
* share wind farm
* sync favorites

---

## Authentication Flow

```text
User action requires login
↓
Login Bottom Sheet
↓
Google Login / Email Login / Not Now
↓
Optional full login or registration screen
↓
Return to previous action
```

---

## Firebase Usage

Firebase Authentication:

* Google Login
* Email and Password Login

Firestore:

* synced favorites for authenticated users
* optional user preferences if needed

No unnecessary personal data should be stored.

---

# 17. Error Handling

User-facing errors must be clear and non-technical.

---

## Error Types

### Weather Data Unavailable

Message:

```text
Winddaten aktuell nicht verfügbar.
```

### No Internet Connection

Message:

```text
Keine Internetverbindung. Einige Inhalte sind möglicherweise nicht aktuell.
```

### Wind Farm Not Found

Message:

```text
Zu diesem Windpark sind aktuell keine Daten verfügbar.
```

### Login Failed

Message:

```text
Anmeldung fehlgeschlagen. Bitte versuchen Sie es erneut.
```

### Favorite Could Not Be Saved

Message:

```text
Favorit konnte nicht gespeichert werden.
```

### Calculation Not Available

Message:

```text
Diese Kennzahl kann aktuell nicht berechnet werden.
```

---

# 18. Calculation Architecture

All calculations must be placed in the domain layer.

---

## Calculation Use Cases

```text
CalculateCurrentOutputUseCase

CalculateAnnualProductionUseCase

CalculateHouseholdsSuppliedUseCase

CalculateCo2SavingsUseCase

CalculateMunicipalRevenueUseCase

CalculateLocalEnergyContributionUseCase

CalculateNoiseEstimateUseCase
```

---

## Calculation Rules

* calculations must be testable
* input values must be explicit
* assumptions must be documented
* calculated values must be marked as estimates
* each calculated metric must support a transparency overlay

---

# 19. Privacy Architecture

WindNah follows Privacy by Design.

Rules:

* no mandatory account
* no behavioral profiling
* no advertising tracking
* no marketing tracking
* minimal analytics only
* location is optional
* location is not permanently stored

---

## Location Data

Used only for:

* centering the map
* showing nearby wind farms

Location data should not be stored permanently.

---

## Analytics

Analytics are optional.

Only aggregated and non-personal usage metrics may be collected.

Allowed examples:

* number of detail page openings
* number of fact articles opened
* number of favorites created

Not allowed:

* session duration tracking
* individual navigation paths
* behavioral profiles
* advertising identifiers

---

# 20. Dependency Injection

Hilt is used for dependency injection.

DI modules should be organized by responsibility.

Examples:

```text
NetworkModule

DatabaseModule

RepositoryModule

UseCaseModule

FirebaseModule
```

Repositories and use cases should be injected through constructors.

---

# 21. Testing Strategy

## Unit Tests

Test:

* use cases
* calculations
* repositories with fake data sources
* ViewModels

---

## UI Tests

Test:

* main navigation
* onboarding flow
* map screen states
* login bottom sheet
* profile settings

---

## Integration Tests

Test:

* repository + local database
* repository + remote data source
* offline fallback behavior

---

# 22. AI Coding Rules

AI-generated code must follow these rules:

* Follow MVVM and Clean Architecture
* Do not put business logic in Composables
* Do not call APIs from ViewModels directly
* Use repository interfaces
* Use immutable UI state
* Use StateFlow for screen state
* Use Hilt for dependency injection
* Use Material Design 3 components
* Place reusable UI components in core:designsystem
* Place calculations in core:domain
* Place API logic in core:network and core:data
* Add loading and error states for every screen
* Keep feature modules independent from each other

---

# 23. Recommended Implementation Order

1. Create Gradle multi-module setup
2. Create core:model
3. Create core:designsystem
4. Create core:common
5. Create navigation structure in app module
6. Create feature:onboarding
7. Create feature:discover with mock data
8. Add core:network and API clients
9. Add core:data repositories
10. Add core:database with Room
11. Create feature:windpark-detail
12. Create calculation use cases
13. Create feature:facts
14. Create feature:my-turbines
15. Add Firebase Auth
16. Add profile and settings
17. Add offline fallback
18. Add tests
