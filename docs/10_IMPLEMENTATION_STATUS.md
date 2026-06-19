# WindNah – Implementation Status

Version: 1.0
 Last Updated: 2026-06-19

---

# Milestone 1 – Project Foundation ✅ DONE

Duration: Days 1–3

## Build & Tooling

| Tool | Version | Notes |
|------|---------|-------|
| AGP | 8.13.2 | Downgraded from 9.x — Hilt 2.56.1 incompatible with AGP 9.x |
| Kotlin | 2.2.10 | |
| KSP | 2.2.10-2.0.2 | Kotlin 2.x format: `{kotlin}-2.0.{n}` |
| Hilt | 2.56.1 | |
| DataStore | 1.1.1 | |
| Coroutines | 1.9.0 | |

`gradle.properties`: `android.useAndroidX=true` required.

---

## Delivered

### core:model
- `WindFarm`
- `WindTurbine`
- `WeatherData`
- `EnergyMetrics`
- `WindFarmStatus` (IN_BETRIEB, IN_WARTUNG, IN_PLANUNG, STILLGELEGT)

### core:common
- `sealed class Result<T>` (Success, Error, Loading)

### core:domain
- `UserPreferencesRepository` interface
  - `hasCompletedOnboarding: Flow<Boolean>`
  - `isDarkModeEnabled: Flow<Boolean>`
  - `suspend fun setOnboardingCompleted()`
  - `suspend fun setDarkModeEnabled(Boolean)`

### core:data
- `UserPreferencesRepositoryImpl` (DataStore<Preferences>)
  - Key `onboarding_completed` (Boolean, default false)
  - Key `dark_mode_enabled` (Boolean, default false = Light)
- `DataModule` (Hilt, `@InstallIn(SingletonComponent::class)`)

### core:designsystem
- `StatusChip`
- `WindNahMetricCard`
- `FactCard`
- `WindFarmPreviewCard`

### app
- `WindNahTheme` (Material 3, WindNah green palette)
- `AppViewModel` — exposes `StateFlow<String?>` startDestination + `StateFlow<Boolean>` darkModeEnabled
- `MainActivity` — `@AndroidEntryPoint`, zeigt `LaunchScreen` während `startDestination == null`, danach `WindNahApp`; übergibt `darkTheme = darkModeEnabled` an `WindNahTheme`
- `WindNahNavGraph` — routes: `onboarding`, `discover`, `facts`, `my_turbines`, `profile`, `wind_farm_detail/{windFarmId}`, `login`, `register`
- Bottom Navigation — 4 tabs: Entdecken / Fakten / Meine Anlagen / Profil (hidden on onboarding)

---

# Milestone 2 – Core Navigation & UI ✅ DONE

Duration: Days 3–6

## Delivered

### feature:onboarding
- `OnboardingScreen` — 3-page HorizontalPager, visuell nach Figma (nodes 120:2152 / 120:2149 / 120:2130)
  - Header: WindNah-Logo (40 dp) + App-Name (titleMedium)
  - Media-Bilder pro Seite (330 dp, 24 dp Radius)
  - Seiten 1 & 2: rechts-ausgerichteter Pill-Button „Weiter"
  - Seite 3: „Standort freigeben & starten" (ACCESS_COARSE_LOCATION → completeOnboarding) + „ohne Standort starten" (direkt completeOnboarding) + Footer-Hinweis
  - Kein Überspringen-Button, keine Page-Dots (entspricht Figma)
- `OnboardingViewModel` — speichert Onboarding-Abschluss in DataStore
- First-launch detection: erste App-Öffnung → Onboarding, danach direkt Entdecken
- `LaunchScreen` — Branding-Splash (Figma 120:2155); Logo + App-Name + `CircularProgressIndicator` solange DataStore lädt
- Drawable-Assets: `onboarding_media_1/2/3.png` (aus Figma exportiert)

### feature:profile
- `ProfileScreen` — vollständiger Settings-Screen
  - Sektion **Erscheinungsbild**: Dark Mode Switch → schreibt DataStore via `ProfileViewModel`
  - Sektion **Konto**: „Anmelden"-Button → öffnet `LoginBottomSheet` (lokal, kein Route)
  - Sektion **Datenquellen**: MaStR + DWD Info-Texte
  - Sektion **Über die App**: Version + Impressum-Placeholder
- `ProfileViewModel` (`@HiltViewModel`) — liest/schreibt `isDarkModeEnabled` via `UserPreferencesRepository`
- `LoginBottomSheet` (private Composable in ProfileScreen) — Google (Stub/M5) + E-Mail-Login + „Nicht jetzt"

