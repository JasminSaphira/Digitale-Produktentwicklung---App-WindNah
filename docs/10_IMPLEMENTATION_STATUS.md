# WindNah – Implementation Status

Version: 1.0
 Last Updated: 2026-06-21

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

# Milestone 3 – Wind Farm Discovery ✅ DONE

Duration: Days 5–9

## Delivered

- Discover/Entdecken UI visuell an Figma node `138-2193` ausgerichtet, unter Beachtung von Material 3 und dem WindNah Design System
- Google Maps wurde entfernt
- Discover nutzt jetzt einen OpenStreetMap-basierten Ansatz mit `osmdroid`
- Discover UI weiter in Richtung Figma node `138-2193` poliert: saubereres Suchfeld, klarere Abstaende, verfeinerte Chips/FABs und verbesserte Empty/Loading/Error States
- Map-first Layout mit Suchfeld, Status-Filtern und Bundesland-Auswahlliste
- Windpark-Marker mit Mock-Daten
- Marker-Tap oeffnet eine visuell ueberarbeitete Bottom Preview / Bottom Sheet
- Preview zeigt Name, Ort, Status, Anlagenzahl, installierte Leistung, versorgte Haushalte und CO2-Einsparung
- CTA "Details ansehen" navigiert ueber bestehende Route `wind_farm_detail/{windFarmId}`
- `DiscoverViewModel`, `DiscoverUiState` und `DiscoverUiEvent` eingefuehrt
- Mock-Windparkdaten liegen hinter `WindFarmRepository` / `GetDiscoverWindFarmsUseCase`
- Region-, Status- sowie ZIP-/Ortssuche filtern die tatsaechlich angezeigten Windparks ueber ViewModel/UseCase
- Ein Material-3-Recenter-Button wurde ergaenzt; Standort bleibt optional, wird nicht gespeichert und nur fuer das Zentrieren verwendet
- **Marker-Clustering**: Zoom-basiertes geografisches Clustering via Haversine-Distanz (nativ in Kotlin, kein bonuspack); Cluster-Marker zeigt Anzahl der Windparks; Tap auf Cluster zoomt hinein
- **Figma-Polishing**: Layers-FAB (`#3C4B37`, 40dp, oben rechts) wiederhergestellt; Marker-Badge oben links mit Markerfarbe als Textfarbe (analog Figma); Filter-Chip Shadow; „In Wartung"-Textfarbe weiß; Snackbar-Umlaut; SearchBar-Position direkt unter Statusbar
- **osmdroid Cache-Initialisierung**: `WindNahApplication.onCreate()` setzt `osmdroidBasePath` und `osmdroidTileCache` auf internen App-Cache → Karte lädt Tiles korrekt

## Deferred to Later Milestones

- Real MaStR/DWD API integration → M5
- Vollstaendige Windpark-Detailseite → M4

---

# Milestone 4 – Wind Farm Details ✅ DONE

Duration: Days 8–13

## Implemented

- `WindFarmDetail` domain model (`core:model`)
- `WindFarmRepository.observeWindFarmDetail()` + `GetWindFarmDetailUseCase` (`core:domain`)
- `FakeWindFarmRepository` extended: 5 Windparks × mock Turbinen (Enercon, Vestas, Siemens Gamesa, Nordex)
- `WindFarmDetailViewModel` mit `SavedStateHandle` (windFarmId aus NavArgs), Hilt, StateFlow
- `WindFarmDetailScreen` nach Figma (nodes 61:916 + 75:1338):
  - Hero-Header 208dp: dunkler Gradient-Overlay, Name + Standort, Zurück-Button, Favorit + Teilen
  - `SecondaryTabRow`: Tab „Übersicht" + Tab „Windräder Details"
  - Übersicht-Tab: StatusChip, Metriken-Karte (MW / Haushalte / CO₂), Größenvergleich-Card mit Balkendiagramm (Windrad 220m, Kölner Dom 157m, Berliner Fernsehturm 368m, Dresdner Frauenkirche 91m)
  - Windräder-Details-Tab: horizontale `LazyRow` mit scrollbaren Turbinen-Karten (Status, Modell, MW, Rotor, Nabenhöhe, Baujahr, Betreiber)
- NavGraph: `ROUTE_WIND_FARM_DETAIL` zeigt `WindFarmDetailScreen` (ersetzt Placeholder-Box)

## Offen (Future Milestones)
- Produktionsverlauf-Chart (M5/M6)
- Lärmschätzung-Simulation (M6)
- Transparenz-Overlays (M6)
- Reale Daten via MaStR-API (M5)

---

