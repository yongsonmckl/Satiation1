# Phase 5-6-7 Task List

This file is the implementation guide and remaining-work record for the open scope across Phase 5, Phase 6, Phase 7, and the final Phase 8 cross-phase audit.

Latest update:
- Date: 2026-06-27
- Repository reality reviewed against code, tests, schema snapshots, manifest, and current `.harness` notes
- Phase 5 status: substantially completed
- Phase 6 status: substantially completed
- Phase 7 status: partially completed
- Main remaining risks: migration confidence, export/import verification, populated-data history verification, reminder delivery verification, and broader end-to-end usability coverage

## Current Baseline

Already present in the app:
- Room-backed meals, meal items, weights, profile, settings, and preset foods
- explicit Room migrations from `4 -> 5` and `5 -> 6`
- schema export under `app/schemas`
- editable Gemini API key in onboarding and settings
- editable macro targets in settings
- follow-system/manual theme behavior
- primary and secondary accent-color overrides
- nested settings route
- display-units preference persistence
- date-format preference persistence
- meal history route with search/filter behavior
- meal detail/edit/delete reuse from history results
- backup/export/import/clear actions with category selection
- first-run onboarding Gemini API key step
- first-run guide with skip/exit path
- reminder settings, scheduler wiring, and reboot rescheduling support
- instrumentation coverage for Gemini parsing, image loading, AI persistence, and live Gemini checks
- local/unit coverage for display preferences, history filtering, and reminder scheduling logic

Confirmed remaining issues:
- some summary/chart labels still need a full unit-consistency audit outside the already converted profile/onboarding/weight flows
- marker notes still persist in shared preferences instead of Room
- preserved-data migration safety still needs verification against older real or fixture-backed databases
- some Phase 6 surfaces are implemented but still need fuller data-backed interaction verification, especially history edit/delete with populated records and reminder delivery timing
- export/import/clear behavior exists in code but still lacks strong automated or repeatable verification evidence

## Definition Of Done

Phase 5-6-7 is complete when all of the following are true:
- users can manage settings without hidden partially wired options
- unit preference is editable, persisted, and consistently reflected in profile, onboarding, weight, and related displays
- users can browse meal history outside the dashboard-only views
- history supports meaningful search and filtering
- history results support edit and delete flows with confirmed propagation
- users have a first-run guide with a clear skip/exit path
- notifications/reminders can be configured, scheduled, updated, and disabled cleanly
- users have a safe data-management surface for backup/export/import and destructive reset actions
- Room upgrades preserve intended data across supported versions
- verification covers the new flows and the highest-risk persistence paths
- a final end-to-end audit confirms the original Phase 1-7 plan is implemented coherently across the shipped app

## Task Breakdown

### 5.1 Unit preference wiring

- [x] Add a visible unit-preference setting instead of keeping `preferredUnits` as storage-only state
- [x] Support at minimum `metric` and `imperial`
- [x] Convert profile, onboarding, and weight-edit surfaces to respect the selected unit mode
- [x] Keep persistence canonical internally
- [x] Add local/unit coverage for core display-preference helpers
- [ ] Re-check target, summary, and chart labels for unit consistency

Current status:
- `DisplayPreferences.kt` is the main conversion/formatting helper
- `DisplayPreferencesTest.kt` covers unit conversion and date-format behavior
- open work is mostly a UI label audit, not core conversion plumbing

### 5.2 Settings/history access expansion

- [x] Restructure settings so low-priority controls move into a nested `Settings` destination
- [x] Keep top-level settings focused on profile/API key/settings navigation
- [x] Keep the nested `Edit Profile` flow focused on name/pronouns/height/weight
- [x] Remove `Starting Weight` from the visible profile card
- [x] Build the nested `Settings` destination with:
  - `Display Units`
  - `Edit Nutrients`
  - `Appearance`
  - `Meal History`
  - `Notifications`
  - `Advanced`
- [x] Add a settings-accessible history entry point
- [x] Define a stable history route in navigation
- [x] Reuse existing meal edit/delete behavior from history
- [x] Support opening logged meal details from history results
- [x] Remove the separate add-menu import-photo action
- [x] Keep image import reachable inside the camera scan flow
- [x] Add `Save As Preset Meal` prompts/actions for manual and AI flows
- [x] Remove the embedded preset-food list from the lower manual-entry screen

Current status:
- implementation is present and routed through `SettingsPhase5Screens.kt`, `DashboardScreens.kt`, and `AppNavigation.kt`
- remaining risk is not missing UI but fuller behavioral verification with realistic data

### 5.3 Data management actions

- [x] Add an `Advanced` destination under nested settings
- [x] Add explicit destructive actions only behind confirmation dialogs
- [x] Add export flow for selected categories
- [x] Support selective export choices
- [x] Add import/restore flow with validation and failure handling
- [x] Add database-clear controls only inside `Advanced`
- [x] Add date-format preferences in `Advanced`
- [x] Include marker-note annotations in export/import/clear behavior for now

