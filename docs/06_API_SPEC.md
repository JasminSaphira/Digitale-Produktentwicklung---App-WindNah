# WindNah – API & Data Specification

Version: 1.0
Status: Draft
Related Documents:

* 01_PRD.md
* 02_INFORMATION_ARCHITECTURE.md
* 03_USER_STORIES.md
* 04_ARCHITECTURE.md
* 05_DESIGN_SYSTEM.md

---

# 1. Purpose

This document defines:

* external data sources
* API integrations
* repository contracts
* caching strategies
* domain models
* calculated metrics
* transparency metadata

The API Specification serves as the technical foundation for data integration within WindNah.

---

# 2. Data Source Overview

| Source                          | Type            | Purpose                                                                              |
| ------------------------------- | --------------- | ------------------------------------------------------------------------------------ |
| Marktstammdatenregister (MaStR) | External API    | Wind turbine and wind farm master data                                               |
| Deutscher Wetterdienst (DWD)    | External API    | Live wind and weather information                                                    |
| Umweltbundesamt (UBA)           | Public Dataset  | CO₂ emission factors                                                                 |
| AGEE-Stat                       | Public Dataset  | Energy statistics                                                                    |
| Statistisches Bundesamt         | Public Dataset  | Household consumption statistics                                                     |
| Fachagentur Wind und Solar      | Public Dataset  | Acceptance and contextual information, Municipal participation calculations (EEG §6) |
| EEG §6                          | Legal Reference | Municipal participation calculations                                                 |
| Markdown Files                  | Local Content   | Facts and myth explanations                                                          |

---

# 3. Source Responsibilities

## MaStR

Primary source for:

* wind turbines
* wind farm assignment
* turbine specifications
* operator information
* operational status

---

## DWD

Primary source for:

* current wind speed
* wind direction
* weather conditions
* historical wind data where available

---

## UBA

Primary source for:

* electricity emission factors
* CO₂ calculations

---

## AGEE-Stat

Primary source for:

* renewable energy benchmarks
* contextual energy statistics

---

## Statistisches Bundesamt

Primary source for:

* average household consumption

Used for:

* households supplied calculation

---

## Fachagentur WInd & Solar (EEG §6)

Primary source for:

* municipal participation estimation

---

## Markdown Content

Primary source for:

* fact articles
* myth explanations
* educational content

---

# 4. Data Architecture

WindNah distinguishes between:

```text
Raw Data
↓
Domain Models
↓
Calculated Metrics
↓
UI Models
```

---

# 5. Wind Farm Aggregation Strategy

MaStR primarily provides turbine-level information.

WindNah aggregates turbines into wind farms.

---

## Aggregation Flow

```text
MaStR Turbines
↓
Group by Wind Farm Name
↓
Create Wind Farm
↓
Aggregate Metrics
↓
Expose to UI
```

---

## Wind Farm Aggregation Rules

### Wind Farm Name

Source:

MaStR Wind Farm Reference

---

### Turbine Count

Calculation:

```text
Number of turbines in group
```

---

### Installed Capacity

Calculation:

```text
Sum of turbine rated power
```

Unit:

MW

---

### Wind Farm Coordinates

Calculation:

```text
Average latitude

Average longitude
```

Purpose:

* map marker placement
* clustering
* navigation

---

### Average Turbine Height

Calculation:

```text
Average hub height of all turbines
```

Purpose:

Landmark comparison card.

---

# 6. Domain Models

## WindFarm

```kotlin
data class WindFarm(
    val id: String,
    val name: String,
    val status: WindFarmStatus,
    val latitude: Double,
    val longitude: Double,
    val turbineCount: Int,
    val installedCapacityMw: Double,
    val averageHubHeightM: Double,
    val operator: String?,
    val turbines: List<WindTurbine>
)
```

---

## WindTurbine

```kotlin
data class WindTurbine(
    val id: String,
    val name: String,
    val manufacturer: String?,
    val turbineType: String?,
    val ratedPowerMw: Double,
    val rotorDiameterM: Double?,
    val hubHeightM: Double?,
    val commissioningDate: LocalDate?,
    val operator: String?,
    val latitude: Double,
    val longitude: Double
)
```

---

## WeatherData

```kotlin
data class WeatherData(
    val windSpeedMs: Double?,
    val windDirectionDeg: Double?,
    val yearlyAverageWindSpeedMs: Double?,
    val weatherDescription: String?
)
```

