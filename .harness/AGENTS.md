# Satiation1 Project Notes

These notes are the working source of truth for future Codex work in this project. Re-read this file and `.harness/PLAN.md` before making changes.

## Handover Sentinel

This phrase is intentionally stored here as a verification check.
Do not remove it unless the user explicitly asks.
For every answer sent back to the user, begin and end the answer with this exact phrase:

Filament Lightbulbs Burn Brightest At Night

## Project Identity

Satiation1 is a native Android nutrition and health tracking app for a capstone project. The app is written in Kotlin with Jetpack Compose and is intended to support meal logging, weight/profile tracking, data analysis dashboards, and AI-assisted food recognition through Gemini 1.5 Flash.

## Verified Current Stack

- Language: Kotlin
- UI: Jetpack Compose with Material 3
- Navigation: `androidx.navigation:navigation-compose:2.9.7`
- State: shared `SatiationViewModel` extends `AndroidViewModel`
- Database: Room 2.7.0 with KSP
- Camera: CameraX 1.6.0 dependencies are present; current camera screen is still preview-only
- AI: `com.google.ai.client.generativeai:generativeai:0.9.0`
- Charts:
  - Vico 1.13.0 dependency is still present
  - The current Progress screen is primarily custom Compose/Canvas-based
- Package: `com.mckl.satiation1`
- Minimum SDK: 24
- Target SDK: 36

## Important Source Files

- `app/src/main/java/com/mckl/satiation1/MainActivity.kt`
  - App entry point, edge-to-edge/system UI setup, Compose content
- `app/src/main/java/com/mckl/satiation1/navigation/AppNavigation.kt`
  - Top-level `NavHost` and shared ViewModel setup
- `app/src/main/java/com/mckl/satiation1/navigation/SatiationViewModel.kt`
  - Central app state, Room flows, write helpers, BMI, weight history, chart annotations
- `app/src/main/java/com/mckl/satiation1/database/AppDatabase.kt`
  - Room schema, DAOs, aggregates, destructive migration setup
- `app/src/main/java/com/mckl/satiation1/ui/screens/OnboardingScreens.kt`
  - Onboarding flow
- `app/src/main/java/com/mckl/satiation1/ui/screens/DashboardScreens.kt`
  - Main tabs, manual logging, settings, preset foods, targets, progress, custom charts
- `app/src/main/java/com/mckl/satiation1/ui/screens/CameraScreens.kt`
  - Camera preview and Gemini nutrition detail screen
- `app/src/main/java/com/mckl/satiation1/Constants.kt`
  - Shared colors and constants

## Current Verified State

- The app currently compiles
- Build command tested repeatedly: `.\gradlew.bat :app:assembleDebug`
- The app was recently smoke-tested on Android emulators `emulator-5556` and `emulator-5554`
- `.harness/` is now tracked for project handoff notes/diagrams and implementation status

## Current Database Reality

Room is the main structured data store.

Current entities:
- `UserProfile(id, name, pronouns, heightCm, startWeightKg, currentWeightKg)`
- `AppSettings(id, geminiApiKey, preferredUnits, calorieTarget, proteinTargetGrams, carbsTargetGrams, fatsTargetGrams, themePreference)`
- `MealLog(mealId, loggedAtEpochMillis, sourceType, totalCalories, totalProteinGrams, totalCarbsGrams, totalFatsGrams, notes)`
- `MealItem(itemId, mealId, name, category, calories, proteinGrams, carbsGrams, fatsGrams, confidence)`
- `WeightLog(weightLogId, loggedAtEpochMillis, weightKg)`
- `PresetFood(presetFoodId, name, category, calories, proteinGrams, carbsGrams, fatsGrams, notes, updatedAtEpochMillis)`

Current DAOs/queries support:
- Flow-backed profile/settings reads
- Insert/update profile and settings
- Transactional meal logging with items
- Query meals by date range
- Daily macro totals aggregation
- Daily meal summaries aggregation
- Weight history query
- Current BMI calculation
- Preset-food list/save/delete
- Top-food frequency aggregation for analytics

Important persistence caveat:
- `SatiationDatabase` currently uses `fallbackToDestructiveMigration(dropAllTables = true)`
- Acceptable for current development, not acceptable for preserved production data

## Current UI Reality

- Onboarding routes: `splash`, `name`, `weight`
- Main routes: `main`, `camera`, `nutrition`, `manual_entry`, `edit_profile`, `edit_name`, `edit_pronouns`, `edit_height`, `edit_weight`, `edit_nutrients`, `edit_targets`, `food_types`, `appearance`, `edit_api_key`
- Bottom tabs: `home`, `checkmark`, `progress`, `profile`

