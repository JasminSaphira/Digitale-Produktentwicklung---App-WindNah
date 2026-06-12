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
