# WindNah – Implementation Status

Version: 1.2
Last Updated: 2026-06-27

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

Profil-Update 2026-06-26:
- Profil-Redesign nach Figma node `680:7549`: Profilkarte mit Avatar/Stats, prominente App-Info-Karte, Kontakt-Section, Datenschutz-Hinweis und kartenbasierte Settings-Listen
- Standort-Switch steuert die WindNah-interne Standortnutzung und fragt bei Aktivierung `ACCESS_COARSE_LOCATION` an
- Metrik-Switches steuern die Sichtbarkeit von Live-Stromproduktion, CO2-Einsparung und versorgten Haushalten im Windpark-Detail-Screen
- `ProfileViewModel` liest/schreibt Dark Mode, Standortnutzung und Metrik-Sichtbarkeit via `UserPreferencesRepository`

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
- Suche ist umlaut-tolerant (ae/ä, oe/ö, ue/ü, ss/ß werden beidseitig normalisiert)
- **Live-Suchvorschlaege** (2026-06-27): waehrend des Tippens erscheint unter dem Suchfeld eine Dropdown-Liste passender Windparks (Name + Ort, max. 6); Tap auf einen Vorschlag zentriert die Karte auf den Park und oeffnet die Vorschau; die Liste schliesst sich nach Auswahl. `WindFarmSelected` hat dafuer ein `recenter`-Flag (true aus Vorschlaegen, false bei Marker-Taps)
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

# Milestone 6 - Transparency & Calculations DONE

Duration: Days 12–17

Status Update 2026-06-26: Implemented / DONE

## Implemented

- `CalculateCurrentOutputUseCase`
- `CalculateHouseholdsSuppliedUseCase`
- `CalculateCo2SavingsUseCase`
- `CalculateMunicipalRevenueUseCase`
- `CalculateLocalEnergyContributionUseCase`
- `CalculateNoiseEstimateUseCase`
- `CalculateAnnualProductionUseCase`
- Zentrale Berechnungslogik in `WindFarmMetricCalculator` (`core:domain`) eingefuehrt
- `EnergyMetrics` um `estimatedNoiseLevelDbA` erweitert
- `GetWindFarmDetailUseCase` rechnet Detail-Kennzahlen jetzt konsistent aus:
  - aktueller Output auf Basis von DWD/BrightSky-Winddaten, Hubhoehe, Nennleistung und Turbinenstatus
  - Jahresproduktion per Volllaststunden-Modell
  - versorgte Haushalte, CO2-Einsparung, lokaler Energiebeitrag und kommunale Einnahmen
  - bildungsorientierte Laermschaetzung in dB(A)
- MaStR-Mapping und Mock-Daten liefern keine `null`-Platzhalter mehr fuer `localEnergyContributionPercent` und `municipalRevenueEurPerYear`
- Detail-Screen zeigt neue Laermschaetzungs-Karte und kompakte Transparenz-Zusammenfassung fuer die Kennzahlen
- Transparenztexte liegen in `WindFarmMetricTransparency` und benennen Formelannahmen, Datenquellen und Aktualisierungsrhythmus

## Verification

- `:core:domain:test` erfolgreich
- `:core:data:test` erfolgreich
- `:feature:windpark-detail:compileDebugKotlin` erfolgreich
- Voller `./gradlew test` wird aktuell noch durch bestehende fehlerhafte Onboarding-PNGs blockiert (`onboarding_media_1/2/3.png`: keine gueltige PNG-Signatur)

---

# Milestone 7 – Facts, Favorites & Meine Anlagen ✅ DONE

Duration: Days 15–18

## Done

### Facts

- `FactsScreen` ersetzt den bisherigen Placeholder und ist visuell an Figma node `311-3632` angelehnt
- Fakten liegen als lokale Markdown-Dateien unter `core:data/src/main/assets/facts/`
- Markdown-Struktur nutzt eine Datei pro Kategorie:
  - `natur-und-umwelt.md`
  - `mensch-und-gesundheit.md`
  - `energie-und-technik.md`
  - `wirtschaft-und-gesellschaft.md`
- Jede Kategorie-Datei enthaelt mehrere Faktenbloecke mit stabiler ID, Mythos, Titel, Quellen und Erklaerung
- `FactArticle` und `FactCategory` im `core:model` eingefuehrt
- `FactRepository` und `GetFactsUseCase` im Domain-Layer eingefuehrt
- `LocalMarkdownFactRepository` liest Android Assets und parst Frontmatter ohne externe Dependency
- `FactsViewModel` und `FactsUiState` laden Fakten ueber Repository/UseCase
- Kategorie-Tabs filtern ueber UI-State; `Alle` zeigt alle lokal abgelegten Fakten
- `FactCard` zeigt Mythos, Fakt-Erklaerung und Quellenchips