---

# 7. Wind Farm ID Strategy

WindNah generates its own WindFarmId.

Purpose:

* favorites
* sharing
* deep links
* local caching

---

## Generation Rule

```text
normalizedWindFarmName
+
federalState
+
coordinateCentroid
```

Result:

```text
windfarm_sachsen_wendischbora
```

---

# 8. Repository Contracts

---

## WindFarmRepository

Responsibilities:

* load wind farms
* search wind farms
* filter wind farms
* load details
* aggregate turbines

---

### Functions

```kotlin
suspend fun getWindFarms()

suspend fun getWindFarm(id: String)

suspend fun searchWindFarms(query: String)

suspend fun filterWindFarms(...)
```

---

## WeatherRepository

Responsibilities:

* load weather data
* load wind data

---

### Functions

```kotlin
suspend fun getWeather(
    latitude: Double,
    longitude: Double
)
```

---

## FactRepository

Responsibilities:

* load facts
* load categories
* parse markdown

---

### Functions

```kotlin
suspend fun getFacts()

suspend fun getFact(id: String)
```

---

## FavoriteRepository

Responsibilities:

* add favorites
* remove favorites
* synchronize favorites

---

## UserPreferencesRepository

Responsibilities:

* language
* dark mode
* visible metrics

---

# 9. MaStR Integration

## Required Fields

Wind Farm Name

Used for:

```text
Wind Farm Aggregation
```

---

## Turbine Name

Used for:

```text
Turbine Detail Screen
```

---

## Coordinates

Used for:

```text
Map
Clustering
Navigation
```

---

## Operational Status

Used for:

```text
Status Filter
Status Chips
```

Values:

```text
In Betrieb
In Planung
In Wartung
Stillgelegt
```

---

## Rated Power

Used for:

```text
Installed Capacity
Current Output
```

---

## Rotor Diameter

Used for:

```text
Turbine Detail Card
```

---

## Hub Height

Used for:

```text
Landmark Comparison
```

---

## Commissioning Date

Used for:

```text
Turbine Detail Card
```

---

## Operator

Used for:

```text
Turbine Detail Card
Wind Farm Details
```

---

# 10. DWD Integration

## Current Wind Speed

Unit:

```text
m/s
```

---

## Wind Direction

Unit:

```text
°
```

---

## Yearly Average Wind Speed

Unit:

```text
m/s
```

Purpose:

* context
* comparisons

---

## Weather Description

Examples:

```text
Cloudy
Sunny
Rain
```

---

# 11. Cache Strategy

WindNah uses offline-first principles.

---

## Wind Farm Data

Cache Duration:

```text
30 Days
```

Reason:

Stammdaten change rarely.

---

## Weather Data

Cache Duration:

```text
15 Minutes
```

Reason:

Avoid excessive API requests.

---

## Facts

Cache Duration:

```text
Permanent
```

Reason:

Markdown content is bundled with the application.

---

## Favorites

Cache Duration:

```text
Permanent
```

Synced if authenticated.

---

# 12. Calculated Metrics

WindNah calculates multiple metrics.

All calculations occur in:

```text
core:domain
```

---

# 12.1 Current Output

Purpose:

Estimate current power generation.

---

## Inputs

```text
Current Wind Speed
Rated Power
Turbine Characteristics
```

---

## Formula

Approximation based on wind speed and turbine performance assumptions.

Output:

```text
MW
```

---

# 12.2 Households Supplied

Purpose:

Translate energy into understandable context.

---

## Inputs

```text
Annual Production
```

---

## Formula

```text
Annual Production (kWh)
/
3500 kWh
```

Source:

Statistisches Bundesamt

---

# 12.3 CO₂ Savings

Purpose:

Show the climate benefit of wind energy generation by comparing the lifecycle emissions of wind energy with the emissions of the displaced conventional electricity mix.

---

## Inputs

```text
Annual Production (kWh)

Emission Factor Conventional Electricity Mix (g CO₂/kWh)

Emission Factor Wind Energy (g CO₂/kWh)
```

---

## Formula

```text
CO₂ Savings (t/year)

=
Annual Production (kWh)
×
(
Emission Factor Conventional Electricity Mix
−
Emission Factor Wind Energy
)
/
1,000,000
```

---

## Reference Values

### Conventional Electricity Mix

```text
363 g CO₂ / kWh
```

