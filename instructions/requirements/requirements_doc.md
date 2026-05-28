# GitHub Copilot Instructions — WindNah Requirements Engineering Document

## Project Context

This repository contains the requirements engineering documentation for the mobile application “WindNah”.

WindNah is an Android application that visualizes publicly available data about onshore wind turbines and wind farms in Germany to improve transparency and public acceptance of renewable energy.

The requirements document follows:

* CPRE / IREB principles
* structured requirements engineering
* professional enterprise documentation standards

The document is written as:

* a single self-contained HTML requirements specification
* printable and responsive
* semantically structured
* maintainable and extendable

---

# General Editing Rules

When modifying the requirements document:

* NEVER remove existing sections unless explicitly requested.
* Preserve the overall CPRE / IREB structure.
* Preserve semantic HTML structure.
* Preserve responsive layout and print support.
* Keep styling consistent with existing design.
* Keep all IDs, anchors and navigation links functional.
* Do not introduce external dependencies unless requested.
* Keep the document self-contained.

---

# Requirement Engineering Rules

When adding new requirements:

* Every requirement must have:

  * unique requirement ID
  * title
  * description
  * priority
  * acceptance criteria
  * status badge

Use the following requirement ID format:

REQ-F-001   → Functional Requirement
REQ-NF-001  → Non-functional Requirement
REQ-UC-001  → Use Case
REQ-US-001  → User Story

Priorities:

* MUST
* SHOULD
* COULD

Statuses:

* Draft
* Review
* Approved

---

# Data Source Rules

The “Data Sources & Integration” table is one of the most important sections.

When adding new data sources:

* append new rows instead of replacing existing content
* preserve column order
* preserve formatting
* include:

  * Source
  * Data Type
  * Concrete Data
  * Purpose in WindNah
  * App Display
  * Integration Method
  * Format
  * Priority

Preferred public data sources:

* Marktstammdatenregister (MaStR)
* Deutscher Wetterdienst (DWD)
* Umweltbundesamt (UBA)
* AGEE-Stat
* Fachagentur Wind & Solar
* Statistisches Bundesamt
* Goal100
* OpenStreetMap / Mapbox

---

# UI / UX Documentation Rules

When adding UI sections:

* use card-based layouts
* preserve green energy inspired styling
* use placeholders for screenshots
* maintain responsive behavior
* preserve sidebar navigation structure

Supported screens:

* Discover map
* Wind park overview
* Wind turbine details
* Facts & statistics pages

---

# Technical Architecture Rules

Preferred stack:

* Kotlin
* Jetpack Compose
* MVVM
* REST APIs
* Supabase / Firebase
* Python backend services
* Mapbox / OpenStreetMap

When generating architecture diagrams:

* use SVG placeholders
* use layered architecture visualization
* separate frontend/backend/external APIs

---

# Content Writing Style

Use:

* professional tone
* concise explanations
* structured formatting
* technical clarity
* consistent terminology

Avoid:

* marketing language
* emojis inside documentation
* redundant explanations
* inconsistent terminology

Preferred terms:

* Wind farm
* Wind turbine
* Renewable energy
* Transparency
* Public acceptance
* Municipal participation

---

# HTML/CSS Rules

Use:

* semantic HTML5
* CSS Grid/Flexbox
* modern enterprise styling
* reusable classes
* responsive design
* print CSS
* accessible contrast ratios

Do not:

* use inline styles excessively
* break navigation anchors
* duplicate CSS unnecessarily

---

# Change Management

When new information is added:

* integrate it into the correct section
* avoid creating duplicate sections
* preserve document hierarchy
* update tables of contents if needed
* maintain consistent numbering

If information is unclear:

* create TODO placeholders
* add comments explaining assumptions

---

# Future Extensions

The document should remain extendable for:

* AR features
* Camera recognition
* Community functionality
* Push notifications
* Live turbine monitoring
* Additional public APIs

---

# Important

The requirements document is intended for:

* university presentation
* software engineering planning
* architecture documentation
* implementation preparation

Maintain professional quality at all times.
