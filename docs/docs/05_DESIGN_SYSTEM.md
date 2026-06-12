# WindNah – Design System Documentation

Version: 1.0
Status: Draft
Related Documents:

* 01_PRD.md
* 02_INFORMATION_ARCHITECTURE.md
* 03_USER_STORIES.md
* 04_ARCHITECTURE.md

---

# 1. Design Principles

WindNah is based on Google's Material Design 3 (Material You).

The design system aims to:

* increase trust through transparency
* reduce cognitive load
* communicate complex information clearly
* support accessibility
* maintain consistency across all screens
* provide a modern Android-native experience

The design language should feel:

* trustworthy
* informative
* approachable
* neutral
* environmentally focused

---

# 2. Design Philosophy

WindNah follows four core design principles.

---

## Transparency First

Every important metric should be explainable.

Users should always be able to understand:

* where data comes from
* how values are calculated
* how often data is updated

Implementation:

* Transparency overlays
* Source references
* Calculation explanations

---

## Information Before Interaction

Users should immediately access information without barriers.

Implementation:

* No mandatory login
* No mandatory registration
* Minimal onboarding friction

---

## Progressive Disclosure

Show only relevant information initially.

Allow users to explore deeper information when interested.

Implementation:

* Wind Farm Overlay
* Detail Screens
* Transparency Overlays
* Expandable Fact Cards

---

## Familiar Android Experience

Use Material Design 3 patterns whenever possible.

Avoid custom interactions that differ from Android standards.

---

# 3. Branding

## App Name

WindNah

---

## Logo

WindNah Wind Turbine Logo

Characteristics:

* Wind turbine symbol
* Green color palette
* Nature-inspired visual language
* Renewable energy focus

Usage:

* Launch Screen
* App Icon
* Login Screen
* Onboarding
* Profile

---

# 4. Material Design Foundation

WindNah follows Material Design 3 guidelines.

Reference:

https://m3.material.io/

The following Material Design 3 foundations should be used whenever possible:

* Color System
* Typography System
* Shape System
* Motion System
* Elevation System
* Adaptive Layout System

Custom components should only be introduced when no suitable Material component exists.

---

# 5. Color System

WindNah uses a Material Design 3 Green Theme.

---

## Brand Colors

### Primary

Used for:

* Primary Buttons
* Active Navigation
* Important Actions

```text
#3F6836
```

---

### Primary Container

Used for:

* Highlighted Cards
* Positive Status Areas

```text
#C0EFB0
```

---

### Secondary

Used for:

* Secondary Information
* Supporting UI Elements

```text
#53634E
```

---

### Background

```text
#F8FBF1
```

---

### Surface

```text
#F8FBF1
```

---

### Error

```text
#BA1A1A
```

---

### Maintenance / Warning

Used for:

* In Wartung Status
* Temporary Restrictions
* Warning Indicators

```text
#F9CD55
```

---

# 6. Status Colors

## In Betrieb

Material Green

Used for:

* Active Wind Farms
* Positive Production Status

---

## In Wartung

```text
#F9CD55
```

Used for:

* Maintenance Status
* Service Interruptions

---

## In Planung

Material Secondary Color

Used for:

* Planned Projects

---

## Stillgelegt

Material Neutral Grey

Used for:

* Inactive Wind Farms

---

# 7. Dark Mode

WindNah supports Light Mode and Dark Mode.

Requirements:

* Full Material Design 3 support
* Dynamic color compatibility where possible
* Contrast-compliant text
* Identical information hierarchy

Dark Mode must be available in Profile Settings.

---

# 8. Typography

WindNah follows Material Design 3 typography guidelines.

Font Family:

```text
Roboto
```

No custom fonts should be used.

---

## Typography Scale

Use Material 3 defaults:

```text
Display Large
Display Medium
Display Small

Headline Large
Headline Medium
Headline Small

Title Large
Title Medium
Title Small

Body Large
Body Medium
Body Small

Label Large
Label Medium
Label Small
```

---

## Usage Guidelines

### Headlines

Used for:

* Screen Titles
* Wind Farm Names

---

### Titles

Used for:

* Card Titles
* Sections

---

### Body

Used for:

* Descriptions
* Fact Texts
* Supporting Information

---

### Labels

Used for:

* Buttons
* Chips
* Navigation

---

# 9. Shape System

WindNah uses Material Design 3 shape guidelines.

---

## Standard Radius

```text
16dp
```

Used for:

* Cards
* Bottom Sheets
* Dialogs
* Search Fields
* Fact Cards

---

## Full Radius

Used for:

* Chips
* Status Pills
* Small Indicators

---

# 10. Iconography

Icon Set:

```text
Material Symbols Outlined
```

---

## Navigation Icons

### Entdecken

```text
map
```

---

### Fakten

```text
fact_check
```

---

### Meine Anlagen

```text
star
```

---

### Profil

```text
person
```

---

## Utility Icons

Examples:

```text
info
search
filter_list
location_on
share
favorite
favorite_border
language
dark_mode
light_mode
```

---

# 11. Elevation System

Use Material Design 3 elevation values.

---

## Elevation 0

Backgrounds

---

## Elevation 1

Standard Cards

---

## Elevation 2

Interactive Cards

---

## Elevation 3

Bottom Sheets

---

## Elevation 4+

Dialogs

---

# 12. Layout & Spacing

WindNah follows an 8dp spacing system.

---

## Base Units

```text
4dp
8dp
16dp
24dp
32dp
48dp
```

---

## Screen Padding