Current status:
- `AppBackupSupport.kt` builds/parses the JSON payload
- `SatiationViewModel` executes export/import/clear orchestration
- confirmation dialogs exist in the UI
- open work is verification depth, not missing functionality

### 5.4 Persistence hardening

- [x] Replace destructive Room migration fallback with real migrations
- [x] Turn on schema export and keep schema snapshots in version control
- [ ] Decide whether chart annotations should move from shared preferences into Room
- [ ] Re-verify upgrade safety against existing local data
- [ ] Add stronger automated migration coverage if feasible

Current status:
- database version is `6`
- explicit `4 -> 5` and `5 -> 6` migrations are configured
- schema snapshots exist for versions `5` and `6`
- migration safety is still under-verified against preserved data

### 6.1 Meal history and search

- [x] Build a dedicated history surface around Room meal queries
- [x] Add date-range filtering beyond the Calendar page
- [x] Add text search over meal names/categories/notes where appropriate
- [x] Add meal-type/source filtering
- [x] Add calorie-band filtering
- [x] Keep loading, empty, and no-match states explicit
- [x] Add local/unit tests for main filter logic paths

Current status:
- `HistoryScreen` exists in `SettingsPhase5Screens.kt`
- `HistorySupport.kt` contains the pure filter logic
- `HistorySupportTest.kt` covers representative filter combinations

### 6.2 History result actions

- [x] Allow editing meals directly from history results
- [x] Allow deleting meals directly from history results
- [x] Reuse existing meal-write logic
- [x] Guard destructive actions with confirmation dialogs
- [ ] Verify populated-data edit propagation across Home, Daily Targets, Progress, and history
- [ ] Verify populated-data delete propagation across the same surfaces

Current status:
- implementation is present
- under-verification remains the real gap

### 6.3 First-run tutorial and guidance

- [x] Add a first-run guide flow
- [x] Insert an onboarding step for the Gemini API key before setup completion
- [x] Keep Gemini API key entry uncensored on onboarding/settings screens
- [x] Include a skip path with confirmation
- [x] Cover manual logging, camera/Gemini scanning, Progress, and settings/API key setup in the guide copy
- [x] Keep the guide as an onboarding-first flow

Current status:
- `OnboardingApiKeyScreen` and `OnboardingGuideScreen` live in `Phase6Screens.kt`
- emulator QA was previously recorded for the cold-start onboarding path

### 6.4 Notifications and reminders

- [x] Choose the scheduling approach
- [x] Add reminder settings UI inside nested settings
- [x] Add notification permission handling where required
- [x] Create notification channels and reminder copy
- [x] Support meal logging, weight logging, and macro check-in reminders
- [x] Reschedule reminders after reboot
- [x] Add local/unit coverage for next-trigger scheduling logic
- [ ] Run longer timed end-to-end reminder delivery verification
- [ ] Future redesign: replace the fixed reminder toggles with a multi-reminder builder
- [ ] Future redesign: support weekday-specific scheduling
- [ ] Future redesign: replace the basic editor with a wheel-style time picker

Current status:
- reminders are AlarmManager-based
- settings persist in Room-backed `AppSettings`
- `ReminderReceiver` schedules the next instance after firing
- `ReminderBootReceiver` restores schedules after reboot

### 6.5 Accessibility and empty-state follow-through

- [x] Rework `Appearance` so `Use System Theme` locks manual selection while enabled
- [x] Expose manual light/dark choice only when follow-system is disabled
- [x] Keep appearance controls inside nested settings
- [x] Add customizable accent-color controls
- [x] Add a BMI gauge to Stats
- [x] Remove the custom right-edge scrollbar treatment
- [x] Keep plus-menu interaction reliable with visible system navigation
- [x] Rename the manual-entry header to `Manual Entry`
- [x] Make Progress Calendar quick-range buttons responsive on smaller screens
- [x] Add a clearer selected-bar indicator
- [ ] Expand accessibility/settings support beyond current appearance and color work
- [ ] Audit major flows for readable copy, tap targets, and non-data empty states
- [ ] Add guidance states for first use in areas that still assume prior data
- [ ] Evaluate weekly aggregation for `Past Month` if it improves chart usability without harming clarity

### 7.1 Verification closeout

- [x] Re-run `.\gradlew.bat :app:assembleDebug` after major batches
- [x] Add targeted tests for display preferences
- [x] Add targeted tests for history search/filter behavior
- [x] Add targeted tests for reminder scheduling logic
- [x] Emulator-verify key new user flows end to end for currently documented passes
- [ ] Add targeted tests for export/import or data-reset behavior
- [ ] Add targeted tests for Room migration safety
- [ ] Capture stronger evidence for reminder timing and history propagation

