# Satiation1 Implementation Plan

This plan is based on the current repository state. Update it whenever feature status changes materially.

Internal note:
- When committing and pushing, do not mention internal phase labels in commit messages.

## Current Sitrep

- Phase 0: Completed
- Phase 1: Completed
  - Core Room/data layer is in place
  - Still uses destructive migration, so it is not production-safe yet
- Phase 2: Completed
  - Manual logging, preset foods, Room-backed home/targets, and fallback flow are implemented
- Phase 3: Completed
  - Progress screen has been reworked into a calendar-first analytics dashboard
  - Overview preview tap behavior and calendar chart interaction rules were refined and emulator-verified
  - Calendar page was simplified by removing the old week browser and preset strip
  - Marker dots, favorite foods, macro split, weight trend, stats, improved chart tooltip behavior, meal edit/delete, and page-entry animation are implemented
  - Calendar range shortcuts, conditional x-axis behavior, and manual-entry date override were completed
  - Phase 3 feature scope is considered complete based on the implemented dashboard behavior and recent emulator verification
- Phase 4: Implemented for current scope
  - Real capture, manual import, Gemini request execution, parsing, review/edit, and AI save flow are in place
  - Live hinted Gemini verification passed on 2026-06-25
  - Repeated live unhinted Gemini verification also passed on 2026-06-25 after client-side malformed-output retry handling was added
- Phase 5: Mostly completed
  - Settings rework, profile editing, targets, presets, and appearance are implemented
  - Some settings-related features are still missing
- Phase 6: Planned
  - History/search/tutorial/notifications are largely not implemented
- Phase 7: Mostly completed
  - Build checks, Phase 3 verification, and Phase 4 verification were completed
  - Remaining risk is model-estimate variability rather than an unverified feature branch

## Phase 0: Stabilize The Current Build [x]

- [x] Fix `DashboardScreens.kt` syntax so the app compiles
- [x] Remove the duplicate `settings` route in `AppNavigation.kt`
- [x] Add or remove the `manual_entry` route so the add menu cannot navigate to a missing destination
- [x] Run `.\gradlew.bat :app:assembleDebug`
- [x] Remove age from onboarding, settings, database, and profile UI

## Phase 1: Room Data Model For Analysis [x]

Status:
- Core schema/query layer is implemented in Room
- Migration/data-preservation work is still pending

Current coverage:
- `UserProfile`
- `AppSettings`
- `MealLog`
- `MealItem`
- `WeightLog`
- `PresetFood`
- Daily macro totals
- Daily meal summaries
- Top-food aggregation
- BMI calculation

Still outstanding:
- Real migrations when destructive migration must be removed

## Phase 2: Manual Logging And Dashboard Data [x]

- [x] Implement `Preset Foods / Manual Entry`
- [x] Allow calories, protein, carbs, and fats entry
- [x] Persist manual entries into Room
- [x] Add preset foods support
- [x] Make preset foods editable from settings and manual entry
- [x] Replace hard-coded home summary with today's Room aggregate
- [x] Show total nutrients consumed today
- [x] Replace in-memory meal list with today's Room meals
- [x] Persist profile height/current weight changes and write weight updates to `WeightLog`
- [x] Replace static macro bars with Room-backed totals and configurable targets

## Phase 3: Interactive Analytics Dashboard [x]

Implemented:
- [x] Calendar dialog for picking a date
- [x] Daily calorie trend
- [x] Nutritional split chart
- [x] Favorite foods chart
- [x] Weight trend
- [x] BMI display
- [x] Selected-day meals and selected-day macro summary
- [x] Marker/annotation notes with visible calendar dots
- [x] Empty and sparse-data states
- [x] `Trends & Stats` summary card
- [x] Light-mode green contrast adjustment
- [x] Stable passive scrollbar thumb sizing
- [x] `Log New Weight` shortcut in the add menu with verified return flow
- [x] Progress overview calorie preview now routes into Calendar instead of acting like an interactive chart
- [x] Calendar page week browser removed
- [x] Calendar page preset strip removed
- [x] `Current Day` surfaced on the Calendar range card
- [x] Calendar calorie chart now ignores empty taps and toggles tooltip on/off for real bars
- [x] Animated fade visibility added for floating chart tooltips
- [x] Conditional calorie-chart x-axis behavior:
  - no labels when range is longer than 14 days
  - weekday/date labels when range is 14 days or less
