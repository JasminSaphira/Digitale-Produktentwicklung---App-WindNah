## Project Overview

WindNah is an Android application designed to increase transparency and public acceptance of wind energy in Germany.

The app combines official wind energy data, live weather information and educational content to help users better understand local wind farms and their impact.

Key features include:

* Interactive wind farm map
* Wind farm detail pages
* Live wind information
* Fact checks and myth explanations
* Transparency overlays for all metrics
* Favorites and sharing
* Material Design 3 user experience

---

# Technology Stack

## Frontend

* Kotlin
* Jetpack Compose
* Material Design 3

## Architecture

* MVVM
* Clean Architecture
* Multi-Module Architecture

## Libraries

* Hilt
* Retrofit
* Room
* Navigation Compose
* Google Maps Compose
* Firebase Authentication
* Firebase Firestore

---

# Getting Started

## Prerequisites

* Android Studio (latest stable version)
* JDK 11 or higher
* An Android emulator or physical device (Android 8.0 / API 26 or higher)

## Firebase configuration (required)

The app uses **Firebase Authentication**, so a `google-services.json` file is required to build and run it. This file contains project-specific secrets and is therefore **not included in the repository** (it is listed in `.gitignore`).

Without it, the `:app` module will fail to build with the error:

```text
File google-services.json is missing. The Google Services Plugin cannot function without it.
```

To provide the file:

1. Obtain the `google-services.json` for the WindNah Firebase project
   (either from the project owner, or by creating your own Firebase project
   with an Android app using the application ID `com.example.windnah`).
2. Place it in the app module directory:

```text
WindNah-App/app/google-services.json
```

> **Note:** This file must be created by you — it is not part of the repository and cannot be shared through Git.

## MaStR API access (Marktstammdatenregister)

The app fetches wind farm data from the official **Marktstammdatenregister (MaStR)** SOAP API. Access requires a personal **API key** and a **Marktakteur number (MaStR number)**.

For the first weeks / testing, a working key is currently **already included in the source** so the app runs out of the box — no setup needed to try it out.

You are encouraged to replace it with **your own key**. The two values live in:

```text
WindNah-App/core/network/src/main/java/com/windnah/core/network/mastr/MastrSoapClient.kt
```

as the constants:

```kotlin
internal const val MASTR_API_KEY = "..."       // your personal API key
internal const val MASTR_MARKTAKTEUR = "..."   // your Marktakteur (MaStR) number
```

### How to obtain your own MaStR key

1. Register a free account at the Marktstammdatenregister:
   https://www.marktstammdatenregister.de
2. In your account settings, request access to the **MaStR web service (API)**.
   You will receive a personal **API key** and your **Marktakteur-MaStR-Nummer**
   (format `SOM…`).
3. Paste both values into the two constants above and re-run the app.

> The included key is a shared test credential and may be revoked later — for long-term or production use, please use your own.

## Build and run

1. Open the `WindNah-App/` folder directly in Android Studio
   (open this folder, **not** the repository root).
2. Let Gradle sync finish. Android Studio generates `local.properties`
   (the Android SDK path) automatically — it does not need to be committed.
3. Select a device/emulator and press **Run**.

To build or run the unit tests from the command line:

```text
cd WindNah-App
./gradlew assembleDebug
./gradlew test
```

---

# Repository Structure

```text
WindNah/
│
├── .github/
│   └── copilot-instructions.md
│
├── docs/
│   ├── 01_PRD.md
│   ├── 02_INFORMATION_ARCHITECTURE.md
│   ├── 03_USER_STORIES.md
│   ├── 04_ARCHITECTURE.md
│   ├── 05_DESIGN_SYSTEM.md
│   ├── 06_API_SPEC.md
│   ├── 07_AI_GUIDELINES.md
│   ├── 08_DEVELOPMENT_ROADMAP.md
│   └── 09_CALCULATION_SPEC.md
│
├── facts/
│
├── WindNah-App/
│
└── README.md
```

---

# Documentation Overview

Before implementing any feature, read the project documentation.

## Document Hierarchy

The following hierarchy applies:

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

If two documents conflict, the higher document takes precedence.

---

# Development Workflow

## Android Studio

Android Studio is the primary development environment.

Use Android Studio for:

* coding
* running the application
* emulator testing
* dependency management
* debugging
* previews
* refactoring

The Android project is located in:

```text
WindNah-App/
```

Open this folder directly in Android Studio.

---

## VS Code + Cursor

VS Code is used for:

* documentation
* architecture discussions
* AI-assisted development
* Figma MCP workflows
* code generation

Open the repository root:

```text
WindNah/
```

This allows Cursor to access:

```text
docs/
facts/
WindNah-App/
```

simultaneously.

---

# AI Workflow

Before generating code, AI tools must read:

```text
docs/04_ARCHITECTURE.md
docs/05_DESIGN_SYSTEM.md
docs/07_AI_GUIDELINES.md
```

When implementing calculations, additionally read:

```text
docs/09_CALCULATION_SPEC.md
```

---

## Example Cursor Prompt

```text
Read:

docs/04_ARCHITECTURE.md
docs/05_DESIGN_SYSTEM.md
docs/07_AI_GUIDELINES.md

Implement the Discover feature according to the project architecture.

Use MVVM, Clean Architecture and Material Design 3.
```

---

# Project Architecture

WindNah follows:

```text
MVVM
+
Clean Architecture
```

Application flow:

```text
Composable
↓
ViewModel
↓
UseCase
↓
Repository
↓
Data Source
```

---

## Important Rules

Never:

* Call APIs directly from Composables
* Put business logic into ViewModels
* Access Room directly from UI
* Create dependencies between feature modules

Always:

* Use StateFlow
* Use UseCases
* Use Repository interfaces
* Follow Material Design 3
* Follow the WindNah Design System

---

# Module Structure

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

# Development Order

Implementation should follow this order:

## Phase 1

Project setup

* Multi-module structure
* Hilt
* Navigation
* Material Theme

---

## Phase 2

Design System

* WindNahTheme
* StatusChip
* WindNahMetricCard
* FactCard
* WindFarmPreviewCard

---

## Phase 3

Core Screens

* Onboarding
* Navigation
* Profile

---

## Phase 4

Discover

* Map
* Search
* Filters

---

## Phase 5

Wind Farm Details

* Metrics
* Transparency overlays
* Charts

---

## Phase 6

Facts

* Markdown integration
* Categories

---

## Phase 7

Authentication

* Login
* Registration
* Favorites

---

## Phase 8

Testing and polishing

---

# Git Workflow

Main branches:

```text
main
develop
```

Feature branches:

```text
feature/discover

feature/detail

feature/facts

feature/profile

feature/auth
```

Rules:

* Never commit directly to main.
* Use feature branches.
* Merge via Pull Request.
* Test before merging.

---

# Transparency First

Transparency is a core product principle.

Every calculated metric must provide:

* What does this metric show?
* How is it calculated?
* Data sources
* Update frequency

Details are documented in:

```text
docs/09_CALCULATION_SPEC.md
```

---

# Goal

The goal is not only to build a functional Android application but also to provide transparent, understandable and trustworthy information about wind energy for the general public.
