# WindNah – Product Requirements Document (PRD)

**Version:** 1.1
**Status:** Draft
**Project:** WindNah
**Authors:** Team WindNah
**Customer:** Umweltbundesamt (UBA)
**Last Updated:** June 2026

---

# 1. Product Overview

## Product Name

WindNah

## Product Vision

WindNah is a mobile application designed to increase public acceptance and transparency of onshore wind energy in Germany.

The application aggregates publicly available data from multiple trusted sources and transforms it into understandable information for citizens.

WindNah helps users understand:

* how wind turbines work
* how much electricity they generate
* what environmental benefits they create
* how local communities benefit financially
* why turbines sometimes stand still

---

# 2. Problem Statement

Many citizens see wind turbines in their daily lives but often do not understand their contribution to energy supply, climate protection, or local communities.

Publicly available information exists but is:

* distributed across multiple sources
* difficult to access
* highly technical
* not localized
* difficult to interpret

As a result, misconceptions and uncertainty can emerge, reducing local acceptance of renewable energy projects.

WindNah addresses this challenge by making relevant wind energy information understandable, transparent and location-based.

---

# 3. Product Goals

## Transparency

Provide understandable information about wind farms and wind turbines.

## Education

Help users understand renewable energy and correct common misconceptions.

## Acceptance

Increase trust and acceptance through factual and transparent information.

## Accessibility

Make complex energy and environmental data accessible to non-experts.

## Local Relevance

Connect users with wind energy projects in their surroundings.

---

# 4. Target Audience

## Primary Audience

### Concerned Residents and Skeptics

Characteristics:

* Live near existing or planned wind farms
* Interested in local impacts
* Seek objective information
* Concerned about noise, landscape impact and property value

Goals:

* Understand local effects
* Verify claims and concerns
* Access transparent information

---

## Secondary Audience

### Energy Transition Supporters

Characteristics:

* Environmentally conscious
* Interested in renewable energy
* Support climate protection initiatives

Goals:

* Learn about local wind projects
* Track sustainability metrics
* Save and revisit interesting wind farms

---

## Tertiary Audience

### Curious Passersby

Characteristics:

* Encounter wind turbines during travel or recreation
* Have little prior knowledge

Goals:

* Quickly understand what they see
* Access simple explanations
* Explore nearby wind farms

---

# 5. Success Metrics

WindNah follows a privacy-preserving measurement approach.

The application focuses on aggregated, non-personal usage indicators.

## Product Reach

* Number of installations
* Number of active devices

## Feature Usage

* Number of wind farm detail pages opened
* Number of fact pages opened
* Number of favorites created

## Educational Impact

* Number of transparency overlays opened
* Number of fact articles viewed

## Acceptance Indicators

* Most viewed sustainability metrics
* Most viewed transparency explanations

The application does not create individual behavioral profiles and does not track detailed user activity such as session duration or navigation paths.

---

# 6. Privacy Principles

WindNah follows a Privacy-by-Design philosophy.

The application is intended to inform citizens about renewable energy and therefore minimizes the collection of personal data.

Principles:

* No advertising tracking
* No marketing tracking
* No sale of user data
* No behavioral profiling
* No unnecessary personal data collection

All core functionality is available without registration.

## Location Data

Location access is optional.

The user's location is used exclusively for:

* displaying nearby wind farms
* centering the map on the user's region
* improving local relevance of displayed information

Location data is not permanently stored and can be disabled at any time.

## Account Data

User accounts are only required for:

* favorites synchronization
* wind farm sharing

Anonymous usage remains fully supported.

---

# 7. Onboarding

Displayed on first app launch.

## Screen 1

### Title

Windkraft in deiner Nähe

### Purpose

Introduce nearby wind energy discovery.

---

## Screen 2

### Title

Mythen klären, Fakten kennen

### Purpose

Introduce transparency and fact-checking.

---

## Screen 3

### Title

Windenergie verstehen. Zukunft gestalten.

### Purpose

Introduce sustainability metrics and local discovery.

### Location Permission Explanation

Aktivieren Sie den Standortzugriff, um sofort Windparks in Ihrer Nähe zu finden.