### Favorites

- Lokale Favoriten-Persistenz via Room im `core:database`-Modul eingefuehrt
- `FavoriteRepository` im Domain-Layer und lokale Implementierung im Data-Layer eingefuehrt
- Windpark-Detail-Stern speichert und entfernt Favoriten persistent ohne Login-Zwang
- `MyTurbinesViewModel` liest gespeicherte Favoriten als Vorbereitung fuer die vollstaendige Meine-Anlagen-Liste

### Meine Anlagen (2026-06-26)

- `RecentlyViewedRepository` im Domain-Layer mit `observeRecentlyViewedIds(limit)` und `recordViewed(windFarmId)`
- `RecentlyViewedEntity` und `RecentlyViewedDao` im `core:database`-Modul
- Room-Datenbank auf Version 2 erhöht; Migration `1 → 2` legt Tabelle `recently_viewed_wind_farms` an
- `RecentlyViewedRepositoryImpl` im Data-Layer; trimmt die Liste nach jedem `recordViewed` auf maximal 10 Einträge
- Hilt-Binding und DAO-Provider in `DataModule` ergänzt
- `WindFarmDetailViewModel` ruft einmalig pro Detailöffnung `recordViewed(windFarmId)` im `init`-Block auf
- `MyTurbinesViewModel` kombiniert `favoriteIds`, `recentlyViewedIds` und `observeWindFarmPreviews()` zu `MyTurbinesUiState`
- `MyTurbinesScreen` nach Figma node `306:3226`:
  - Abschnitt **Gespeichert** mit Stern-Icon, Count-Badge und `FavoriteWindFarmCard` (Status, Name, Ort, CO₂/Haushalte/Leistung)
  - Stern in Favoritenkarte entfernt den Favoriten per `removeFavorite()`
  - Abschnitt **Zuletzt angesehen** als Liste mit bis zu 10 echten Windparks
  - Empty States: „Noch keine Favoriten gespeichert." / „Noch keine Windparks angesehen."
  - CTA „Neue Windraeder entdecken" ruft `onNavigateToMap()` auf
  - Keine Dummy-Daten; alle Werte kommen aus echten Repository-Daten

## Planned

- `FactDetailScreen`
- Firebase Sync und Auth-Gating fuer Favoriten

---

# Milestone 8 – Offline Support ✅ DONE

Duration: Days 17–19

Status Update 2026-06-27: Implemented / DONE

## Implemented

- **Room-Cache für MaStR-Daten** (`core:database`): neue Entities `CachedWindFarmEntity` (Park + Energiemetriken flach) und `CachedTurbineEntity` (FK `windFarmId`, indiziert)
- **`CachedWindFarmDao`** mit atomarem `replaceAll()` (`@Transaction`: clear + insert), `observeWindFarms()` (Flow), `getWindFarm`/`getTurbines`/`count`
- **DB Version 2 → 3**, `MIGRATION_2_3` (drei `CREATE TABLE`/Index); in DataModule registriert + DAO-Provider ergänzt
- **`WindFarmRepositoryImpl` auf Cache-First umgestellt**:
  - emittiert sofort den persistierten Cache (offline sichtbar), refresht **immer** beim Start von MaStR
  - bei Netzfehler bleibt der Cache erhalten; `CancellationException` wird durchgereicht (kein Fehl-Fallback)
  - **Mock-Fallback entfernt**: `FakeWindFarmRepository` gelöscht; ohne Cache + offline → echte Fehlermeldung
  - Detail-Turbinen kommen aus dem Room-Cache → Detailseite funktioniert offline
  - `CacheMapper` für Entity ↔ Domain
- **Offline-Signal in der UI**: neues `WindFarmPreviewsResult` (Previews + `isStale`) durch Domain/UseCase/ViewModel; dezenter Offline-Banner ("Offline – zwischengespeicherte Daten", CloudOff-Icon) im DiscoverScreen, wenn Cache bei fehlgeschlagenem Refresh gezeigt wird
- **Wetter wird bewusst NICHT gecacht** (online-only): offline zeigt der Detail-Screen Stammdaten + ehrliches "– m/s · Keine Winddaten" statt eines veralteten Live-Werts

## Verification (manuelles UAT am Emulator, 2026-06-27)