Recommended:

```text
16dp
```

---

## Card Spacing

Recommended:

```text
16dp
```

---

## Section Spacing

Recommended:

```text
24dp
```

---

# 13. Navigation Components

## Bottom Navigation

Primary navigation pattern.

Contains:

* Entdecken
* Fakten
* Meine Anlagen
* Profil

Uses Material 3 Navigation Bar.

---

## Tabs

Used in:

* Wind Farm Details
* Fakten Categories

Uses Material 3 Tab Row.

---

## Back Navigation

Uses standard Android navigation patterns.

---

# 14. Core Components

These are the primary reusable components of WindNah.

---

# 14.1 WindNahMetricCard

Most important reusable component.

Used across:

* Wind Farm Overview
* Environmental Metrics
* Economic Metrics
* Weather Metrics

---

## Structure

```text
Icon

Title

Main Value

Unit

Supporting Text

Info Button
```

---

## Variants

### Compact

Width:

50%

Used for:

* CO₂ Savings
* Households
* Local Contribution
* Annual Production

---

### Expanded

Width:

100%

Used for:

* Current Output
* Municipal Revenue
* Noise Simulation
* Wind Speed

---

## Interaction

Info Button opens:

```text
Transparency Overlay
```

---

# 14.2 WindFarmPreviewCard

Used in:

* Wind Farm Overlay

Contains:

* Name
* Location
* Status
* Turbine Count
* Capacity
* Households
* CO₂ Savings

Actions:

* Open Details

---

# 14.3 FactCard

Used in:

* Fakten

Pattern:

Accordion

Contains:

* Myth Statement
* Expand Indicator

Expands to:

* Explanation
* Sources
* References

---

# 14.4 TurbineCard

Used in:

* Turbine Carousel

Contains:

* Status
* Turbine Type
* Rated Power
* Rotor Diameter
* Hub Height
* Construction Year
* Operator

---

# 14.5 StatusChip

Used for:

* In Betrieb
* In Wartung
* In Planung
* Stillgelegt

Shape:

Rounded

---

# 15. Input Components

Use Material Design 3 components.

---

## Search

Material 3 Search Bar

Used in:

* Discover Screen

---

## Filters

Material 3 Filter Chips

Used in:

* Wind Farm Status Filter

---

## Selection

Material 3 Dropdown Menu

Used in:

* Federal State Filter
* Language Selection

---

## Slider

Material 3 Slider

Used in:

* Noise Simulation

---

# 16. Bottom Sheets

WindNah heavily uses Bottom Sheets.

---

## Wind Farm Preview

Appears after marker selection.

---

## Login Prompt

Appears when:

* Favorite action
* Share action

Contains:

* Google Login
* Email Login
* Not Now

---

## Transparency Overlay

May be implemented as:

* Modal Bottom Sheet
  or
* Dialog

Contains:

* Explanation
* Calculation
* Sources
* Update Frequency

---

# 17. Charts & Data Visualization

Used for:

* Production History
* Future Comparisons

---

## Design Rules

Use:

* minimal grid lines
* high readability
* accessible colors
* Material Design styling

Avoid:

* decorative charts
* excessive animation

---

# 18. Motion

Use Material Design 3 motion principles.

---

## Allowed Animations

* Bottom Sheet transitions
* Tab transitions
* Card expansion
* Loading states

---

## Avoid

* Long animations
* Decorative motion
* Auto-playing animations

---

# 19. Accessibility

Accessibility is mandatory.

---

## Requirements

Support:

* Dynamic Font Sizes
* Screen Readers
* High Contrast
* Dark Mode

---

## Touch Targets

Minimum:

```text
48dp x 48dp
```

---

## Contrast

Follow WCAG AA minimum requirements.

---

# 20. Responsive Behavior

Support:

* Small Phones
* Standard Phones
* Large Phones
* Foldables (best effort)

---

## Adaptive Layout

Prefer:

* LazyColumn
* LazyRow
* Responsive Cards

---

# 21. Empty States

Every feature must define an empty state.

Examples:

### No Favorites

```text
Noch keine Favoriten gespeichert.
```

---

### No Search Results

```text
Keine Windparks gefunden.
```

---

### No Recently Viewed

```text
Noch keine Windparks angesehen.
```

---

# 22. Loading States

Every screen must define loading behavior.

Preferred:

* Skeleton Loading
* Progress Indicators

Avoid:

* Empty white screens

---

# 23. Error States

Every screen must define error behavior.

Examples:

### Wind Data Unavailable

```text
Winddaten aktuell nicht verfügbar.
```

---

### No Internet

```text
Keine Internetverbindung.
```

---

### Calculation Unavailable

```text
Diese Kennzahl kann aktuell nicht berechnet werden.
```

---

# 24. Design System Rules for AI

AI-generated UI must:

* Follow Material Design 3
* Use Roboto
* Use WindNah color palette
* Use Material Symbols Outlined
* Use 16dp corner radius
* Reuse WindNahMetricCard whenever possible
* Reuse WindFarmPreviewCard whenever possible
* Use Material components before creating custom ones
* Support Dark Mode
* Support accessibility requirements
* Follow the 8dp spacing system
* Keep interactions consistent across features

---

# 25. Design-to-Code Workflow

Preferred workflow:

```text
Figma
↓
Figma MCP Server
↓
Jetpack Compose
↓
Review against Design System
↓
Implementation
```

The Design System is the source of truth.

Figma provides visual specifications.

AI-generated code must follow the Design System even when extracted from Figma.