- [x] `Past Week`, `Past Month`, and `All Time` quick-range buttons under the Calendar range card
- [x] Selected calorie-bar color and deselection return state refinement
- [x] Progress / Calendar / Stats page-entry animation to match the settings-page feel
- [x] Logged-meal edit/delete from dashboard meal surfaces
- [x] Manual food entry date override:
  - `Use Current Date` checkbox
  - single-date calendar picker when unchecked
- [x] Progress root reset behavior from the bottom navigation

Phase 3 closeout notes:
- Feature scope for the interactive analytics dashboard is complete
- Recent emulator smoke testing covered the major Progress, Calendar, Stats, navigation, and chart-interaction flows
- Remaining work around broader app-wide transitions or additional analytics ideas should be treated as future polish, not unfinished Phase 3 scope

## Phase 4: Gemini Food Recognition [x]

Done:
- [x] Remove the hardcoded API key from `CameraScreens.kt`
- [x] Add API key editing in settings
- [x] Persist and load the API key from local settings
- [x] Keep scan entry accessible from the add menu
- [x] Add actual CameraX image capture, not only preview
- [x] Add manual image import as a first-class path for debugging and normal use
- [x] Convert the captured/imported image into the shared Gemini input path
- [x] Populate `SatiationViewModel.capturedImage`
- [x] Make camera -> nutrition flow depend on a real captured image
- [x] Harden Gemini response parsing by stripping fences and validating fields
- [x] Expand Gemini output to include totals plus item-level macros/confidence
- [x] Add review/edit-before-save behavior
- [x] Save accepted AI results to Room as `MealLog` + `MealItem`
- [x] Add user-facing error states for missing key, no image, API failure, and malformed output

Closeout note:
- malformed live model output now triggers an automatic re-request inside `GeminiNutritionClient`
- semantic output variability remains normal model behavior and is handled by the review/edit step

## Phase 5: Settings Rework []

Done:
- [x] Rename/rework the profile tab into settings-oriented UI
- [x] Include profile editing
- [x] Include height editing
- [x] Include weight editing
- [x] Include Gemini API key editing
- [x] Include macro target editing
- [x] Make preset foods editable from settings
- [x] Include appearance selection

Still open:
- [ ] Keep unit preferences in settings only and fully wire them through the app
- [ ] Add logged-meal editing/removal access from settings/history
- [ ] Add data management actions such as clear/export/import

## Phase 6: Discovery, History, And Guidance []

- [ ] Build search/filter history around calendar/date browsing
- [ ] Support editing/removing entries from search/filter results
- [ ] Add first-run tutorial with skip confirmation
- [ ] Add customizable notifications/reminders

## Phase 7: Verification []

Done:
- [x] Run `.\gradlew.bat :app:assembleDebug`
- [x] Run `.\gradlew.bat :app:assembleAndroidTest`
- [x] Smoke test onboarding
- [x] Recheck onboarding edge-to-edge padding on splash, name, and weight screens
- [x] Smoke test settings/profile edit
- [x] Smoke test manual meal logging
- [x] Smoke test dashboard after meals and weight logs
- [x] Smoke test light/dark theme behavior for Progress-related changes
- [x] Recheck fresh-start default theme behavior after switching the fallback/default appearance to dark
- [x] Run parser/image/persistence instrumentation coverage
- [x] Verify live hinted Gemini scan with the provided food image and API key
- [x] Verify AI save flow updates Home, Daily Targets, and Progress

Still required:
- [ ] Continue treating semantic AI estimate variability as a product concern for future refinement, not a current Phase 4 blocker

## Additional Feature Notes From User Numbering

[ ] 1. Meal history with search and filters by date, meal type, and macro range
[ ] 2. Meal detail/edit/delete flow for logged entries across future history/search views beyond Home, Daily Targets, and Calendar
[ ] 3. Height/weight logging enhancements beyond the current basics
[x] 4. Custom food library via preset foods
[x] 5. Daily/weekly nutrition summaries and trends section
[ ] 6. Goal progress expansion beyond current macro targets
[ ] 7. Data export/import for backups/debugging/presentation
[x] 8. Better AI review screen before saving
[ ] 9. Gemini scan error recovery and fallback improvements
[ ] 10. Empty states and first-run guidance across the app
[ ] 11. Full unit-preference validation and wiring
[x] 12. Persistent graph markers/annotations
[ ] 13. Broader accessibility settings beyond current dark mode and color fixes
[ ] 14. Notifications/reminders for meal logging, weight logging, or macro target check-ins
