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
  - `suspend fun setOnboardingCompleted()`

### core:data
- `UserPreferencesRepositoryImpl` (DataStore<Preferences>)
- `DataModule` (Hilt, `@InstallIn(SingletonComponent::class)`)

### core:designsystem
- `StatusChip`
- `WindNahMetricCard`
- `FactCard`
- `WindFarmPreviewCard`

### app
- `WindNahTheme` (Material 3, WindNah green palette)
- `AppViewModel` — reads onboarding state, exposes `StateFlow<String?>` startDestination
- `MainActivity` — `@AndroidEntryPoint`, zeigt `LaunchScreen` während `startDestination == null`, danach `WindNahApp`
- `WindNahNavGraph` — routes: `onboarding`, `discover`, `facts`, `my_turbines`, `profile`, `wind_farm_detail/{windFarmId}`
- Bottom Navigation — 4 tabs: Entdecken / Fakten / Meine Anlagen / Profil (hidden on onboarding)

---

# Milestone 2 – Core Navigation & UI 🔄 IN PROGRESS

Duration: Days 3–6

## Delivered

### feature:onboarding
- `OnboardingScreen` — 3-page HorizontalPager
  - Seite 1: Air-Icon — "Willkommen bei WindNah"
  - Seite 2: Map-Icon — "Erkunde die Karte"
  - Seite 3: Lightbulb-Icon — "Verstehe die Fakten"
  - Buttons: "Überspringen" / "Weiter" / "Loslegen"
  - Page indicator dots
- `OnboardingViewModel` — speichert Onboarding-Abschluss in DataStore
- First-launch detection: erste App-Öffnung → Onboarding, danach direkt Entdecken
- `LaunchScreen` — Branding-Splash (Figma 120:2155); Logo + App-Name + `CircularProgressIndicator` solange DataStore lädt

## Pending

### feature:profile
- [ ] `ProfileScreen` — vollständiger Settings-Screen
  - Sektionen: Erscheinungsbild (Dark Mode), Datenquellen, Impressum
  - Aktuell: Placeholder

### feature:auth
- [ ] `LoginBottomSheet` — kontextuelles Login (Google / E-Mail / "Nicht jetzt")
- [ ] `LoginScreen` — E-Mail + Passwort + Google, Link zu Registrierung
- [ ] `RegistrationScreen` — Name + E-Mail + Passwort

### Navigation
- [ ] Routes für `login` und `register` im NavGraph eintragen

## Definition of Done
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
| `feature:auth` build.gradle.kts fehlt Hilt + KSP + icons-extended | Pending — benötigt vor Auth-Screen-Implementierung |
| `feature:profile` build.gradle.kts fehlt icons-extended | Pending |
| Placeholder-Screens: Discover, Facts, MyTurbines | Werden in M3/M7 ersetzt |

---

# Key File Locations

| Was | Pfad |
|-----|------|
| Nav Graph + Routen | `app/src/main/java/com/example/windnah/navigation/WindNahNavGraph.kt` |
| AppViewModel | `app/src/main/java/com/example/windnah/AppViewModel.kt` |
| MainActivity | `app/src/main/java/com/example/windnah/MainActivity.kt` |
| OnboardingScreen | `feature/onboarding/src/main/java/com/windnah/feature/onboarding/OnboardingScreen.kt` |
| LaunchScreen | `feature/onboarding/src/main/java/com/windnah/feature/onboarding/LaunchScreen.kt` |
| DataModule (Hilt) | `core/data/src/main/java/com/windnah/core/data/di/DataModule.kt` |
| UserPrefsRepo Interface | `core/domain/src/main/java/com/windnah/core/domain/repository/UserPreferencesRepository.kt` |
| UserPrefsRepo Impl | `core/data/src/main/java/com/windnah/core/data/repository/UserPreferencesRepositoryImpl.kt` |
| Version Catalog | `gradle/libs.versions.toml` |