### 7.2 AI and product-risk follow-up

- [ ] Keep treating semantic AI estimate variability as a product-quality concern instead of a parser bug
- [ ] After AI meal confirmation, generate a shorter summarized saved-meal name for downstream lists
- [ ] Reconcile CameraX preview and capture framing if future QA still shows mismatch
- [ ] Decide whether additional Gemini reliability work belongs in this phase range or a later refinement phase
- [ ] If retained here, record evaluation notes for repeated scan variance and user-review expectations

### 8.1 Phase 1-7 implementation audit

- [ ] Re-read `.harness/PLAN.md` and convert every Phase 1-7 item into an explicit audit checklist
- [ ] Mark each original plan item as:
  - implemented and verified
  - implemented but under-verified
  - partially implemented
  - not implemented
- [ ] Resolve any mismatch between `.harness/PLAN.md`, `.harness/AGENTS.md`, and repository reality
- [ ] Record any scope that was claimed complete earlier but is still behaviorally incomplete
- [ ] Produce a final gap list for anything still blocking true Phase 1-7 completion

### 8.2 Cross-phase integration validation

- [ ] Verify onboarding, settings, manual entry, Progress, camera scan, AI save flow, and history/data-management features all work together in one app session
- [ ] Verify edits and deletes propagate correctly across Home, Daily Targets, Progress, history, and any export/import flows
- [ ] Verify profile, targets, appearance, API key, and unit preferences interact without stale state or navigation regressions
- [ ] Verify AI-saved meals and manually saved meals coexist correctly in analytics, history, and summaries
- [ ] Verify backup/import/export/migration behavior does not break downstream screens or calculations

## Suggested Implementation Order

- [ ] 1. Finish migration-confidence work before claiming Phase 1/5 persistence hardening is truly closed
- [ ] 2. Complete the unit-label/chart-label audit
- [ ] 3. Verify populated-data history edit/delete propagation
- [ ] 4. Verify export/import/clear behavior with realistic data sets
- [ ] 5. Run longer reminder-delivery checks
- [ ] 6. Run the formal Phase 8 audit checklist across original Phase 1-7 scope

## Verification Checklist

- [x] Run `.\gradlew.bat :app:assembleDebug`
- [x] Run `.\gradlew.bat :app:assembleAndroidTest`
- [x] Run `.\gradlew.bat :app:testDebugUnitTest`
- [x] Verify history can find meals by date and at least one non-date filter
- [x] Verify first-run guide can be skipped and does not loop unexpectedly
- [x] Verify local/unit coverage exists for:
  - display preferences
  - history filtering
  - reminder scheduling logic
- [ ] Verify unit-preference changes affect all major summaries/charts consistently
- [ ] Verify history edit updates existing meals without duplication
- [ ] Verify history delete removes meals from all Room-backed surfaces
- [ ] Verify reminder settings persist and produce the expected scheduled behavior
- [ ] Verify export produces usable output and import handles invalid input safely
- [ ] Verify migration from an older local database preserves user data
- [ ] Verify marker-note behavior still matches the chosen persistence/export strategy
- [ ] Verify the final Phase 8 audit checklist covers all original `PLAN.md` Phase 1-7 items
- [ ] Verify unit, integration, and usability outcomes are captured back into `.harness`

## Primary Files To Watch

- `app/src/main/java/com/mckl/satiation1/ui/screens/DashboardScreens.kt`
- `app/src/main/java/com/mckl/satiation1/ui/screens/SettingsPhase5Screens.kt`
- `app/src/main/java/com/mckl/satiation1/ui/screens/Phase6Screens.kt`
- `app/src/main/java/com/mckl/satiation1/navigation/AppNavigation.kt`
- `app/src/main/java/com/mckl/satiation1/navigation/SatiationViewModel.kt`
- `app/src/main/java/com/mckl/satiation1/database/AppDatabase.kt`
- `app/src/main/java/com/mckl/satiation1/history/HistorySupport.kt`
- `app/src/main/java/com/mckl/satiation1/reminders/ReminderScheduler.kt`
- `app/src/main/java/com/mckl/satiation1/backup/AppBackupSupport.kt`
- `app/src/main/java/com/mckl/satiation1/DisplayPreferences.kt`
- `app/src/main/AndroidManifest.xml`

## Risks To Watch

- unit conversion can silently corrupt profile or weight meaning if display units and stored units are mixed incorrectly
- history/search can drift from shared meal semantics if write-path reuse is bypassed
- reminder features can expand quickly into platform edge cases if redesigned without constraints
- marker notes still live outside Room, which complicates migration and backup decisions
- migration verification still needs real preserved-data coverage rather than only compile/runtime success
- external Gemini behavior can still vary even when parser and retry logic are correct
