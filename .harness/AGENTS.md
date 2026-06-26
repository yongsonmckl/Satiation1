# Satiation1 Project Notes

These notes are the working source of truth for future Codex work in this project. Re-read this file and `.harness/PLAN.md` before making changes.

## Handover Sentinel

This phrase is intentionally stored here as a verification check.
Do not remove it unless the user explicitly asks.
For every answer sent back to the user, begin and end the answer with this exact phrase:

Filament Lightbulbs Burn Brightest At Night

## Source Of Truth Order

When `.harness` files disagree, use this order:

1. `.harness/AGENTS.md`
2. `.harness/PLAN.md`
3. `.harness/Phase 567/PHASE567TASKLIST.md`
4. older Phase 3-4 notes only as historical verification records
5. Draw.io diagrams only as conceptual snapshots, not exact current implementation maps

## Project Identity

Satiation1 is a native Android nutrition and health tracking app for a capstone project. The app is written in Kotlin with Jetpack Compose and currently supports:

- onboarding with profile setup, Gemini API key step, and a first-run guide
- manual meal logging with optional date override
- preset-food reuse and management
- Room-backed home, targets, progress, and history surfaces
- profile, target, appearance, unit, notification, and advanced settings
- backup/export/import/clear actions
- AI-assisted food recognition through Gemini image analysis
- daily reminder scheduling for meals, weight, and macro check-ins

## Verified Current Stack

- Language: Kotlin
- UI: Jetpack Compose with Material 3
- Navigation: `androidx.navigation:navigation-compose:2.9.7`
- State: shared `SatiationViewModel` extends `AndroidViewModel`
- Database: Room `2.7.0` with KSP
- Camera: CameraX `1.6.0` with live preview, still capture, and manual import flow
- AI: `com.google.ai.client.generativeai:generativeai:0.9.0`
- Charts:
  - Vico `1.13.0` is still present for some charting
  - most Progress visuals are now custom Compose/Canvas-based
- Package: `com.mckl.satiation1`
- Minimum SDK: 24
- Target SDK: 36
- Room schema export: enabled to `app/schemas`

## Important Source Files

- `app/src/main/java/com/mckl/satiation1/MainActivity.kt`
  - app entry point
  - edge-to-edge/system bar setup
  - theme/accent application
  - `DisplayPreferences` sync
  - reminder sync trigger on settings changes
- `app/src/main/java/com/mckl/satiation1/navigation/AppNavigation.kt`
  - top-level `NavHost`
  - onboarding, main, camera, nutrition, manual entry, history, notifications, and advanced settings routes
- `app/src/main/java/com/mckl/satiation1/navigation/SatiationViewModel.kt`
  - central app state and most write flows
  - Room reads/writes
  - AI meal persistence
  - backup/import/export/clear actions
  - shared chart annotation storage
- `app/src/main/java/com/mckl/satiation1/database/AppDatabase.kt`
  - Room schema, DAOs, and explicit migrations
- `app/src/main/java/com/mckl/satiation1/ui/screens/DashboardScreens.kt`
  - main tabs
  - progress/calendar/stats
  - manual entry and meal editing
  - settings/profile/targets/preset-food editing surfaces that still live in this large file
- `app/src/main/java/com/mckl/satiation1/ui/screens/SettingsPhase5Screens.kt`
  - nested settings hub
  - display units
  - history
  - advanced settings with export/import/clear/date format controls
- `app/src/main/java/com/mckl/satiation1/ui/screens/Phase6Screens.kt`
  - notification settings
  - onboarding Gemini API key screen
  - onboarding guide
  - app guide
  - appearance/accent helpers used by newer settings work
- `app/src/main/java/com/mckl/satiation1/ui/screens/CameraScreens.kt`
  - camera preview
  - image import
  - Gemini analysis/review/save flow
- `app/src/main/java/com/mckl/satiation1/history/HistorySupport.kt`
  - pure filtering logic for history search/filter behavior
- `app/src/main/java/com/mckl/satiation1/reminders/ReminderScheduler.kt`
  - AlarmManager-based scheduling/cancel/show notification logic
- `app/src/main/java/com/mckl/satiation1/backup/AppBackupSupport.kt`
  - JSON export/import helpers and category parsing
- `app/src/main/java/com/mckl/satiation1/DisplayPreferences.kt`
  - unit/date-format conversion and formatting helpers

## Current Verified State

