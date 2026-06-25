# Phase 5-6-7 Task List

This file is the implementation guide and remaining-work record for the open scope across Phase 5, Phase 6, and Phase 7.

Latest implementation update:
- Date: 2026-06-25
- Overall remaining-phase status: partially implemented
- Phase 5 status: mostly completed, with settings/data-management gaps still open
- Phase 6 status: planned, with most discovery/history/guidance work still open
- Phase 7 status: partially completed, with verification hardening still open
- Current build baseline:
  - `.\gradlew.bat :app:assembleDebug` passed during recent Phase 4 verification
  - `.\gradlew.bat :app:assembleAndroidTest` passed during recent Phase 4 verification

Phase 5-6-7 goal:
- finish the remaining settings and data-management work
- add history/search/discovery surfaces that work beyond the current dashboard views
- add first-run guidance and notifications/reminders
- remove the main persistence and verification risks that still block the app from feeling production-ready for its current scope

## Current Baseline

Already present in the app:
- Room-backed meals, meal items, weights, profile, settings, and preset foods
- editable Gemini API key in settings
- editable macro targets in settings
- appearance switching in settings
- preset-food management in settings
- manual meal create/edit flow
- meal edit/delete from Home, Daily Targets, and Calendar
- weight logging and current-weight editing
- Progress overview, Calendar, and Stats surfaces
- Gemini scan capture/import/review/save flow
- instrumentation coverage for Gemini parsing, image loading, AI persistence, and live Gemini scan checks

Confirmed remaining issues from docs and code:
- `AppSettings.preferredUnits` exists in Room, but unit presentation is still hard-coded around `kg` and `cm`
- there is no dedicated history/search route in navigation yet
- there is no implemented notification/reminder stack, permission flow, or scheduler dependency
- there is no implemented in-app export/import or clear-data management flow
- Room still uses `fallbackToDestructiveMigration(dropAllTables = true)`
- Room schema export is disabled with `exportSchema = false`
- Android backup/data-extraction XML files are still template placeholders
- marker notes still persist in shared preferences instead of Room

## Definition Of Done

Phase 5-6-7 is complete when all of the following are true:
- users can manage settings without hidden partially wired options
- unit preference is editable, persisted, and consistently reflected in profile, onboarding, weight, and related displays
- users can browse meal history outside the current dashboard-only views
- history supports meaningful search and filtering
- history results support edit and delete flows
- users have a first-run tutorial or guidance flow with a clear skip/exit path
- notifications/reminders can be configured, scheduled, updated, and disabled cleanly
- users have a safe data-management surface for backup/export/import and destructive reset actions
- Room no longer relies on destructive migration for normal app upgrades
- verification covers the new flows and the highest-risk persistence paths

## Task Breakdown

### 5.1 Unit preference wiring

- [ ] Add a visible unit-preference setting instead of keeping `preferredUnits` as storage-only state.
- [ ] Decide and document the supported unit modes, at minimum `metric` and `imperial`.
- [ ] Convert profile, onboarding, and weight-edit surfaces to respect the selected unit mode.
- [ ] Keep persistence canonical internally so conversions do not corrupt stored values.
- [ ] Re-check target, summary, and chart labels for unit consistency.

Why this is still open:
- `AppSettings.preferredUnits` exists in Room, but current UI helpers still format height and weight as `cm` and `kg` directly.

### 5.2 Settings/history access expansion

- [ ] Add a settings-accessible history entry point instead of relying only on Home, Daily Targets, and Calendar detail cards.
- [ ] Define a stable history route in navigation.
- [ ] Reuse existing meal edit/delete behavior instead of duplicating meal-write logic.
- [ ] Support opening logged meal details from history results.

Why this is still open:
- current navigation routes do not include a history/search destination.

### 5.3 Data management actions

- [ ] Add a data-management section in settings.
- [ ] Add explicit destructive actions only behind confirmation dialogs.
- [ ] Add export flow for meals, weights, profile/settings, or a scoped subset that matches the capstone/demo needs.
- [ ] Add import or restore flow with validation and failure handling.
- [ ] Decide whether marker notes should be included in export/import.

Why this is still open:
- import/export is still called out as missing in `.harness/AGENTS.md`
- Android backup XML files are still stock templates rather than intentional app rules

### 5.4 Persistence hardening

- [ ] Replace destructive Room migration fallback with real migrations.
- [ ] Turn on schema export and keep schema snapshots in version control.
- [ ] Decide whether chart annotations should move from shared preferences into Room before backup/export work is finalized.
- [ ] Re-verify upgrade safety against existing local data.

Why this is still open:
- the database still uses `fallbackToDestructiveMigration(dropAllTables = true)`
- `exportSchema` is still `false`

### 6.1 Meal history and search

- [ ] Build a dedicated history surface around the existing Room meal queries.
- [ ] Add date-range filtering that works beyond the current Calendar page.
- [ ] Add text search over meal names and notes where appropriate.
- [ ] Add meal-type/source filtering, including at least manual vs AI scan.
- [ ] Add macro-range or calorie-range filtering if it remains part of the required scope.
- [ ] Keep empty, no-match, and loading states explicit.

