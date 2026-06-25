# Phase 3-4 Task List

This file is now an archive bridge note. Phase 3 is complete, and the active detailed Phase 4 record lives in `.harness/Phase 3-4/PHASE4TASKLIST.md`.

## Scope

- Phase 3: Interactive Analytics Dashboard
- Phase 4: Gemini 1.5 Flash Food Recognition

## Current Status Summary

### Phase 3

Status: completed

Implemented:
- Progress overview plus separate Calendar and Stats pages
- Date picker dialog
- Simplified Calendar range card with `Choose Range`
- `Current Day` text on the Calendar range card
- Quick-range buttons:
  - `Past Week`
  - `Past Month`
  - `All Time` when history exists
- Selected-day meals and selected-day summary
- Calorie trend chart
- Calorie chart empty-tap guard
- Calorie chart tap-again-to-dismiss tooltip behavior
- Animated fade visibility for floating chart tooltips
- Conditional x-axis labels based on selected range length
- Selected calorie-bar highlight and deselection color reset
- Nutritional split chart
- Favorite foods chart
- Weight trend chart
- Trends/statistics card
- Marker dots and notes
- Light-mode green contrast fix
- Stable scrollbar thumb
- Add-menu `Log New Weight` flow
- Manual-entry date override with `Use Current Date` and single-day picker
- Meal edit/delete from Home, Daily Targets, and Calendar day-detail cards
- Progress / Calendar / Stats page-entry animation
- Progress bottom-tab reset back to the Progress overview root

Optional future refinements:
- Richer point-level chart inspection
- Logged-meal edit/delete from history

Closeout note:
- Phase 3 scope is considered complete based on the implemented feature set and the recent emulator verification passes
- Remaining dashboard work, if any, should be treated as later polish or future enhancement rather than open Phase 3 scope

### Phase 4

Status: implemented for current scope

Implemented:
- real CameraX still-image capture
- shared captured-image state through `SatiationViewModel.capturedImage`
- manual image import from the camera screen
- separate add-menu `Scan Food (Import Photo)` shortcut
- Gemini request construction with optional user hint
- defensive Gemini parsing and validation
- review/edit-before-save UX with confidence display
- retake and manual-entry fallback paths
- AI save into Room as `MealLog` plus `MealItem`
- propagation of saved AI meals into Home, Daily Targets, and Progress
- raw-response debug panel for development
- instrumentation coverage for parsing, image loading, persistence, and live Gemini checks

Current open risks:
- semantic AI output variability remains normal and should still be reviewed before save

---

## Current Implementation Tasks

### Phase 3

Phase 3 is complete.

Completed Phase 3 closeout items:
- [x] Add conditional calorie-chart x-axis behavior:
- [x] no labels when the selected range is longer than 2 weeks
- [x] weekday/date labels when the selected range is 2 weeks or less
- [x] Run focused emulator verification for the refined Progress UI
- [x] Confirm the calendar flow is stable across overview, calendar, stats, and range selection states
- [x] Add `Past Week`, `Past Month`, and `All Time` quick-range buttons under the Calendar range card
- [x] Final polish on the selected calorie-bar color and its deselection return state
- [x] Add Progress / Calendar / Stats page-entry animation
- [x] Add manual-entry date override:
- [x] `Use Current Date` checkbox
- [x] single-date picker when unchecked
- [x] Add logged-meal edit/delete from:
- [x] Home `Meals Eaten`
- [x] Daily Targets meal list
- [x] Calendar `Meals for [Date]`

### Phase 4 Closeout Snapshot

- [x] Build with `.\gradlew.bat :app:assembleDebug`
- [x] Build with `.\gradlew.bat :app:assembleAndroidTest`
- [x] Run parser/image/persistence instrumentation coverage
- [x] Run live hinted Gemini scan with the provided food image
- [x] Run repeated live no-hint Gemini scans successfully after malformed-output retry handling was added
- [x] Verify persisted AI result updates Home, Daily Targets, and Progress
- [x] Re-automate denied-camera-permission verification in the same depth as the current live pass

---

## Recommended Next Order

1. Read `.harness/Phase 3-4/PHASE4TASKLIST.md` first for the full current implementation and verification record.
2. Decide whether the next priority is:
   - hardening no-hint live Gemini reliability, or
   - deeper permission-flow re-automation.
3. Only after that, consider Phase 5 or broader product work.

## Extra Addons

1. Manual image import is already implemented and should remain a first-class debugging and normal-use path.
2. Optional user prompting before sending the picture to Gemini is already implemented.