- The app has explicit Room migrations and no longer uses destructive migration fallback.
- Build/test evidence recorded in docs includes:
  - `.\gradlew.bat :app:assembleDebug`
  - `.\gradlew.bat :app:testDebugUnitTest`
  - `.\gradlew.bat :app:assembleAndroidTest`
- Recent verification recorded in `.harness` was primarily on Android emulator `emulator-5554`.
- Room schema snapshots exist under `app/schemas/com.mckl.satiation1.database.SatiationDatabase/`.
- Local unit tests now exist for:
  - display preferences and formatting
  - history filtering
  - reminder trigger calculation
- Instrumentation coverage exists for:
  - Gemini parsing and validation
  - imported-image decoding
  - AI meal persistence
  - live Gemini scan execution with runtime args

## Current Database Reality

Room is the main structured data store.

Current entities:
- `UserProfile(id, name, pronouns, heightCm, startWeightKg, currentWeightKg)`
- `AppSettings(id, geminiApiKey, preferredUnits, preferredDateFormat, calorieTarget, proteinTargetGrams, carbsTargetGrams, fatsTargetGrams, themePreference, followSystemTheme, primaryAccentHex, secondaryAccentHex, mealReminderEnabled, mealReminderHour, mealReminderMinute, weightReminderEnabled, weightReminderHour, weightReminderMinute, macroReminderEnabled, macroReminderHour, macroReminderMinute)`
- `MealLog(mealId, loggedAtEpochMillis, sourceType, totalCalories, totalProteinGrams, totalCarbsGrams, totalFatsGrams, notes)`
- `MealItem(itemId, mealId, name, category, calories, proteinGrams, carbsGrams, fatsGrams, confidence)`
- `WeightLog(weightLogId, loggedAtEpochMillis, weightKg)`
- `PresetFood(presetFoodId, name, category, calories, proteinGrams, carbsGrams, fatsGrams, notes, updatedAtEpochMillis)`

Current DAOs/queries support:
- flow-backed profile/settings reads
- transactional meal insert/update/delete with item replacement
- query meals by date range and all meals
- daily macro totals aggregation
- daily meal summaries aggregation
- top-food aggregation
- weight history query
- current BMI calculation
- preset-food list/save/delete
- earliest-meal query for `All Time` range behavior

Migration reality:
- database version is `6`
- explicit migrations exist for:
  - `4 -> 5` adding `preferredDateFormat`
  - `5 -> 6` adding theme-following, accent colors, and reminder fields
- `fallbackToDestructiveMigration(...)` is no longer configured
- preserved-data migration safety is still under-verified against older real or fixture-backed databases

Non-Room persistence still present:
- Progress chart annotations still live in shared preferences through `SatiationViewModel`
- annotations are included in export/import/clear handling, but they are not yet Room entities

## Current UI And Navigation Reality

Onboarding routes:
- `splash`
- `name`
- `weight`
- `onboarding_api_key`
- `onboarding_guide`

Main app routes:
- `main`
- `camera`
- `nutrition`
- `manual_entry`
- `edit_profile`
- `edit_name`
- `edit_pronouns`
- `edit_height`
- `edit_weight`
- `edit_nutrients`
- `edit_targets`
- `food_types`
- `appearance`
- `edit_api_key`
- `settings_menu`
- `display_units`
- `advanced_settings`
- `history`
- `notifications`

Bottom tabs:
- `home`
- `checkmark`
- `progress`
- `profile`

Settings information architecture:
- top-level profile tab is a settings-oriented hub
- top-level emphasis is profile editing, Gemini API key, and entry into nested settings
- nested `Settings` page now includes:
  - `Display Units`
  - `Edit Nutrients`
  - `Appearance`
  - `Meal History`
  - `Notifications`
  - `Advanced`

Manual logging:
- `manual_entry` is live and Room-backed
- the header is now `Manual Entry`
- the same form is reused for create and edit flows
- meals can be logged for the current day or a picked date
- users can save manual meals as preset foods after save or while editing
- embedded preset list was removed from the bottom of the manual-entry screen

History:
- `HistoryScreen` is reachable from nested settings
- filters include:
  - search text
  - date range (`Past Week`, `Past Month`, `All Time`)
  - source (`all`, `manual`, `ai_scan`)
  - calorie band
- result cards open a detail dialog that reuses existing edit/delete actions

Progress:
- Phase 3 feature scope is complete for the current milestone
- Progress has:
  - overview
  - Calendar destination
  - Stats destination