# Milestone 5 – Data Integration ✅ DONE

Duration: Days 9–15

## Implemented

- **`core:network`** vollständig aufgebaut (Retrofit 2.11, OkHttp 4.12, Kotlinx Serialization 1.8)
- **MaStR API Client** (`MastrApiService`, `MastrWindUnitDto`, `MastrRemoteDataSource`)
  - OData REST-API: `https://www.marktstammdatenregister.de/MaStRAPI/wapi/mastr/EinheitWind`
  - Felder: Name, Gemeinde, Bundesland, Koordinaten, Nennleistung, Rotor, Nabenhöhe, Betriebsstatus, Hersteller, Typ
- **DWD API Client** via BrightSky (`DwdApiService`, `BrightSkyWeatherDto`, `DwdRemoteDataSource`)
  - BrightSky: `https://api.brightsky.dev/current_weather` (freier DWD-Wrapper)
  - Liefert: Windgeschwindigkeit (m/s), Windrichtung (°), Zeitstempel
- **`NetworkModule`** (Hilt, `SingletonComponent`) — zwei separate Retrofit-Instanzen (`@Named`)
- **Mapper** (`MastrMapper`, `DwdMapper`)
  - MaStR-Turbinen → Windpark-Aggregation nach `NameWindpark`
  - Windpark-ID-Generierung: `windfarm_{bundesland}_{name}_{lat}_{lon}`
  - Basismetriken aus Stammdaten: Jahresproduktion (∅ 2000 Volllaststunden), Haushalte (÷3500 kWh), CO₂ (UBA-Faktor 354 g/kWh)
  - Rotordurchmesser = Rotorblattlänge × 2
- **`WindFarmRepositoryImpl`** — ersetzt `FakeWindFarmRepository` als primäre Impl
  - Session-Cache für MaStR-Daten (Stammdaten ändern sich selten)
  - Fallback-kompatibel: `FakeWindFarmRepository` bleibt im Code für Tests
- **`WeatherRepository`** Interface (`core:domain`) + `WeatherRepositoryImpl` (`core:data`)
- **`DataModule`** aktualisiert: bindet `WindFarmRepositoryImpl` + `WeatherRepositoryImpl`

## Offen (Future Milestones)
- DWD-Windgeschwindigkeit in Echtzeit-Leistungsberechnung einfließen lassen → M6
- Room-Caching für Offline-Betrieb → M8
- Paginierung für große MaStR-Abfragen (>500 Anlagen) → M8

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
| Placeholder-Screens: Facts, MyTurbines | Werden in M7 ersetzt |
| Discover nutzt Mock-Windparkdaten | Real-Datenintegration fuer M5 geplant |
| Google Sign-In / Firebase Auth nicht integriert | Geplant für M5 — Auth-Screens sind UI-Stubs |

---

# Key File Locations

| Was | Pfad |
|-----|------|
| Nav Graph + Routen | `app/src/main/java/com/example/windnah/navigation/WindNahNavGraph.kt` |
| AppViewModel | `app/src/main/java/com/example/windnah/AppViewModel.kt` |
| MainActivity | `app/src/main/java/com/example/windnah/MainActivity.kt` |
| OnboardingScreen | `feature/onboarding/src/main/java/com/windnah/feature/onboarding/OnboardingScreen.kt` |
| DiscoverScreen | `feature/discover/src/main/java/com/windnah/feature/discover/DiscoverScreen.kt` |
| DiscoverViewModel | `feature/discover/src/main/java/com/windnah/feature/discover/DiscoverViewModel.kt` |
| LaunchScreen | `feature/onboarding/src/main/java/com/windnah/feature/onboarding/LaunchScreen.kt` |
| ProfileScreen | `feature/profile/src/main/java/com/windnah/feature/profile/ProfileScreen.kt` |
| ProfileViewModel | `feature/profile/src/main/java/com/windnah/feature/profile/ProfileViewModel.kt` |
| LoginScreen | `feature/auth/src/main/java/com/windnah/feature/auth/LoginScreen.kt` |
| RegistrationScreen | `feature/auth/src/main/java/com/windnah/feature/auth/RegistrationScreen.kt` |
| DataModule (Hilt) | `core/data/src/main/java/com/windnah/core/data/di/DataModule.kt` |
| UserPrefsRepo Interface | `core/domain/src/main/java/com/windnah/core/domain/repository/UserPreferencesRepository.kt` |
| UserPrefsRepo Impl | `core/data/src/main/java/com/windnah/core/data/repository/UserPreferencesRepositoryImpl.kt` |
| Version Catalog | `gradle/libs.versions.toml` |