Die Standortfreigabe ist weiterhin optional und kann jederzeit geändert werden.

### Actions

* Standort freigeben & starten
* Ohne Standort starten

---

# 8. Authentication

# 8. Authentication

## Authentication Strategy

WindNah follows a low-friction onboarding philosophy.

Users should be able to access all informational content immediately after onboarding without creating an account.

The application intentionally avoids mandatory registration in order to:

* reduce entry barriers
* encourage exploration
* support anonymous usage
* build trust through transparency
* maximize accessibility for all target groups

Authentication is therefore only requested when a user attempts to access functionality that benefits from account-based storage or synchronization.

---

## Features Available Without Login

Users can access the following features anonymously:

* Interactive map
* Wind farm search
* Filters
* Wind farm details
* Sustainability metrics
* Fact Check section
* Recently viewed wind farms
* Dark mode
* Language selection
* Transparency overlays

---

## Login-Gated Features

Authentication is only required for:

* Saving favorites across devices
* Sharing wind farms
* Synchronizing user preferences

---

## Contextual Authentication

Login is not presented during onboarding or app startup.

Instead, authentication is triggered contextually when a user initiates an action that requires an account.

Example:

User taps "Add to Favorites"

↓

Login Bottom Sheet

↓

Continue with Google
Continue with Email
Not now

---

## Supported Authentication Methods

* Google Login
* Email & Password Login

Users may continue using the application without creating an account.


---

# 9. Main Navigation

Bottom Navigation contains four sections:

1. Entdecken
2. Fakten
3. Meine Anlagen
4. Profil

---

# 10. Entdecken

## Purpose

Explore wind farms throughout Germany.

## Interactive Map

Features:

* User location
* Wind farm markers
* Marker clustering

## Search

Users can search by:

* Place name
* ZIP code

## Filters

### Federal State Filter

* All states
* Single state selection

### Status Filter

* In Operation
* Maintenance
* Planned
* Decommissioned

---

## Wind Farm Selection

When selecting a wind farm:

### Map Behavior

* Zoom into selected wind farm
* Display individual turbine markers

### Preview Overlay

Displayed as a bottom sheet.

Contains:

* Wind farm name
* Location
* Status
* Number of turbines
* Installed capacity
* Households supplied
* CO₂ savings per year

### Actions

* Open detail page
* Add to favorites
* Share wind farm

---

# 11. Wind Farm Details

The detail screen contains two tabs.

---

## Tab 1 – Overview

### Operational Status

* Current status
* Last update

### Live Wind Conditions

* Current wind speed
* Weather conditions

### Current Output

Displays estimated current production.

Includes:

* Current output
* Maximum capacity
* Utilization percentage

Example:

4.2 MW / 6 MW (70%)

---

### Annual Energy Production

Displays estimated annual generation.

Unit:

MWh/year

---

### Households Supplied

Displays estimated number of households supplied.

Based on average household consumption.

---

### CO₂ Savings

Displays estimated annual CO₂ reduction.

Includes relatable comparison.

Example:

Equivalent to removing 3,900 passenger cars from the road for one year.

---

### Local Energy Contribution

Displays percentage of local electricity demand covered.

Example:

22% of municipal electricity demand.

---

### Municipal Revenue

Displays estimated annual municipal participation.

Based on EEG §6.

Unit:

€/year

---

### Production History

Interactive chart.

Time ranges:

* Last 7 days
* Last 30 days

Unit:

MWh

---

### Noise Simulation

Displays estimated noise level.

Users can adjust viewing distance.

Slider values:

* 100 m
* 250 m
* 500 m
* 1000 m

Output:

Noise level in dB

Includes relatable comparison.

Example:

Comparable to a quiet library.

---

## Tab 2 – Turbine Details

### Turbine Carousel

One card per turbine.

Contains:

* Status
* Turbine model
* Rated power
* Rotor diameter
* Hub height
* Year of construction
* Operator

---

### Wind Turbine Size Comparison

Visualization comparing turbine height against landmarks.

Comparison Objects:

* Cologne Cathedral
* Berlin TV Tower
* Frauenkirche Dresden

Purpose:

Improve comprehensibility of technical dimensions.

---

# 12. Transparency Layer