- Online-Erstbefüllung: Karte lädt, kein Banner ✅
- Offline mit Cache: gecachte Parks + Offline-Banner ✅
- Offline-Detailseite: Stammdaten + Turbinen aus Cache, Output "Keine Winddaten" ✅
- Wieder online: Refresh erfolgreich, Banner verschwindet ✅
- Erstinstallation offline (leerer Cache): Empty-State "Windparks koennen aktuell nicht geladen werden" + Retry, keine Mock-Daten, kein Crash ✅
- Retry nach Wiederverbindung lädt alle Parks ✅
- DB-Migration 2 → 3 lief beim Update einer bestehenden v2-DB ohne Crash ✅

## Offen (Future Milestones)

- Caching von Fakten (liegen bereits als lokale Assets vor → kein Netz nötig)
- Room-Migrations-/DAO-Unit-Tests (bewusst weggelassen, da `core:database` keine Test-Infrastruktur hat → ggf. M9)

---

# Milestone 9 – Polish 🟡 GRÖSSTENTEILS DONE

Duration: Days 19–21

Status Update 2026-06-27

## Implemented

### Bugfixes
- **Onboarding-PNGs repariert**: `onboarding_media_1/2/3.png` waren in Wirklichkeit JPEG-Dateien mit `.png`-Endung und schlugen beim Android-PNG-Cruncher fehl → blockierten `./gradlew test`. Zu echtem PNG re-encodiert (gleicher 824×549-Inhalt). **Voller `./gradlew test` läuft jetzt grün.**

### Accessibility
- **Touch Targets ≥ 48dp**: Die 32dp-Icon-Buttons im Header/Preview (Zurück, Favorit, Teilen, Vorschau schließen, Favoriten-Stern) behalten optisch ihren 32dp-Kreis, bekommen aber wieder die 48dp-Default-Klickfläche (getönter Kreis in innere `Box` verschoben). Relevant für die 50+-Zielgruppe.
- **Switch-Semantik**: `ProfileSwitchRow` ist jetzt ein einzelnes `toggleable`-Element (`Role.Switch`) → TalkBack liest Titel + Untertitel + Schalter als eine Bedienelement-Einheit.
- Content-Descriptions geprüft: eigenständige Icon-Buttons haben deutsche Beschreibungen; dekorative Icons neben sprechendem Text bleiben bewusst `null` (verhindert TalkBack-Doppelung).

### Dark Mode — BEWUSST WEGGELASSEN
- Es gibt **kein dediziertes Dark-Mode-Design in Figma** (nur ein Dunkelmodus-Toggle-Label im Profil; der Styleguide definiert nur das M3-Light-Token-System).
- Ein Dark-Mode-Versuch wurde gestartet (Farb-Mapping auf M3-Tokens) und dann **auf Wunsch komplett verworfen**.
- Dunkelmodus wurde anschließend **vollständig aus der App entfernt** (alle Schichten): Dark-ColorScheme + `darkTheme`-Parameter + Dark-Farbtokens raus, Dunkelmodus-Toggle aus dem Profil entfernt, `isDarkModeEnabled`/`setDarkModeEnabled` aus `UserPreferencesRepository` (Interface, Impl, DataStore-Key) und den ViewModels entfernt. **Die App ist fest Light-Theme.**

## Offen
- **UI-Polish** (Spacing/Typography-Feinschliff gegen das Design-System) — als optional/kosmetisch eingestuft, noch nicht umgesetzt.
- Performance-Optimierungen (kein konkreter Bedarf identifiziert).

## Verification
- `./gradlew test` grün (alle Module inkl. App)
- Emulator-Smoke: App startet hell, Dunkelmodus-Toggle ist weg, Switches funktionieren

---

# Known Issues / Tech Debt

| Issue | Status |
|-------|--------|
| Placeholder-Screens: MyTurbines | ✅ Umgesetzt in M7 (Favoriten + Zuletzt angesehen) |
| Discover lädt echte MaStR-Daten via SOAP | ✅ M8: Room-Cache-First; offline werden gecachte Daten gezeigt, Mock-Fallback entfernt |
| Google Sign-In / Firebase Auth nicht integriert | Auth-Screens sind UI-Stubs → M7 |
| Windpark-Thumbnail im Bottom Sheet: Gradient-Placeholder | Echtes Foto fehlt (kein Asset) |
| Turbinenkarten: LazyRow statt M3-Carousel | Carousel-Peek-Ansicht → M6 |
| Profil: Benutzerprofil-Karte mit Avatar/Stats fehlt | Erfordert Auth → M7 |
| Login ist Bottom Sheet statt vollständiger Screen | Vollständiges Login-Formular → M7 |
| localEnergyContributionPercent + municipalRevenueEurPerYear | Werden in M6 berechnet; derzeit null → UI zeigt „–" |
| Dark Mode | ⛔ Bewusst weggelassen — kein Figma-Dark-Design vorhanden; in M9 vollständig aus der App entfernt. App ist fest Light-Theme. |

