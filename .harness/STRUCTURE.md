# Satiation1 Project Structure

This file is a compact map of the important project files.

## Root

- `README.md`
  - Basic project description
- `.gitignore`
  - Git ignore rules
- `build.gradle.kts`
  - Top-level Gradle plugin setup
- `settings.gradle.kts`
  - Gradle project settings and repositories
- `gradle/libs.versions.toml`
  - Version catalog for dependencies and plugins

## Harness

- `.harness/AGENTS.md`
  - Project-level operating notes and verified current state
- `.harness/PLAN.md`
  - Feature plan, user decisions, implementation status, and handoff notes
- `.harness/STRUCTURE.md`
  - This file
- `.harness/Phase 3-4/PHASE3P1.md`
  - Phase 3 status summary
- `.harness/Phase 3-4/PHASE3P2.md`
  - Phase 3 follow-up patch report and test notes
- `.harness/Phase 3-4/PHASE3P3.md`
  - Later Phase 3 refinement and emulator verification report
- `.harness/Phase 3-4/PHASE3P4.md`
  - Latest Phase 3 Progress interaction/polish report and emulator verification
- `.harness/Phase 3-4/PHASE3TO4TASKLIST.md`
  - Phase 3 closeout summary and current Phase 4 task list
- `.harness/Diagrams/use-case-diagram.drawio`
- `.harness/Diagrams/activity-user-flow.drawio`
- `.harness/Diagrams/erd.drawio`

## Android App Module

- `app/build.gradle.kts`
  - App module dependencies and Android configuration
  - Contains Compose, Navigation, Room, CameraX, Vico, Gemini, and KSP setup
- `app/src/main/AndroidManifest.xml`
  - App manifest and camera permission declaration

## Main Kotlin Package

Path:
- `app/src/main/java/com/mckl/satiation1/`

Files:
- `MainActivity.kt`
  - App entry point
  - Sets edge-to-edge/system UI behavior and loads the Compose app
- `Constants.kt`
  - Shared colors and constant lists

## Navigation And App State

Path:
- `app/src/main/java/com/mckl/satiation1/navigation/`

Files:
- `AppNavigation.kt`
  - Top-level `NavHost`
  - Defines onboarding, main app, camera, manual entry, profile/settings, nutrient, and appearance routes
- `SatiationViewModel.kt`
  - Shared app state
  - Holds onboarding temp state, captured image state, Room flows, write helpers, BMI, weight history, top foods, and chart annotations

## Database

Path:
- `app/src/main/java/com/mckl/satiation1/database/`

Files:
- `AppDatabase.kt`
  - Room schema, DAOs, aggregate queries, and database builder
  - Current entities:
    - `UserProfile`
    - `AppSettings`
    - `MealLog`
    - `MealItem`
    - `WeightLog`
    - `PresetFood`
  - Important caveat: currently uses destructive migration fallback

## UI Screens

Path:
- `app/src/main/java/com/mckl/satiation1/ui/screens/`

Files:
- `OnboardingScreens.kt`
  - Splash, staged profile onboarding, and body-metrics onboarding screens
- `DashboardScreens.kt`
  - Main container, tab content, manual entry, preset foods, daily targets, progress, settings, profile editing, nutrient editing, API key editing, and appearance UI
  - Current Progress screen includes:
    - progress overview plus separate Calendar and Stats pages
    - built-in date range picker dialog
    - simplified range card with `Current Day` plus `Choose Range`
    - quick-range shortcuts for `Past Week`, `Past Month`, and `All Time`
    - calorie bar chart
    - nutritional split donut chart
    - favorite foods chart
    - weight trend chart
    - marker dots and notes
    - trends/stat summaries
    - conditional x-axis labels for short vs long ranges
    - page-entry animation across Progress, Calendar, and Stats
    - meal edit/delete actions on dashboard meal cards
  - Phase 3 dashboard scope is complete; remaining future work is outside the core analytics-dashboard milestone
  - This is still the largest and highest-change file in the app
- `CameraScreens.kt`
  - Camera preview screen and Gemini nutrition detail screen
  - Current known limitation: preview exists, but real still-image capture/save-to-ViewModel flow is still incomplete

## Theme

Path:
- `app/src/main/java/com/mckl/satiation1/ui/theme/`

Files:
- `Color.kt`
  - Theme colors
- `Theme.kt`
  - Compose theme setup
  - Light-mode primary green was darkened for better contrast
- `Type.kt`
  - Typography setup

## Resources

Path:
- `app/src/main/res/`

Important areas:
- `values/`
- `xml/`
- `mipmap*/`, `drawable/`

## Tests

Paths:
- `app/src/test/java/com/mckl/satiation1/`
- `app/src/androidTest/java/com/mckl/satiation1/`

Files:
- `ExampleUnitTest.kt`
- `ExampleInstrumentedTest.kt`

Current state:
- Only template test files exist
- There is no meaningful automated regression coverage yet

## Where To Look By Task

- Build or dependency issues:
  - `app/build.gradle.kts`
  - `build.gradle.kts`
  - `settings.gradle.kts`
  - `gradle/libs.versions.toml`
- Navigation or route issues:
  - `navigation/AppNavigation.kt`
  - `ui/screens/DashboardScreens.kt`
- Shared state or persistence issues:
  - `navigation/SatiationViewModel.kt`
  - `database/AppDatabase.kt`
- Onboarding changes:
  - `ui/screens/OnboardingScreens.kt`
- Manual logging, preset foods, daily targets, progress, settings, or profile changes:
  - `ui/screens/DashboardScreens.kt`
  - `navigation/SatiationViewModel.kt`
- Camera or Gemini changes:
  - `ui/screens/CameraScreens.kt`
  - `navigation/SatiationViewModel.kt`
  - `database/AppDatabase.kt`
- Project direction and user decisions:
  - `.harness/AGENTS.md`
  - `.harness/PLAN.md`

## Current High-Risk Files

- `app/src/main/java/com/mckl/satiation1/ui/screens/DashboardScreens.kt`
  - Largest UI file
  - Contains main tabs, manual logging, settings, preset foods, and most custom analytics UI
- `app/src/main/java/com/mckl/satiation1/ui/screens/CameraScreens.kt`
  - Gemini/camera flow is still incomplete here
- `app/src/main/java/com/mckl/satiation1/database/AppDatabase.kt`
  - Central schema/DAO file
  - Destructive migration is still enabled
- `app/src/main/java/com/mckl/satiation1/navigation/SatiationViewModel.kt`
  - Shared application logic and Room integration