Source:

Umweltbundesamt (UBA)

---

### Wind Energy Lifecycle Emissions

```text
9 g CO₂ / kWh
```

Source:

Lifecycle assessment values for onshore wind energy

---

## Example Calculation

```text
Annual Production:
5,000,000 kWh

Conventional Electricity Mix:
363 g CO₂ / kWh

Wind Energy:
9 g CO₂ / kWh
```

```text
CO₂ Savings

=
5,000,000
×
(363 − 9)
/
1,000,000

=
1,770 t CO₂/year
```

---

## Output

```text
t CO₂ / year
```

---

## UI Representation

WindNah should additionally provide a relatable comparison to help users understand the impact.

Example:

```text
Equivalent to approximately
3,900 passenger cars
not driving for one year.
```

The conversion factor must be documented in the transparency overlay.

---

## Transparency Overlay

Display:

* Description of the metric
* Formula used
* Assumed emission factors
* Source references
* Last update date

---

## Source

* Umweltbundesamt (UBA)

```
```

---

# 12.4 Local Energy Contribution

Purpose:

Show local relevance.

---

## Inputs

```text
Annual Production

Municipal Consumption
```

---

## Formula

Annual Production

=
Installed Capacity × Full Load Hours

Municipal Consumption

=
Population × 5,500 kWh

Local Contribution

=
Annual Production
/
Municipal Consumption
× 100

---

## Source
Regionaldatenbank Deutschland / Destatis

# 12.5 Municipal Revenue

Purpose:

Estimate local financial participation.

---

## Inputs

```text
Installed Capacity

EEG §6
```

---

## Formula

Based on EEG §6 assumptions.

Output:

```text
€/year
```

---

# 12.6 Noise Estimation

Purpose:

Provide understandable distance-based estimate.

---

## Inputs

```text
Distance
Wind Speed
Turbine Characteristics
```

---

## Output

```text
dB(A)
```

---

# 12.7 Average Turbine Height

Purpose:

Landmark comparison.

---

## Formula

```text
Average Hub Height
```

---

## Comparison Objects

### Frauenkirche Dresden

Approx.

```text
91 m
```

---

### Cologne Cathedral

Approx.

```text
157 m
```

---

### Berlin TV Tower

Approx.

```text
368 m
```

---

# 13. Transparency Metadata

Every metric must expose metadata.

---

## TransparencyModel

```kotlin
data class TransparencyInfo(
    val title: String,
    val description: String,
    val source: String,
    val calculation: String,
    val updateFrequency: String
)
```

---

## Example

CO₂ Savings

```text
Description:
Estimated avoided CO₂ emissions.

Source:
Umweltbundesamt

Calculation:
Annual production × emission factor

Update:
Daily
```

---

# 14. Fact Content Architecture

Facts are stored as Markdown.

---

## Structure

```text
facts/
│
├── natur-und-umwelt
├── mensch-und-gesundheit
├── energie-und-technik
└── wirtschaft-und-gesellschaft
```

---

## Example

```markdown
---
id: myth_noise
title: "Windräder sind extrem laut"
category: mensch-und-gesundheit
---

# Windräder sind extrem laut

...
```

---

# 15. Offline Behaviour

If no internet connection is available:

---

## Available

* Favorites
* Cached Wind Farms
* Cached Detail Screens
* Facts
* User Preferences

---

## Unavailable

* Live Wind Data
* Live Weather Data

---

## User Message

```text
Keine Internetverbindung.
Einige Inhalte sind möglicherweise nicht aktuell.
```

---

# 16. API Error Handling

---

## DWD Unavailable

```text
Winddaten aktuell nicht verfügbar.
```

---

## MaStR Unavailable

```text
Windparkdaten aktuell nicht verfügbar.
```

---

## Calculation Failure

```text
Diese Kennzahl kann aktuell nicht berechnet werden.
```

---

## Missing Data

```text
Zu dieser Anlage liegen derzeit keine vollständigen Daten vor.
```

---

# 17. Future Extensions

Potential future integrations:

* OpenStreetMap context data
* Additional weather providers
* Renewable energy comparison metrics
* Historical production datasets
* Community participation information
* Regional energy demand APIs

---

# 18. Source of Truth

The following hierarchy applies:

```text
PRD
↓
API_SPEC
↓
Architecture
↓
Implementation
```

All API integrations, calculations and repository implementations must follow this specification.
