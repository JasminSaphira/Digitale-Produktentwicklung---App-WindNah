# WindNah – Development Roadmap

Version: 1.0

Status: Draft

Related Documents:

* 01_PRD.md
* 02_INFORMATION_ARCHITECTURE.md
* 03_USER_STORIES.md
* 04_ARCHITECTURE.md
* 05_DESIGN_SYSTEM.md
* 06_API_SPEC.md
* 07_AI_GUIDELINES.md

---

# 1. Purpose

This document defines:

* implementation order
* milestones
* team responsibilities
* MVP scope
* stretch goals

The roadmap is optimized for a team of 2–3 developers and a project duration of approximately 3 weeks.

---

# 2. Project Goal

Deliver a functional Android application that:

* increases transparency around wind energy
* supports fact-based understanding
* demonstrates modern Android development practices
* follows Material Design 3
* follows Clean Architecture and MVVM

---

# 3. MVP Definition

The MVP must include:

## Onboarding

* Launch Screen
* 3 Onboarding Screens
* Optional Location Permission

---

## Entdecken

* Google Map
* Wind Farm Markers
* Search
* Status Filter
* Federal State Filter
* Wind Farm Overlay

---

## Wind Farm Details

### Overview

* Status
* Current Output
* Wind Speed
* Annual Production
* Households Supplied
* CO₂ Savings
* Local Energy Contribution
* Municipal Revenue

### Charts

* Production History

### Turbine Details

* Turbine Carousel
* Landmark Comparison

---

## Fakten

* Categories
* Fact Articles
* Sources

---

## Meine Anlagen

* Favorites
* Recently Viewed

---

## Profil

* Dark Mode
* Language Selection
* Location Permission Management
* Sources
* Privacy Information

---

## Authentication

* Google Login
* Email Login
* Contextual Login Bottom Sheet

---

## Transparency

For every metric:

* Description
* Calculation
* Source
* Update Frequency

---

# 4. Team Structure

Recommended team structure:

---

## Developer A

Focus:

Application Foundation

Responsible for:

* app module
* navigation
* onboarding
* authentication
* profile
* design system integration

Modules:

```text
app

feature:onboarding
feature:auth
feature:profile

core:designsystem
core:common
```

---

## Developer B

Focus:

Discovery & Wind Farm Experience

Responsible for:

* map integration
* MaStR integration
* search
* filters
* wind farm details

Modules:

```text
feature:discover
feature:windpark-detail

core:network
```

---

## Developer C

Focus:

Data, Calculations & Facts

Responsible for:

* DWD integration
* repositories
* Room
* calculations
* facts
* favorites

Modules:

```text
feature:facts
feature:my-turbines

core:data
core:database
core:domain
```

---

# 5. Milestone 1 – Project Foundation

Duration:

Days 1–3

Goal:

Application structure is established.

---

## Deliverables

* Multi-module setup
* Hilt setup
* Navigation setup
* Material 3 theme
* Design system module
* Git branching strategy
* CI setup (optional)

---

## Definition of Done

Application launches successfully.

Bottom navigation works.

All modules compile.

---

# 6. Milestone 2 – Core Navigation & UI

Duration:

Days 3–6

Goal:

Users can navigate through the application.

---

## Deliverables

### Onboarding

* Launch Screen
* Onboarding Screens

### Navigation

* Bottom Navigation

### Profile

* Settings Screens

### Authentication

* Login Bottom Sheet
* Login Screen
* Registration Screen

---

## Definition of Done

All screens are reachable.

Navigation graph complete.

---

# 7. Milestone 3 – Wind Farm Discovery

Duration:

Days 5–9

Goal:

Users can discover wind farms.

---

## Deliverables

### Map

* Google Maps integration
* Marker rendering
* Marker clustering

### Search

* Search by place
* Search by ZIP code

### Filters

* Status filter
* Federal state filter

### Overlay

* Wind Farm Preview Card

---

## Definition of Done

Wind farms can be found and selected.

---

# 8. Milestone 4 – Wind Farm Details

Duration:

Days 8–13

Goal:

Users can explore wind farms in detail.

---

## Deliverables

### Overview Tab

* Status
* Capacity
* Wind Speed
* Output
* Households
* CO₂
* Revenue

### Turbine Details Tab

* Turbine Carousel
* Landmark Comparison

### Charts

* Production History

---

## Definition of Done

Complete detail page available.

---

# 9. Milestone 5 – Data Integration

