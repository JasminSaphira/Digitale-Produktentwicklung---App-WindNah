# WindNah – Information Architecture

Version: 1.0
Status: Draft
Related Document: 01_PRD.md

---

# 1. Information Architecture Overview

WindNah follows a hierarchical navigation structure with four primary sections accessible through a persistent bottom navigation bar.

The architecture is designed to:

* minimize onboarding friction
* support anonymous usage
* prioritize discoverability
* provide contextual transparency
* allow progressive exploration of wind energy information

---

# 2. Application Structure

```text
WindNah
│
├── Launch
│
├── Onboarding
│
├── Main Application
│   │
│   ├── Entdecken
│   ├── Fakten
│   ├── Meine Anlagen
│   └── Profil
│
└── Authentication (Contextual)
```

---

# 3. Launch Flow

```text
Launch Screen
↓
First App Start?
│
├── Yes
│   ↓
│   Onboarding
│
└── No
    ↓
    Main Application
```

---

# 4. Onboarding Flow

```text
Onboarding 1
↓
Onboarding 2
↓
Onboarding 3
↓
Main Application
```

---

## Screen 1

Windkraft in deiner Nähe

Purpose:

Introduce local wind farm discovery.

---

## Screen 2

Mythen klären, Fakten kennen

Purpose:

Introduce fact-checking and transparency.

---

## Screen 3

Windenergie verstehen. Zukunft gestalten.

Purpose:

Explain local relevance and location functionality.

Actions:

* Standort freigeben & starten
* Ohne Standort starten

---

# 5. Main Navigation

```text
Bottom Navigation

Entdecken
Fakten
Meine Anlagen
Profil
```

Bottom navigation remains visible throughout the application except during onboarding and authentication.

---

# 6. Entdecken

Purpose:

Explore wind farms throughout Germany.

---

## Structure

```text
Entdecken
│
├── Map View
│
├── Search
│
├── Filters
│
├── Windpark Overlay
│
└── Windpark Details
```

---

## Map View

Contains:

* User location (optional)
* Wind farm markers
* Marker clustering

---

## Search

Users can search by:

* Place name
* ZIP code

---

## Filters

```text
Filters
│
├── Federal State
│
└── Status
```

### Federal State

* All
* Individual State Selection

### Status

* In Operation
* Maintenance
* Planned
* Decommissioned

---

## Wind Farm Selection Flow

```text
Map Marker
↓
Wind Farm Overlay
↓
Wind Farm Details
```

---

## Wind Farm Overlay

Displays:

* Wind farm name
* Location
* Status
* Number of turbines
* Installed capacity
* Households supplied
* CO₂ savings

Actions:

* Open Details
* Add Favorite
* Share Wind Farm

---

# 7. Wind Farm Details

```text
Wind Farm Details
│
├── Overview
└── Turbine Details
```

---

# 7.1 Overview Tab

```text
Overview
│
├── Status
├── Current Output
├── Wind Speed
├── Annual Production
├── Households
├── CO₂ Savings
├── Local Contribution
├── Municipal Revenue
├── Production History
└── Noise Simulation
```

---

## Card Hierarchy

```text
Status
↓
Current Output
↓
Wind Speed
↓
Annual Production
↓
Households
↓
CO₂ Savings
↓
Local Contribution
↓
Municipal Revenue
↓
Production History
↓
Noise Simulation
```

---

## Production History

```text
Production History
│
├── 7 Days
└── 30 Days
```

---

## Noise Simulation

```text
Noise Simulation
│
├── Distance Slider
│
├── 100m
├── 250m
├── 500m
└── 1000m
```

Displays:

* Estimated dB value
* Everyday comparison

---

# 7.2 Turbine Details Tab

```text
Turbine Details
│
├── Turbine Carousel
└── Size Comparison
```

---

## Turbine Carousel

```text
Wind Turbine
│
├── Status
├── Turbine Type
├── Rated Power
├── Rotor Diameter
├── Hub Height
├── Construction Year
└── Operator
```

---

## Size Comparison

Visual comparison against:

* Cologne Cathedral
* Berlin TV Tower
* Frauenkirche Dresden

---

# 8. Transparency Layer

Transparency overlays can be accessed from nearly every metric card.

---

## Overlay Structure

```text
Metric Card
↓
Info Button
↓
Transparency Overlay
```

---

## Transparency Overlay

Contains:

```text
Meaning

Calculation

Data Source

Update Frequency
```

---

# 9. Fakten

Purpose:

Correct misconceptions and explain wind energy using evidence-based information.

---

## Structure

```text
Fakten
│
├── Categories
│
├── Fact Cards
│
└── Fact Detail
```

---

## Categories

```text
Alle

Natur & Umwelt

Mensch & Gesundheit

Energie & Technik

Wirtschaft & Gesellschaft
```

---

## Fact Card

Displays:

* Myth Statement
* Expand Indicator

---

## Fact Detail

Contains:

* Explanation
* Evidence
* Sources
* References

---

# 10. Meine Anlagen

Purpose:

Provide personalized access to relevant wind farms.

---

## Structure

```text
Meine Anlagen
│
├── Favoriten
│
└── Zuletzt Angesehen
```

---

## Favoriten

Displays:

* Saved wind farms
* Quick access

Actions:

* Remove Favorite
* Open Wind Farm
* Share Wind Farm

---

## Zuletzt Angesehen

Displays:

* Recently viewed wind farms
* Last viewed timestamp

Actions:

* Open Wind Farm

---

# 11. Profil

Purpose:

Manage personalization, privacy and application settings.

---

## Structure

```text
Profil
│
├── Benutzerkonto
│
├── Darstellung
│
├── Sprache
│
├── Standort
│
├── Personalisierung
│
├── Datenschutz
│
├── Quellen
│
└── Impressum
```

---

## Benutzerkonto

Displays:

* Login Status
* Username

Actions:

* Login
* Logout

---

## Darstellung

Options:

* Light Mode
* Dark Mode

---

## Sprache

Options:

* Deutsch
* English

---

## Standort

Options:

* Grant Location Access
* Revoke Location Access

---

## Personalisierung

Users can enable or disable metric cards.

Examples:

* CO₂ Card
* Revenue Card
* Noise Card
* Households Card

---

## Datenschutz

Contains:

* Privacy Policy
* Data Processing Information

---

## Quellen

Displays:

* Data Sources
* Calculation Methodologies

---

## Impressum

Legal Information

---

# 12. Authentication

Authentication is contextual.

Users are never required to log in during onboarding or app startup.

---

## Authentication Trigger

```text
Favorite
↓
Login Bottom Sheet

OR

Share Wind Farm
↓
Login Bottom Sheet
```

---

## Login Bottom Sheet

Options:

* Continue with Google
* Continue with Email
* Not Now

---

## Registration Flow

```text
Login Bottom Sheet
↓
Register
↓
Create Account
↓
Return to previous action
```

---

# 13. Deep Link Structure

Future-ready deep link architecture.

```text
windnah://windpark/{id}

windnah://fact/{id}

windnah://profile

windnah://favorites
```

---

# 14. Screen Inventory

```text
LaunchScreen

OnboardingScreen1
OnboardingScreen2
OnboardingScreen3

MapScreen
WindFarmOverlay
WindFarmDetailScreen

OverviewTab
TurbineDetailTab

FactListScreen
FactDetailScreen

FavoritesScreen
RecentViewedScreen

ProfileScreen

LoginBottomSheet
LoginScreen
RegistrationScreen

TransparencyOverlay
```