Every metric card supports a transparency overlay.

Users can open additional information.

The overlay contains:

## Meaning

What does this metric represent?

## Calculation

How was the metric calculated?

## Data Source

Which data source was used?

## Update Frequency

How often is the value updated?

---

# 13. Fakten

## Purpose

Correct misconceptions and provide evidence-based explanations.

## Initial Topics

* Why do wind turbines sometimes stop?
* Are wind turbines noisy?
* How much electricity does a wind turbine generate?
* How much CO₂ can be saved?
* How do municipalities benefit financially?
* What is EEG §6?
* How does wind energy support the energy transition?

## Fact Categories

- All
- Nature & Environment
- Human & Health
- Energy & Technology
- Economy & Society

---

# 14. Meine Anlagen

## Favorites

User-saved wind farms.

Features:

* Add favorites
* Remove favorites
* Quick access
* Synchronization across devices for authenticated users

---

## Recently Viewed

History of visited wind farms.

Displays:

* Wind farm name
* Last viewed timestamp

---

## Sharing

Authenticated users can share wind farms via the native Android Share Sheet.

Shared information:

* Wind farm name
* Location
* Link to the wind farm

The application does not include comments, feeds, followers or community functions.

---

# 15. Profile

## User Profile

* Username
* Login status

## Appearance

* Light Mode
* Dark Mode

## Permissions

* Location access

## Personalization

Users can customize which metric cards are visible.

Examples:

* CO₂ Card
* Households Card
* Revenue Card
* Noise Card

## Sprache

* Deutsch
* Englisch

## Privacy

* Location settings
* Delete stored preferences
* Privacy policy
* Data processing information

## Legal

* Privacy Policy
* Data Sources
* Imprint

---

# 16. Data Sources

## External APIs

### Marktstammdatenregister (MaStR)

Provides:

* Wind turbine master data
* Wind farm information

### Deutscher Wetterdienst (DWD)

Provides:

* Weather information
* Wind speed information

---

## Public Datasets

### AGEE-Stat

Energy statistics.

### Umweltbundesamt

Environmental indicators.

### Fachagentur Wind und Solar

Acceptance and participation data.

### Statistisches Bundesamt

Household consumption statistics.

---

# 17. Calculated Metrics

The following values are calculated or estimated.

### Current Power Output

Based on:

* Wind speed
* Turbine specifications
* Performance curves

### Noise Estimation

Based on:

* Distance
* Wind speed
* Turbine characteristics

### Households Supplied

Based on average household consumption.

### CO₂ Savings

Based on national electricity emission factors.

### Municipal Revenue

Based on EEG §6 assumptions.

### Local Energy Contribution

Based on estimated annual generation and municipal electricity consumption.

---

# 18. Non-Functional Requirements

## Performance

* Map interactions below 500 ms
* Fast marker rendering

## Reliability

* Graceful API failure handling
* Offline caching for previously loaded data

## Accessibility

* Large text support
* High contrast support
* Material Design 3 accessibility standards

## Security

* GDPR compliant
* Secure authentication
* Minimal personal data collection

---

# 19. MVP Scope

## Included

* Onboarding
* Optional Authentication
* Interactive Map
* Search by Place and ZIP Code
* Federal State Filter
* Status Filter
* Wind Farm Overlay
* Wind Farm Details
* Turbine Details
* Sustainability Metrics
* Production History Chart
* Noise Simulation
* Transparency Overlays
* Facts Section
* Favorites
* Recently Viewed
* Wind Farm Sharing
* Dark Mode
* Live Weather Data

## Excluded

* AR Features
* Camera Scan
* Community Functions
* User Comments
* Push Notifications
* Gamification

---

# 20. Technology Stack

Platform:

Android

Language:

Kotlin

UI Framework:

Jetpack Compose

Design System:

Material Design 3

Architecture:

MVVM + Clean Architecture

Dependency Injection:

Hilt

Networking:

Retrofit

Local Storage:

Room

Reactive State Management:

StateFlow

Navigation:

Navigation Compose

Maps:

Google Maps Compose

Authentication:

Firebase Authentication

Backend:

Firebase / Serverless Architecture

Analytics:

Optional and privacy-preserving only.