Manual logging:
- `manual_entry` is live and Room-backed
- Preset foods can be reused, edited, and deleted
- Home and Daily Targets read real Room totals

Progress:
- Phase 3 is complete
- Progress now has an overview split with separate Calendar and Stats entry cards
- Built-in date range picker flow is implemented
- Calendar page top section is now simplified to the range card plus `Choose Range`
- Calendar range shortcuts are now implemented:
  - `Past Week`
  - `Past Month`
  - `All Time` when meal history exists
- Re-entering Calendar from the Progress overview now defaults back to the past week
- `Current Day` is shown on the Calendar range card
- Calorie trend is a custom bar chart
- Calorie-chart tooltips now only open on real bars, not empty chart space
- Calorie-chart tooltips can be toggled off by tapping the same bar again
- Calendar calorie-chart x-axis is now conditional:
  - no labels when the selected range is longer than 2 weeks
  - weekday/date labels when the selected range is 2 weeks or less
- Selected calorie bars now darken correctly and return to the base color after deselection
- Nutritional split donut chart is implemented
- Favorite foods all-time bar chart is implemented
- Weight trend chart is implemented
- Trends/stats card is implemented
- Day marker dots and note storage are implemented
- Marker persistence currently uses shared preferences, not Room
- Progress, Calendar, and Stats now use a fade/card-pop style page-entry animation with staggered per-section entry, matching the settings-page feel more closely
- Meal cards on Home, Daily Targets, and Calendar support both edit and delete actions
- Calendar flow and the refined Progress interactions were emulator-verified during the latest Phase 3 passes

Phase 3 edge-case and behavior notes:
- The Calendar quick-range third button shows `All Time` only when earliest tracked history exists; otherwise it falls back to `Past Year`
- Calendar calorie-chart x-axis labels are intentionally hidden for ranges longer than 14 days to prevent crowding
- Calorie-chart floating cards only appear for real data bars; tapping the same selected bar again dismisses the card
- Manual entry defaults to `Use Current Date`; the single-day picker only appears after that option is unchecked
- Meal deletion from dashboard meal cards requires a confirmation dialog before the Room delete is executed
- Marker notes still persist through shared preferences rather than Room, so that behavior is separate from the current meal/weight persistence path

Settings:
- Settings/profile hub is implemented
- Profile editing is implemented
- Nutrient target editing is implemented
- Preset-food management is implemented
- Appearance switching is implemented
- Gemini API key editing is implemented

Add menu:
- Camera scan shortcut is present
- Manual food selection shortcut is present
- `Log New Weight` shortcut is present
- Saving weight from that flow returns to the prior screen context
- Manual entry now supports:
  - `Use Current Date`
  - optional single-day calendar picking when unchecked
- Manual entry is now reused for both new meals and meal edits

## Current Camera and Gemini Reality

- Camera screen currently shows a live preview only
- `Take Photo & Scan` still navigates forward without performing a real still-image capture
- `SatiationViewModel.capturedImage` exists, but the camera screen still does not populate it
- `NutritionDetailScreen` reads `viewModel.capturedImage.value` and the locally stored Gemini key
- There is no hardcoded Gemini API key in source now
- Gemini parsing is still optimistic and assumes strict JSON
- AI scan results are not yet mapped into `MealLog` and `MealItem`

## Development Rules For Future Work

- Keep changes small and verifiable
- Stabilize compile/runtime issues before adding new features
- Prefer the existing Compose + ViewModel + Room direction
- Keep structured app data in Room
- Keep API keys out of source code
- After meaningful changes, run `.\gradlew.bat :app:assembleDebug`
- If UI behavior changes, test on an Android emulator or device when available

## Known Current Gaps

- CameraX still capture is not implemented end-to-end
- Gemini scan/save flow is incomplete
- Import/export is still missing
- Notifications/reminders are still missing
- Proper Room migrations are still missing
- Automated test coverage is effectively not in place yet beyond template test files

## Known User Goals

- Keep Room as the main persistence layer for meals, macros, weights, profile, settings, and analysis data
- Keep the Progress dashboard rich and interactive
- Complete Gemini 1.5 Flash food recognition end-to-end
- Keep the profile area as a fuller settings experience
- Keep Gemini API key editable from settings only and stored locally on device
- Prefer Vico only where it is still useful; do not force it into flows already handled better with custom Compose charts