---

Resolved in M6 (2026-06-26): `localEnergyContributionPercent` und `municipalRevenueEurPerYear` werden jetzt berechnet; der Detail-Screen zeigt zusaetzlich Laermschaetzung und Transparenz-Zusammenfassung.

Note 2026-06-26: Die Profilkarte mit Avatar/Stats ist UI-seitig umgesetzt. Offen bleiben echte Auth/Profile-Daten und serverseitige Synchronisation.

---

# Geplant (nicht umgesetzt): Authentifizierung – E-Mail + Google Sign-In

Status: **NUR GEPLANT, kein Code umgesetzt** (Stand 2026-06-27)

Aktuell sind `LoginScreen` und `RegistrationScreen` reine UI-Stubs (E-Mail-/Passwort-Felder,
Google-Button, Navigation). Die `onClick`-Handler enthalten nur `TODO: Firebase Auth`. Es gibt
keine Firebase-Anbindung, kein `AuthRepository`, kein `User`-Modell und keine Session-Persistenz.

## Kosten
- **Firebase Authentication (E-Mail/Passwort + Google) ist im Spark-Plan kostenlos und unbegrenzt** —
  keine Kreditkarte nötig, keine MAU-Grenze für diesen Anwendungsfall.
- **Achtung:** NICHT auf „Identity Platform" upgraden (anderes Preismodell, erst 50k MAU frei).
- SMS-/Phone-Auth würde pro SMS kosten → wird hier **nicht** verwendet.
- Cloud-Sync von Favoriten (Firestore) wäre separat kostenpflichtig ab Quota → **nicht geplant**,
  Favoriten bleiben lokal in Room.

## Teil A – Externes Setup (manuell, nur durch den/die Entwickler:in)
1. Firebase-Projekt anlegen (~10 Min)
2. Android-App registrieren mit Package `com.example.windnah`, `google-services.json` nach `app/` legen (~5 Min)
3. Provider „E-Mail/Passwort" aktivieren (~2 Min)
4. Provider „Google" aktivieren **+ SHA-1-Fingerprint des Debug-Keystores** in Firebase hinterlegen
   (~15–20 Min, häufigste Fehlerquelle — ohne korrekten SHA-1 schlägt Google-Login still fehl)
5. OAuth-Web-Client-ID aus Firebase kopieren (für Credential Manager)

→ ~30–40 Min, SHA-1 ist der fummeligste Teil.

## Teil B – Code (in der App)
1. **Gradle**: `google-services`-Plugin, Firebase-BoM, `firebase-auth`, Credential-Manager-Deps
   (~30 Zeilen über 3 Dateien)
2. **core:model**: `User`-Modell · **core:domain**: `AuthRepository`-Interface + Result-Typen
3. **core:data**: `FirebaseAuthRepository` — E-Mail-Login/Register, Google-Credential-Flow,
   `currentUser`-Flow, Logout (der Google-Credential-Manager-Flow ist der aufwändigste Code-Teil)
4. **feature:auth**: `LoginViewModel` + `RegisterViewModel`, bestehende Screens verdrahten
   (Loading-/Fehler-States, deutsche Fehlermeldungen)
5. **Session-Gating**: App-Start liest Auth-Zustand; Profil zeigt echten User + Logout;
   Favoriten optional an User koppeln (berührt `AppViewModel`, Profil)
6. **Verifikation**: Tests + Emulator (Login, Register, Google, Logout, Fehlerfälle)

## Aufwandseinschätzung
- **E-Mail/Passwort allein**: moderat (halb so viel Code, kein SHA-1-Setup).
- **+ Google Sign-In**: verdoppelt grob den Aufwand und bringt das fehleranfällige externe Setup mit.
- **Gesamt realistisch ~1–2 fokussierte Sessions.** Größtes Risiko liegt nicht im Code, sondern
  im Firebase-/Google-Setup (SHA-1, OAuth-Client).
- **Empfohlene Reihenfolge**: erst E-Mail/Passwort lauffähig machen (in sich geschlossen),
  Google danach als separater Aufsatz — so blockiert das SHA-1-Setup nicht den ganzen Rest.

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
