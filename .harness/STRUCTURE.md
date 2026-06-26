# Satiation1 Project Structure

This file is a compact map of the important project files. It should stay aligned with the real repository state, not older milestone layouts.

## Root

- `README.md`
  - basic project description
- `.gitignore`
  - Git ignore rules
- `build.gradle.kts`
  - top-level Gradle plugin setup
- `settings.gradle.kts`
  - Gradle project settings and repositories
- `gradle/libs.versions.toml`
  - version catalog for core plugin/library versions

## Harness

- `.harness/AGENTS.md`
  - primary handover/source-of-truth notes
- `.harness/PLAN.md`
  - current implementation/verification status by phase
- `.harness/STRUCTURE.md`
  - this file
- `.harness/Phase 567/PHASE567TASKLIST.md`
  - detailed remaining-work and verification checklist for current open scope
- `.harness/Phase 3-4/PHASE3P1.md`
  - archived early/mid Phase 3 snapshot
- `.harness/Phase 3-4/PHASE3P2.md`
  - archived Phase 3 patch record
- `.harness/Phase 3-4/PHASE3P3.md`
  - archived Phase 3 refinement report
- `.harness/Phase 3-4/PHASE3P4.md`
  - archived Phase 3 closeout/refinement report
- `.harness/Phase 3-4/PHASE3TO4TASKLIST.md`
  - archived bridge note between completed Phase 3 and Phase 4 work
- `.harness/Phase 3-4/PHASE4TASKLIST.md`
  - archived detailed Phase 4 implementation and verification record
- `.harness/Diagrams/use-case-diagram.drawio`
  - conceptual use-case diagram; not exhaustive current feature coverage
- `.harness/Diagrams/activity-user-flow.drawio`
  - conceptual app-flow diagram; not an exact route graph
- `.harness/Diagrams/erd.drawio`
  - conceptual data model snapshot

## Android App Module

- `app/build.gradle.kts`
  - app module dependencies and Android configuration
  - Compose, Navigation, Room, CameraX, Gemini, schema export, and KSP setup
- `app/src/main/AndroidManifest.xml`
  - manifest
  - camera, notifications, and boot permissions
  - reminder receivers
- `app/schemas/com.mckl.satiation1.database.SatiationDatabase/`
  - exported Room schema snapshots for versions `5` and `6`

## Main Kotlin Package

Path:
- `app/src/main/java/com/mckl/satiation1/`

Files:
- `MainActivity.kt`
  - app entry point
  - edge-to-edge/system UI behavior
  - theme and accent application
  - reminder channel setup and reminder sync
- `Constants.kt`
  - shared colors, labels, and constant lists
- `DisplayPreferences.kt`
  - unit conversion helpers
  - date-formatting helpers
  - shared display preference sync

## Navigation And App State

Path:
- `app/src/main/java/com/mckl/satiation1/navigation/`

Files:
- `AppNavigation.kt`
  - top-level `NavHost`
  - onboarding, main app, camera, nutrition, manual entry, settings, history, advanced, and notification routes
- `SatiationViewModel.kt`
  - shared app state
  - onboarding temp state
  - captured image state
  - Room-backed flows and write helpers
  - backup/import/export/clear behavior
  - chart annotations via shared preferences

## Database

Path:
- `app/src/main/java/com/mckl/satiation1/database/`

Files:
- `AppDatabase.kt`
  - Room schema, DAOs, aggregate queries, and database builder
  - current entities:
    - `UserProfile`
    - `AppSettings`
    - `MealLog`
    - `MealItem`
    - `WeightLog`
    - `PresetFood`
  - explicit migrations:
    - `4 -> 5`
    - `5 -> 6`
  - important note:
    - destructive migration fallback has been removed, but preserved-data migration safety still needs deeper verification

## Feature Support Packages

Path:
- `app/src/main/java/com/mckl/satiation1/backup/`

Files:
- `AppBackupSupport.kt`
  - JSON export/import payload construction and parsing
  - backup-category selection model

Path:
- `app/src/main/java/com/mckl/satiation1/history/`

Files:
- `HistorySupport.kt`
  - history filter enums/state
  - pure filtering logic used by the Meal History screen

Path:
- `app/src/main/java/com/mckl/satiation1/reminders/`

Files:
- `ReminderSupport.kt`
  - reminder types and next-trigger calculation
- `ReminderScheduler.kt`
  - notification channel creation, AlarmManager scheduling, cancellation, and notification display
- `ReminderReceiver.kt`
  - receives fired alarms, shows notifications, and schedules the next reminder
- `ReminderBootReceiver.kt`
  - resyncs reminders after boot

## UI Screens

Path:
- `app/src/main/java/com/mckl/satiation1/ui/screens/`

Files:
- `OnboardingScreens.kt`
  - splash, staged profile onboarding, and body-metrics onboarding screens
  - onboarding screens use edge-to-edge padding and unit-aware weight/height entry
- `DashboardScreens.kt`
  - main container, home, daily targets, progress, profile editing, targets, preset foods, manual entry, and several shared UI helpers
  - contains the current Progress overview, Calendar, and Stats experience
  - still the largest and highest-risk UI file
- `CameraScreens.kt`
  - camera preview, permission handling, image import, scan review/edit, and AI save flow