### feature:auth
- `LoginScreen` — E-Mail + Passwort + Google-Button (Stub/M5) + Link zu Registrierung
- `RegistrationScreen` — Name + E-Mail + Passwort
- Build-Config: Hilt, KSP, `hilt-navigation-compose`, `lifecycle-viewmodel-compose`, `material-icons-extended` ergänzt

## Definition of Done ✅
Alle Screens erreichbar. Navigation Graph komplett.

---

# Milestone 3 – Wind Farm Discovery ⏳ NOT STARTED

Duration: Days 5–9

## Planned

- Google Maps Integration
- Marker-Rendering + Clustering
- Suche nach Ort / PLZ
- Filter: Status, Bundesland
- Wind Farm Preview Card (Bottom Sheet nach Marker-Tap)

---

# Milestone 4 – Wind Farm Details ⏳ NOT STARTED

Duration: Days 8–13

## Planned

- `WindFarmDetailScreen`
- Tab: Übersicht (Metriken)
- Tab: Turbinen-Details
- Produktionsverlauf (Chart)
- Lärmschätzung (Simulation)
- Transparenz-Overlays

---

# Milestone 5 – Data Integration ⏳ NOT STARTED

Duration: Days 9–15

## Planned

- MaStR API Client (Windpark-Stammdaten)
- DWD API Client (Wetterdaten / Windgeschwindigkeit)
- `core:network` Retrofit-Setup
- Repository-Implementierungen mit echten API-Daten

---

# Milestone 6 – Calculations ⏳ NOT STARTED

Duration: Days 12–17

## Planned

- `CalculateCurrentOutputUseCase`
- `CalculateHouseholdsSuppliedUseCase`
- `CalculateCo2SavingsUseCase`
- `CalculateMunicipalRevenueUseCase`
- `CalculateLocalEnergyContributionUseCase`
- `CalculateNoiseEstimateUseCase`
- `CalculateAnnualProductionUseCase`

---

# Milestone 7 – Facts & Favorites ⏳ NOT STARTED

Duration: Days 15–18

## Planned

- `FactsScreen` mit Kategorien und Artikeln
- `FactDetailScreen`
- `MyTurbinesScreen` mit Favoriten + zuletzt angesehen
- `FavoriteRepository` (lokal + Firebase Sync)

---

# Milestone 8 – Offline Support ⏳ NOT STARTED

Duration: Days 17–19

## Planned

- Room-Datenbank (`core:database`)
- Caching: Windparks, Fakten, Favoriten, zuletzt angesehen
- Offline-Fehlermeldungen

---

# Milestone 9 – Polish ⏳ NOT STARTED

Duration: Days 19–21

## Planned

- Accessibility (TalkBack, Dynamic Fonts, 48dp Touch Targets)
- Dark Mode vollständig
- Bug Fixes
- Performance

---

# Known Issues / Tech Debt

| Issue | Status |
|-------|--------|
| Placeholder-Screens: Discover, Facts, MyTurbines | Werden in M3/M7 ersetzt |
| Google Sign-In / Firebase Auth nicht integriert | Geplant für M5 — Auth-Screens sind UI-Stubs |

---

# Key File Locations

| Was | Pfad |
|-----|------|
| Nav Graph + Routen | `app/src/main/java/com/example/windnah/navigation/WindNahNavGraph.kt` |
| AppViewModel | `app/src/main/java/com/example/windnah/AppViewModel.kt` |
| MainActivity | `app/src/main/java/com/example/windnah/MainActivity.kt` |
| OnboardingScreen | `feature/onboarding/src/main/java/com/windnah/feature/onboarding/OnboardingScreen.kt` |
| LaunchScreen | `feature/onboarding/src/main/java/com/windnah/feature/onboarding/LaunchScreen.kt` |
| ProfileScreen | `feature/profile/src/main/java/com/windnah/feature/profile/ProfileScreen.kt` |
| ProfileViewModel | `feature/profile/src/main/java/com/windnah/feature/profile/ProfileViewModel.kt` |
| LoginScreen | `feature/auth/src/main/java/com/windnah/feature/auth/LoginScreen.kt` |
| RegistrationScreen | `feature/auth/src/main/java/com/windnah/feature/auth/RegistrationScreen.kt` |
| DataModule (Hilt) | `core/data/src/main/java/com/windnah/core/data/di/DataModule.kt` |
| UserPrefsRepo Interface | `core/domain/src/main/java/com/windnah/core/domain/repository/UserPreferencesRepository.kt` |
| UserPrefsRepo Impl | `core/data/src/main/java/com/windnah/core/data/repository/UserPreferencesRepositoryImpl.kt` |
| Version Catalog | `gradle/libs.versions.toml` |