Duration:

Days 9–15

Goal:

Real data is integrated.

---

## Deliverables

### MaStR

* Wind farm aggregation
* Turbine loading

### DWD

* Wind Speed
* Wind Direction
* Yearly Average Wind Speed

### Repository Layer

* Remote Data Sources
* DTO Mapping

---

## Definition of Done

Data loads successfully from APIs.
API Key for that: x7uopppDQpagcZgOHGyWKzjVjR5blJYxZgcjqkXxE5wl6uYFqMv3iYWeTDoceqPa/V21xMzwb4PcxPj+u24JWbWHvN3ycsWBYe/YRvlB/SYaj6Pj79FWkAp0LIZnrdgFi8ITjmBwJ9XKLwjibsuVkeDWjwfs8ntY3qyxN6h64VytW1+AqFk0g9JpGouAyDNP+bin6pvwuYopMrNonygvnBuA+esxDSXRVfCcLLjLAn+9tl0u2O8JITLwZtW+Gw21sOGAt9ScuBWA0t1ituv32WZkHHlpvkzlLoyrulCkxI+j9Vk5fdMCxkDkvl8xvdaJagXCK2HLTvbRYslAeq6xt4aBiCj1kxUbfmP5Y5Mzz/2POGsFIpJ5kdW1BqkdZj+hESauayHT2o/h3vzpmyTJYX/pYcKLVT2GOzRnVPrr/UfCgb5cDR+AwoKCIDjX3RrutDdtNy/aY1cE3fW7ICpcOzvTrjFSOWiUwL+q8SNwKt2VKDmR/p5g+NsfsOwOP3RcF1Ab0CzrNsj1qgn3H0h6kl6WbJE=

---

# 10. Milestone 6 – Calculations

Duration:

Days 12–17

Goal:

All WindNah metrics are functional.

Status:

Implemented on 2026-06-26.

---

## Deliverables

### Current Output - implemented

### Annual Production - implemented

### Households Supplied - implemented

### CO2 Savings - implemented

### Local Contribution - implemented

### Municipal Revenue - implemented

### Noise Estimation - implemented

### Transparency Metadata - implemented

### App Icon - implemented

---

## Definition of Done

All calculations produce valid values.

Transparency metadata available.

Verified with:

* `:core:domain:test`
* `:core:data:test`
* `:feature:windpark-detail:compileDebugKotlin`

---

# 11. Milestone 7 – Facts & Favorites

Duration:

Days 15–18

Goal:

Educational content and personalization work.

---

## Deliverables

### Facts

* Markdown integration
* Categories
* Detail Screens

### Favorites

* Save
* Remove
* Sync

### Recently Viewed

* Tracking
* Storage

---

## Definition of Done

Facts and favorites fully functional.

---

# 12. Milestone 8 – Offline Support

Duration:

Days 17–19

Goal:

Application remains useful without connectivity.

---

## Deliverables

### Room

* Favorites
* Cached Wind Farms
* Cached Facts

### Cache Logic

* Weather Cache
* Wind Farm Cache

---

## Definition of Done

Offline scenarios handled correctly.

---

# 13. Milestone 9 – Polish & Quality

Duration:

Days 19–21

Goal:

Prepare for final submission.

---

## Deliverables

### UI Polish

* Spacing
* Typography
* Consistency

### Accessibility

* Content Descriptions
* Touch Targets
* Contrast

### Bug Fixing

### Performance Improvements

---

## Definition of Done

Application is stable and presentation-ready.

---

# 14. Git Workflow

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
* Merge via Pull Request.
* Review before merge.

---

# 15. Risk Management

## Risk

MaStR API complexity

Mitigation:

* Repository abstraction
* Mock fallback

---

## Risk

DWD availability

Mitigation:

* Weather cache
* Error state

---

## Risk

Time constraints

Mitigation:

* Prioritize MVP
* Defer stretch goals

---

# 16. Stretch Goals

Implement only if MVP is complete.

Possible enhancements:

* Dynamic Material You colors
* Additional fact categories
* More detailed noise model
* Community participation information
* More chart types
* Production forecasts
* Enhanced sharing features

---

# 17. Final Success Criteria

The project is considered successful when:

* MVP is complete
* All critical user stories are implemented
* Material Design 3 is followed
* Clean Architecture is followed
* Real APIs are integrated
* Transparency overlays work
* App is stable for demonstration
* Codebase remains maintainable
