# WindNah – Implementation Status

Version: 1.1
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
  - Headline: `headlineLarge`, Abstand Headline→Body: 36 dp
  - Abstand Bild→Text: 34 dp
  - Seiten 1 & 2: rechts-ausgerichteter Pill-Button „Weiter" (Icon 18 dp)
  - Seite 3: zentrierte Buttons (241 dp breit) — „Standort freigeben & starten" + „ohne Standort starten" (direkt completeOnboarding) + einzeiliger Footer-Hinweis
  - Kein Überspringen-Button, keine Page-Dots (entspricht Figma)
- `OnboardingViewModel` — speichert Onboarding-Abschluss in DataStore
- First-launch detection: erste App-Öffnung → Onboarding, danach direkt Entdecken
- `LaunchScreen` — Branding-Splash (Figma 120:2155); Logo + App-Name + `CircularProgressIndicator` solange DataStore lädt
- Drawable-Assets: `onboarding_media_1/2/3.png` (aus Figma exportiert)

### feature:profile
- `ProfileScreen` — Settings-Screen nach Figma, vollständig überarbeitet (2026-06-21)
  - TopAppBar mit zweizelligem Titel: „Profil" + Untertitel „Einstellungen & Konto"
  - Sektion **Darstellung**: Dark Mode Switch + Sprache-Eintrag (Stub)
  - Sektion **Berechtigungen**: Standort-Toggle + Benachrichtigungen-Toggle (Stubs)
  - Sektion **Datenansicht anpassen**: Live-Stromproduktion / CO₂-Einsparung / Versorgte Haushalte (Toggles, Stubs)
  - Sektion **Allgemein**: Datenschutz / Datenquellen / Über diese App / Hilfe & Support / Feedback — je mit Chevron-Icon
  - Sektion **Konto**: „Anmelden"-Button → öffnet `LoginBottomSheet`
  - App-Info Card (grüner Hintergrund `#1A3F6836`) mit Versionsinfo
  - „Abmelden"-Button (OutlinedButton, Error-Farbe)
- `ProfileViewModel` (`@HiltViewModel`) — liest/schreibt `isDarkModeEnabled` via `UserPreferencesRepository`
- `LoginBottomSheet` (private Composable in ProfileScreen) — Google (Stub) + E-Mail-Login + „Nicht jetzt"

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
- **Figma-Polishing**: Layers-FAB (`#3C4B37`, 40dp, top=190dp); Marker-Badge oben links; Filter-Chip Radius 16dp; Chip-Spacing 10dp; Bundesland-Chip Padding korrekt (start=8, end=16dp)
- **Bottom Sheet** (2026-06-21 überarbeitet): Drag-Handle (40×4dp, `#C3C8BC`), asymmetrischer Radius (top=24dp, bottom=16dp), Thumbnail 118×106dp / Radius 24dp, Pill-Button „Details ansehen", MetricCapsule Radius 24dp / Farbe `#1AC0EFB0`, „Windräder" statt „Anlagen"
- **MarkerHintSnackbar** (2026-06-21): 181×32dp, Farbe `#53634E`, Radius 16dp, kein Close-Button
- **Kein Gradient-Overlay** über der Karte (entfernt 2026-06-21)
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
  - Hero-Header 208dp: dunkler Gradient-Overlay, Name + Standort, Zurück-Button (padding 8dp), Bookmark + Teilen (40dp, Radius 20dp)
  - `SecondaryTabRow`: Tab „Übersicht" + Tab „Windräder Details"
  - **Übersicht-Tab** (2026-06-21 komplett neu nach Figma):
    - `OutputCard`: aktueller Output in MW (30sp Bold), Progress-Bar (104dp, grün), Kapazitätsprozent, „X MW max."
    - `WindstaerkeCard`: Air-Icon, Windgeschwindigkeit (24sp, blaugrün), Windklassifizierung + Ø-Jahresdurchschnitt rechts
    - `MetricsGrid`: 2×2 Grid — Stromproduktion (GWh), Haushalte, CO₂ (t), Lokaler Anteil (%) — je mit Icon, Wert, Subtext
    - `KommunaleEinnahmenCard`: Einnahmen in € (24sp Bold), grüner Hintergrund
  - Windräder-Details-Tab: horizontale `LazyRow` + `GroessenvergleichCard` mit horizontalen Rasterlinien (Canvas, gestrichelt)
  - TurbineCard: Padding 16dp, Bookmark-Icon statt Herz
  - `avgHubHeightM` berechnet aus echten MaStR-Turbinendaten
- NavGraph: `ROUTE_WIND_FARM_DETAIL` zeigt `WindFarmDetailScreen`

## Nachträgliche Verbesserungen (2026-06-21)
- **OSM-Kartenfix**: `ACCESS_NETWORK_STATE`-Permission + User-Agent + `XYTileSource` statt `MAPNIK`; emulator braucht `-dns-server 8.8.8.8,8.8.4.4`
- **Filter-Chips**: `FilterChip` durch `Surface`+`Row` ersetzt (Material3 ignoriert `containerColor` bei `selected=false`)
- **Größere Marker**: 50 % größer (144px Cluster, 152px selected, 128px normal) für 50+ Zielgruppe
- **Cluster Drill-Down**: Klick auf Cluster-Marker zoomt per `haversineKm`-Bounding-Box rein bis Einzelmarker sichtbar
- **URL-Encoding Navigation**: `windFarmDetailRoute()` URL-encodiert IDs; Repository decodiert vor Cache-Lookup (behebt Umlaute-Fehler)
- **Preview-Sheet X-Button**: Fix — Icon nicht mehr überlappend mit Titel
- **Placeholder-Thumbnail**: Grüner Gradient + Air-Icon im Preview-Sheet
- **Detail-Screen**: Background `#F8FBF1`, CO₂-Subtext mit Pkw-Vergleich, dynamische Windstärke