### 6.2 History result actions

- [ ] Allow editing meals directly from history results.
- [ ] Allow deleting meals directly from history results.
- [ ] Ensure edits and deletes propagate back into Home, Daily Targets, Progress, and history itself.
- [ ] Guard destructive actions with the same confirmation quality used elsewhere.

### 6.3 First-run tutorial and guidance

- [ ] Add a first-run guidance flow or tutorial entry experience.
- [ ] Include a skip path with confirmation so users do not get trapped in onboarding guidance.
- [ ] Cover the main product surfaces:
  - manual logging
  - camera/Gemini scanning
  - Progress
  - settings/API key setup
- [ ] Decide whether the tutorial is one-time only, re-openable from settings, or both.

### 6.4 Notifications and reminders

- [ ] Choose the scheduling approach for reminders.
- [ ] Add reminder settings UI for enabling, disabling, and configuring times/types.
- [ ] Add Android notification permission handling where required by platform behavior.
- [ ] Create notification channels and user-visible reminder copy.
- [ ] Support at least meal logging, weight logging, or macro check-in reminders if those remain the intended scope.
- [ ] Verify reminders survive process death and reboot if that is required for the chosen implementation.

Why this is still open:
- there is no notification permission, channel, scheduler, or worker/alarm implementation in the current app module

### 6.5 Accessibility and empty-state follow-through

- [ ] Expand accessibility/settings support beyond the current appearance toggle and color refinements.
- [ ] Audit major flows for readable copy, tap targets, and non-data empty states.
- [ ] Add guidance states for first use in areas that still assume prior data.

### 7.1 Verification closeout

- [ ] Re-run `.\gradlew.bat :app:assembleDebug` after each major remaining batch.
- [ ] Add targeted tests for unit conversion and settings persistence.
- [ ] Add targeted tests for history search/filter behavior.
- [ ] Add targeted tests for export/import or data reset behavior.
- [ ] Add targeted tests for Room migration safety.
- [ ] Add targeted tests for reminder scheduling logic where feasible.
- [ ] Emulator-verify the new user flows end to end.

### 7.2 AI and product-risk follow-up

- [ ] Keep treating semantic AI estimate variability as a product-quality concern instead of a parser bug.
- [ ] Decide whether future Gemini reliability work belongs in Phase 5-7 scope or a later refinement phase.
- [ ] If retained in this phase range, add evaluation notes for repeated scan variance and user-review expectations.

## Suggested Implementation Order

- [ ] 1. Finish persistence hardening before adding import/export on top of a destructive-migration baseline.
- [ ] 2. Wire unit preferences fully so settings no longer expose partially implemented state.
- [ ] 3. Add the dedicated history/search route and reuse existing meal edit/delete flows there.
- [ ] 4. Add data-management actions after persistence rules are explicit.
- [ ] 5. Add the first-run tutorial/guidance flow.
- [ ] 6. Add notifications/reminders after the settings model and user flows are stable.
- [ ] 7. Close with migration, history, export/import, and reminder verification.

## Verification Checklist

- [ ] Run `.\gradlew.bat :app:assembleDebug`.
- [ ] Run `.\gradlew.bat :app:assembleAndroidTest`.
- [ ] Verify unit-preference changes affect onboarding, profile, and weight editing consistently.
- [ ] Verify history can find meals by date and at least one non-date filter.
- [ ] Verify history edit updates existing meals without duplication.
- [ ] Verify history delete removes meals from all Room-backed surfaces.
- [ ] Verify first-run tutorial can be skipped and does not loop unexpectedly.
- [ ] Verify reminder settings persist and produce the expected scheduled behavior.
- [ ] Verify export produces usable output and import handles invalid input safely.
- [ ] Verify migration from an older local database preserves user data.
- [ ] Verify marker-note behavior still matches the chosen persistence/export strategy.

## Optional Add-Ons

- [ ] Move chart annotations into Room so they join the main data lifecycle cleanly.
- [ ] Add richer meal-detail presentation in history results before edit/delete.
- [ ] Add more explicit AI-source filtering or AI-only history summaries.
- [ ] Add a settings entry to replay the tutorial after first run.

## Primary Files To Watch

- `app/src/main/java/com/mckl/satiation1/ui/screens/DashboardScreens.kt`
- `app/src/main/java/com/mckl/satiation1/navigation/AppNavigation.kt`
- `app/src/main/java/com/mckl/satiation1/navigation/SatiationViewModel.kt`
- `app/src/main/java/com/mckl/satiation1/database/AppDatabase.kt`
- `app/src/main/java/com/mckl/satiation1/ui/screens/OnboardingScreens.kt`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/xml/data_extraction_rules.xml`
- `app/src/main/res/xml/backup_rules.xml`

## Risks To Watch

- adding export/import before real migrations could lock in fragile persistence behavior
- unit conversion can silently corrupt profile or weight meaning if display units and stored units are mixed incorrectly
- history/search can duplicate dashboard logic if meal-query reuse is not planned carefully
- reminder features can expand quickly into platform edge cases if the scheduling approach is chosen late
- marker notes currently live outside Room, which complicates backup, migration, and export decisions