- `SettingsPhase5Screens.kt`
  - nested `Settings` menu
  - `Display Units`
  - `Meal History`
  - `Advanced`
- `Phase6Screens.kt`
  - `Notifications`
  - onboarding Gemini API key step
  - onboarding/app guide content
  - appearance/accent controls and related helpers

## AI Support

Path:
- `app/src/main/java/com/mckl/satiation1/ai/`

Files:
- `GeminiNutritionSupport.kt`
  - prompt contract
  - typed result models
  - parsing/validation
  - scan UI-state helpers
- `GeminiNutritionClient.kt`
  - runtime Gemini execution
  - preferred/fallback model selection
  - transient retry handling
- `ScanImageLoader.kt`
  - shared URI/file-to-Bitmap decode path

## Theme

Path:
- `app/src/main/java/com/mckl/satiation1/ui/theme/`

Files:
- `Color.kt`
  - theme colors
- `Theme.kt`
  - Compose theme setup
  - follow-system/manual light-dark selection
  - primary/secondary accent color parsing
- `Type.kt`
  - typography setup

## Resources

Path:
- `app/src/main/res/`

Important areas:
- `values/`
- `xml/`
  - backup/data extraction rules are intentional app rules now, not placeholders
- `mipmap*/`
- `drawable/`

## Tests

Paths:
- `app/src/test/java/com/mckl/satiation1/`
- `app/src/androidTest/java/com/mckl/satiation1/`

Local/unit tests:
- `DisplayPreferencesTest.kt`
  - unit conversion and date-format helpers
- `HistorySupportTest.kt`
  - search/filter behavior for history
- `ReminderSupportTest.kt`
  - next-trigger reminder scheduling logic
- `ExampleUnitTest.kt`
  - template test still present

Instrumentation tests:
- `GeminiNutritionSupportInstrumentedTest.kt`
  - parser/validation behavior
- `AiMealPersistenceInstrumentedTest.kt`
  - AI meal persistence into Room and captured-image cleanup
- `ScanImageLoaderInstrumentedTest.kt`
  - imported-image decoding behavior
- `GeminiLiveScanInstrumentedTest.kt`
  - live Gemini verification with runtime args
- `ExampleInstrumentedTest.kt`
  - template test still present

## Where To Look By Task

- Build, dependency, or schema-export issues:
  - `app/build.gradle.kts`
  - `build.gradle.kts`
  - `settings.gradle.kts`
  - `gradle/libs.versions.toml`
  - `app/schemas/...`
- Navigation or route issues:
  - `navigation/AppNavigation.kt`
  - `ui/screens/DashboardScreens.kt`
  - `ui/screens/SettingsPhase5Screens.kt`
  - `ui/screens/Phase6Screens.kt`
- Shared state or persistence issues:
  - `navigation/SatiationViewModel.kt`
  - `database/AppDatabase.kt`
  - `backup/AppBackupSupport.kt`
- History/search issues:
  - `history/HistorySupport.kt`
  - `ui/screens/SettingsPhase5Screens.kt`
- Reminder/notification issues:
  - `reminders/ReminderSupport.kt`
  - `reminders/ReminderScheduler.kt`
  - `reminders/ReminderReceiver.kt`
  - `reminders/ReminderBootReceiver.kt`
  - `ui/screens/Phase6Screens.kt`
- Onboarding changes:
  - `ui/screens/OnboardingScreens.kt`
  - `ui/screens/Phase6Screens.kt`
- Manual logging, preset foods, targets, progress, settings, or profile changes:
  - `ui/screens/DashboardScreens.kt`
  - `ui/screens/SettingsPhase5Screens.kt`
  - `navigation/SatiationViewModel.kt`
- Camera or Gemini changes:
  - `ui/screens/CameraScreens.kt`
  - `ai/GeminiNutritionSupport.kt`
  - `ai/GeminiNutritionClient.kt`
  - `ai/ScanImageLoader.kt`
  - `navigation/SatiationViewModel.kt`
  - `database/AppDatabase.kt`
- Project direction and user decisions:
  - `.harness/AGENTS.md`
  - `.harness/PLAN.md`
  - `.harness/Phase 567/PHASE567TASKLIST.md`

## Current High-Risk Files

- `app/src/main/java/com/mckl/satiation1/ui/screens/DashboardScreens.kt`
  - largest UI file
  - contains main tabs, manual entry, progress, profile, and many reusable cards/helpers
- `app/src/main/java/com/mckl/satiation1/ui/screens/CameraScreens.kt`
  - orchestrates permission, capture/import, analyze, review, and save
- `app/src/main/java/com/mckl/satiation1/navigation/SatiationViewModel.kt`
  - shared logic for Room, AI persistence, backup actions, and annotation state
- `app/src/main/java/com/mckl/satiation1/database/AppDatabase.kt`
  - central schema/DAO file and migration definitions
- `app/src/main/java/com/mckl/satiation1/ui/screens/SettingsPhase5Screens.kt`
  - history and advanced settings behavior
- `app/src/main/java/com/mckl/satiation1/ui/screens/Phase6Screens.kt`
  - reminders, onboarding guide/API key, and appearance-accent flows
