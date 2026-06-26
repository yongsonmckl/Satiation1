# Satiation1 Implementation Plan

This plan is based on the current repository state as of 2026-06-27. Update it whenever feature status or verification status changes materially.

Internal note:
- when committing and pushing, do not mention internal phase labels in commit messages

## Current Sitrep

- Phase 0: Completed
- Phase 1: Implemented, but preserved-data migration verification is still open
- Phase 2: Completed
- Phase 3: Completed for current feature scope
- Phase 4: Implemented and verified for current feature scope
- Phase 5: Mostly completed
- Phase 6: Mostly completed
- Phase 7: Partially completed
- Phase 8 audit: Not started as a formal repo-wide checklist pass

High-level reality:
- the app has moved beyond the old destructive-migration baseline
- settings/history/advanced/reminder work is implemented in code
- remaining risk is now mostly verification depth, migration confidence, and a few UX/polish decisions rather than large missing feature branches

## Phase 0: Stabilize The Current Build [x]

- [x] Fix `DashboardScreens.kt` syntax so the app compiles
- [x] Remove the duplicate `settings` route in `AppNavigation.kt`
- [x] Add or remove the `manual_entry` route so the add menu cannot navigate to a missing destination
- [x] Run `.\gradlew.bat :app:assembleDebug`
- [x] Remove age from onboarding, settings, database, and profile UI

## Phase 1: Room Data Model For Analysis [~]

Implemented:
- [x] Room-backed core data model exists
- [x] `UserProfile`
- [x] `AppSettings`
- [x] `MealLog`
- [x] `MealItem`
- [x] `WeightLog`
- [x] `PresetFood`
- [x] Daily macro totals
- [x] Daily meal summaries
- [x] Top-food aggregation
- [x] BMI calculation
- [x] Room schema export is enabled
- [x] Explicit migrations replaced destructive fallback for current known versions

Still open:
- [ ] Re-verify upgrade safety against preserved older databases
- [ ] Add stronger automated migration coverage

## Phase 2: Manual Logging And Dashboard Data [x]

- [x] Implement manual entry
- [x] Allow calories, protein, carbs, and fats entry
- [x] Persist manual entries into Room
- [x] Add preset foods support
- [x] Make preset foods editable from settings and manual entry flows
- [x] Replace hard-coded home summary with Room-backed totals
- [x] Show total nutrients consumed today
- [x] Replace in-memory meal list with Room-backed meals
- [x] Persist height/current weight changes and write weight updates to `WeightLog`
- [x] Replace static macro bars with Room-backed totals and configurable targets

## Phase 3: Interactive Analytics Dashboard [x]

Implemented:
- [x] Progress overview with separate Calendar and Stats destinations
- [x] Calendar dialog/range picker flow
- [x] Daily calorie trend
- [x] Nutritional split chart
- [x] Favorite foods chart
- [x] Weight trend
- [x] BMI display and BMI gauge
- [x] Selected-day meals and selected-day macro summary
- [x] Marker/annotation notes with visible calendar dots
- [x] Empty and sparse-data states
- [x] `Trends & Stats` summary card
- [x] Light-mode green contrast adjustment
- [x] `Log New Weight` shortcut in the add menu with verified return flow
- [x] Progress overview preview now routes into Calendar
- [x] `Current Day` surfaced on the Calendar range card
- [x] Calorie-chart empty-tap guard and same-bar toggle-off behavior
- [x] Conditional calorie-chart x-axis behavior
- [x] `Past Week`, `Past Month`, and `All Time` quick-range behavior
- [x] Selected calorie-bar highlight and deselection return state refinement
- [x] Progress / Calendar / Stats page-entry animation
- [x] Logged-meal edit/delete from dashboard meal surfaces
- [x] Manual-entry date override
- [x] Progress root reset behavior from the bottom navigation
- [x] Responsive quick-range labels and selected-bar indicator

Residual note:
- chart annotations still live in shared preferences rather than Room
- any further analytics improvements should be treated as future polish, not unfinished Phase 3 scope

## Phase 4: Gemini Food Recognition [x]

Implemented:
- [x] Remove hardcoded API key from source
- [x] Persist and edit Gemini API key locally
- [x] Keep scan entry accessible from the add flow
- [x] Add real CameraX image capture
- [x] Add manual image import inside the camera flow
- [x] Route capture/import into shared `capturedImage`
- [x] Make nutrition flow depend on a real current image
- [x] Harden Gemini response parsing
- [x] Expand Gemini output to totals plus item-level macros/confidence
- [x] Add review/edit-before-save behavior
- [x] Save accepted AI results to Room as `MealLog` + `MealItem`
- [x] Add user-facing error states for missing key, no image, API failure, and malformed output
- [x] Add model fallback from Pro to Flash
- [x] Retry transient high-demand failures