## Offen (Future Milestones)
- Produktionsverlauf-Chart (M6)
- Lärmschätzung-Simulation (M6)
- Transparenz-Overlays (M6)
- Carousel (M3 Multi-browse) statt LazyRow für Turbinenkarten (M6)
- Detail-Screen Figma-Polishing: System-Status-Bar transparent (kein grauer Balken oben)

---

# Milestone 5 – Data Integration ✅ DONE

Duration: Days 9–15

## Implemented

- **`core:network`** vollständig aufgebaut (OkHttp 4.12, Retrofit 2.11 für DWD)
- **MaStR SOAP Client** (`MastrSoapClient`, `MastrWindUnitDto`, `MastrRemoteDataSource`)
  - SOAP Webdienst (kein REST/OData): `https://www.marktstammdatenregister.de/MaStRApi/Api.svc/Soap11/Anlage`
  - Zwei-Schritt-Strategie: `GetGefilterteListeStromErzeuger(energietraeger=Wind)` → EinheitMastrNummern → `GetEinheitWind` pro Einheit
  - Auth: `apiKey` + `marktakteurMastrNummer` = `SOM961179242694` im SOAP-Body (korrekter Namespace)
  - `XmlPullParser` für XML-Parsing (Android built-in, kein externes Parsing-Framework)
  - Max. 1 Seite × 100 Detailabfragen parallel (chunks à 20) für schnelle Ladezeiten; Fallback auf Mock-Daten
  - Felder: Name, Gemeinde, Bundesland, Koordinaten, Nennleistung, Rotordurchmesser, Nabenhöhe, Betriebsstatus, Hersteller, Typ, Inbetriebnahmedatum
  - `MastrApiService.kt` (REST-Interface) existiert nicht mehr — wurde durch SOAP ersetzt
- **DWD API Client** via BrightSky (`DwdApiService`, `BrightSkyWeatherDto`, `DwdRemoteDataSource`)
  - BrightSky: `https://api.brightsky.dev/current_weather` (freier DWD-Wrapper)
  - Liefert: Windgeschwindigkeit (m/s), Windrichtung (°), Zeitstempel
- **`NetworkModule`** (Hilt, `SingletonComponent`) — `MastrSoapClient` (OkHttp) + DWD Retrofit
- **Mapper** (`MastrMapper`, `DwdMapper`)
  - MaStR-Turbinen → Windpark-Aggregation nach `NameWindpark`
  - Windpark-ID-Generierung: `windfarm_{bundesland}_{name}_{lat}_{lon}`
  - Basismetriken aus Stammdaten: Jahresproduktion (∅ 2000 Volllaststunden), Haushalte (÷3500 kWh), CO₂ (UBA-Faktor 354 g/kWh)
- **`WindFarmRepositoryImpl`** — ersetzt `FakeWindFarmRepository` als primäre Impl
  - Session-Cache für MaStR-Daten; `runCatching`-Fallback auf Mock-Daten
- **`WeatherRepository`** Interface (`core:domain`) + `WeatherRepositoryImpl` (`core:data`)
- **`DataModule`** aktualisiert: bindet `WindFarmRepositoryImpl` + `WeatherRepositoryImpl`
- **`GetWindFarmDetailUseCase`** erweitert:
  - Lädt DWD-Wetter parallel per `coroutineScope { async { } }`
  - Berechnet `estimatedCurrentOutputKw` per Turbine: Hellmann-Exponent (α=0.14), kubische Leistungskurve (Cut-In 3 m/s, Rated 12 m/s), 85% Wake-Efficiency
  - `WindFarmDetail.weather: WeatherData?` — DWD-Daten fließen in Detail-Screen
- **`WindFarmDetail`** Modell erweitert: `weather: WeatherData? = null`

## Offen (Future Milestones)
- Room-Caching für Offline-Betrieb → M8
- Paginierung für große MaStR-Abfragen (>2000 Anlagen) → M8

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
| Discover lädt echte MaStR-Daten via SOAP | Fallback auf Mock-Daten bei API-Fehler |
| Google Sign-In / Firebase Auth nicht integriert | Auth-Screens sind UI-Stubs → M7 |
| Windpark-Thumbnail im Bottom Sheet: Gradient-Placeholder | Echtes Foto fehlt (kein Asset) |
| Turbinenkarten: LazyRow statt M3-Carousel | Carousel-Peek-Ansicht → M6 |
| Profil: Benutzerprofil-Karte mit Avatar/Stats fehlt | Erfordert Auth → M7 |
| Login ist Bottom Sheet statt vollständiger Screen | Vollständiges Login-Formular → M7 |
| localEnergyContributionPercent + municipalRevenueEurPerYear | Werden in M6 berechnet; derzeit null → UI zeigt „–" |

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