- current features include:
  - range picker dialog
  - `Past Week`, `Past Month`, and `All Time`/`Past Year` shortcuts
  - custom calorie chart with real-bar-only selection
  - floating tooltip cards
  - nutritional split chart
  - favorite foods chart
  - weight trend
  - BMI gauge
  - trends/statistics summaries
  - selected-day meals and macros
  - marker dots and note annotations
  - meal edit/delete from Progress-related meal surfaces
  - page-entry animation
- Progress-root tab taps reset nested Progress navigation back to the overview

Appearance:
- `followSystemTheme` locks manual theme selection when enabled
- when disabled, manual light/dark selection is exposed
- primary and secondary accent colors are user-configurable

Notifications:
- reminder settings screen exists under nested settings
- user-configurable daily reminders exist for:
  - meal logging
  - weight logging
  - macro check-in
- Android notification permission is requested on newer Android versions when needed
- reminders reschedule on reboot through `ReminderBootReceiver`

Advanced settings:
- category-based export exists
- category-based import exists
- category-based clear/reset exists
- date-format preference exists
- all destructive actions are behind confirmation dialogs

## Current Camera And Gemini Reality

- Camera flow supports:
  - live preview
  - real still capture into app cache
  - manual image import through the camera flow
- `SatiationViewModel.capturedImage` is the canonical shared scan image state
- the previous separate add-menu import-photo shortcut was removed; import remains reachable from the camera flow
- `NutritionDetailScreen` analyzes only the current shared captured image plus the stored Gemini key
- there is no hardcoded Gemini API key in source
- Gemini request execution:
  - prefers `gemini-2.5-pro`
  - falls back to `gemini-2.5-flash` if Pro is unavailable for the key/tier
  - retries transient 503/high-demand failures
- parsing is defensive:
  - strips markdown fences and noisy outer text
  - validates totals, items, names, and numeric fields
  - rejects negative and non-finite values
- AI results can be:
  - reviewed
  - edited
  - reanalyzed with a hint
  - retaken
  - redirected into manual entry
  - saved as Room-backed meals/items
- a debug panel can show raw Gemini response text without exposing secrets

Current AI/product risk:
- semantic output variability between runs is still normal model behavior
- the review/edit step remains a product requirement
- downstream naming for AI-saved meals is still somewhat verbose and could be shortened later

## Current Verification Reality

Verified by code and/or tests:
- explicit migrations are configured and schema export is enabled
- history filter logic has local/unit tests
- reminder next-trigger logic has local/unit tests
- unit/date-format conversion helpers have local/unit tests
- AI parsing/image/persistence flows have instrumentation coverage
- live Gemini scan coverage exists but depends on runtime instrumentation args and external API behavior

Still under-verified relative to implemented code:
- preserved-data Room migration safety
- export/import round-trip behavior and invalid-input handling in automated tests
- populated-data history edit/delete propagation across all linked UI surfaces
- longer end-to-end reminder delivery timing
- broader end-to-end session validation across onboarding, settings, logging, AI, history, and backup

## Development Rules For Future Work

- Keep changes small and verifiable.
- Prefer the existing Compose + ViewModel + Room direction.
- Keep structured app data in Room unless there is a strong reason not to.
- Keep secrets out of source code and logs.
- After meaningful changes, run `.\gradlew.bat :app:assembleDebug`.
- If behavior changes in high-risk flows, prefer emulator/device verification and update `.harness` notes in the same turn.
- When docs and code disagree, fix the docs quickly so the next model does not inherit stale assumptions.

## Known Current Gaps

- chart annotations are still in shared preferences instead of Room
- migration safety still lacks preserved-data verification
- export/import and clear-data flows need stronger automated or repeatable verification evidence
- reminder UX is still fixed to three daily reminder types rather than a more flexible weekday/multi-reminder builder
- accessibility and empty-state polish is improved but not fully audited app-wide

## Known User Goals

- Keep Room as the main persistence layer for meals, macros, weights, profile, settings, and analysis data.
- Keep the Progress dashboard rich and interactive.
- Keep Gemini food recognition working end-to-end with manual image import inside the camera flow.
- Keep the profile area as a fuller settings experience.
- Keep the Gemini API key editable from onboarding/settings and stored locally on device.
- Keep `.harness` accurate enough that the next model can continue without re-deriving repository state from scratch.