Verified:
- [x] Build `.\gradlew.bat :app:assembleDebug`
- [x] Build `.\gradlew.bat :app:assembleAndroidTest`
- [x] Parser/image/persistence instrumentation coverage
- [x] Live hinted Gemini verification
- [x] Repeated live no-hint verification after malformed-output retry handling
- [x] AI save flow updates Home, Daily Targets, and Progress

Residual note:
- semantic output variability remains a product concern and is intentionally handled by the review/edit step

## Phase 5: Settings Rework [~]

Implemented:
- [x] Rework the profile tab into a settings-oriented hub
- [x] Include profile editing
- [x] Include height editing
- [x] Include weight editing
- [x] Include Gemini API key editing
- [x] Include macro target editing
- [x] Make preset foods editable from settings
- [x] Move lower-priority settings into a nested `Settings` destination
- [x] Add `Display Units` with app-wide metric/imperial preference persistence
- [x] Add history access from settings
- [x] Add `Advanced` data-management actions for export/import/clear/date format
- [x] Add save-to-preset prompts for manual and AI meal flows
- [x] Remove the separate add-menu image-import shortcut and keep import inside the camera flow
- [x] Replace destructive Room migration fallback with explicit migrations
- [x] Turn on schema export and store schema snapshots

Still open:
- [ ] Re-check unit labels and summaries across chart/snapshot surfaces
- [ ] Re-verify migration safety with preserved older data
- [ ] Decide whether chart annotations should move from shared preferences into Room
- [ ] Add stronger verification coverage for export/import/clear flows

## Phase 6: Discovery, History, And Guidance [~]

Implemented:
- [x] Build search/filter history around Room-backed meal browsing
- [x] Support editing/removing entries from history results
- [x] Add first-run guide with skip confirmation
- [x] Add onboarding Gemini API key entry before setup completion
- [x] Add reminder settings and scheduling
- [x] Rework appearance so system theme locks manual theme selection
- [x] Add accent-color selection support
- [x] Remove the custom right-side scrollbar treatment
- [x] Rename the manual-entry header to `Manual Entry`
- [x] Add BMI gauge support to the Stats surface

Still open:
- [ ] Verify history edit/delete behavior thoroughly with populated real records across all linked surfaces
- [ ] Run longer timed reminder delivery verification on-device/emulator
- [ ] Redesign reminders into multi-reminder form-based scheduling with weekday support and a wheel-style time picker
- [ ] Continue accessibility, empty-state, and first-use guidance polish across remaining flows

## Phase 7: Verification [~]

Completed:
- [x] Run `.\gradlew.bat :app:assembleDebug`
- [x] Run `.\gradlew.bat :app:assembleAndroidTest`
- [x] Run `.\gradlew.bat :app:testDebugUnitTest`
- [x] Smoke test onboarding
- [x] Recheck onboarding edge-to-edge padding on splash, name, and weight screens
- [x] Smoke test settings/profile edit
- [x] Smoke test manual meal logging
- [x] Smoke test dashboard after meals and weight logs
- [x] Smoke test light/dark theme behavior for Progress-related changes
- [x] Recheck fresh-start default theme behavior after the default-dark/follow-system changes
- [x] Run parser/image/persistence instrumentation coverage
- [x] Verify live Gemini scan flows for current scope
- [x] Add targeted local tests for display preferences
- [x] Add targeted local tests for history filtering logic
- [x] Add targeted local tests for reminder scheduling logic

Still required:
- [ ] Add preserved-data migration verification
- [ ] Add export/import and clear-data verification
- [ ] Add broader end-to-end integration and usability passes
- [ ] Capture stronger evidence for history edit/delete propagation and reminder delivery behavior

## Phase 8: Cross-Phase Audit [ ]

Not yet completed:
- [ ] Re-read all original Phase 1-7 items and convert them into an explicit audit checklist
- [ ] Mark each item as verified, under-verified, partial, or not implemented
- [ ] Resolve any mismatch between `.harness` docs and repository reality
- [ ] Produce a final gap list for true Phase 1-7 completion

## Additional Feature Notes From User Numbering

[x] 1. Meal history with search and filters by date, meal type, and calorie/macro-related scope
[x] 2. Meal detail/edit/delete flow for logged entries across history/search views beyond Home, Daily Targets, and Calendar
[ ] 3. Height/weight logging enhancements beyond the current basics
[x] 4. Custom food library via preset foods
[x] 5. Daily/weekly nutrition summaries and trends section
[ ] 6. Goal progress expansion beyond current macro targets
[x] 7. Data export/import for backups/debugging/presentation
[x] 8. Better AI review screen before saving
[~] 9. Gemini scan error recovery and fallback improvements
[x] 10. Empty states and first-run guidance across the app
[~] 11. Full unit-preference validation and wiring
[x] 12. Persistent graph markers/annotations
[ ] 13. Broader accessibility settings beyond current dark mode and color fixes
[x] 14. Notifications/reminders for meal logging, weight logging, or macro target check-ins

Legend:
- `[x]` implemented for current scope
- `[~]` implemented but still under-verified or still carrying known follow-up work
- `[ ]` still open
