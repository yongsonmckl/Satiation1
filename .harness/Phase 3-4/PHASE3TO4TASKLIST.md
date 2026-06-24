# Phase 3-4 Task List

This file reflects the current state with Phase 3 complete and Phase 4 still in progress.

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

Status: active incomplete work

Already done:
- Gemini API key editor exists in settings
- API key is stored locally in settings
- Camera entry point exists in the add menu
- Camera preview screen exists
- Gemini dependency is installed
- Nutrition detail screen exists

Still missing:
- Actual still-image capture
- Real captured-image handoff into `SatiationViewModel.capturedImage`
- Real camera -> nutrition scan flow based on captured content
- Hardened Gemini parsing
- Review/edit-before-save UX
- Save accepted AI results into Room
- Error handling for missing key/image/API/parsing failures

---

## Current Implementation Tasks

### Phase 3

Phase 3 is complete.

Completed Phase 3 closeout items:
- [x] Add conditional calorie-chart x-axis behavior:
  - no labels when the selected range is longer than 2 weeks
  - weekday/date labels when the selected range is 2 weeks or less
- [x] Run focused emulator verification for the refined Progress UI
- [x] Confirm the calendar flow is stable across overview, calendar, stats, and range selection states
- [x] Add `Past Week`, `Past Month`, and `All Time` quick-range buttons under the Calendar range card
- [x] Final polish on the selected calorie-bar color and its deselection return state
- [x] Add Progress / Calendar / Stats page-entry animation
- [x] Add manual-entry date override:
  - `Use Current Date` checkbox
  - single-date picker when unchecked
- [x] Add logged-meal edit/delete from:
  - Home `Meals Eaten`
  - Daily Targets meal list
  - Calendar `Meals for [Date]`

### Phase 4

#### 4.1 Camera capture pipeline

- [ ] Replace preview-only camera flow with real still-image capture
- [ ] Add/verify CameraX `ImageCapture` wiring
- [ ] Capture a real image when the user taps `Take Photo & Scan`
- [ ] Keep permission and lifecycle behavior stable after capture changes

#### 4.2 Captured image state handling

- [ ] Convert the captured image into a `Bitmap` or supported Gemini image part
- [ ] Populate `SatiationViewModel.capturedImage`
- [ ] Ensure the captured image survives navigation into `NutritionDetailScreen`
- [ ] Remove reliance on the unused fallback image parameter path

#### 4.3 Gemini request construction

- [ ] Build the request from the real captured image
- [ ] Expand the prompt to return totals and item-level macros
- [ ] Include calories, protein, carbs, fats, category, and confidence where available
- [ ] Keep the output strictly JSON-oriented

#### 4.4 Response parsing hardening

- [ ] Strip markdown code fences before parsing
- [ ] Validate top-level JSON shape
- [ ] Validate numeric fields
- [ ] Validate item list fields
- [ ] Prevent malformed model output from crashing the screen

#### 4.5 Review-and-edit flow

- [ ] Show total calories/macros before save
- [ ] Show item-level values before save
- [ ] Allow user confirmation before logging
- [ ] Add editing capability if feasible within current scope

#### 4.6 Save to Room

- [ ] Map accepted AI result into `MealLog`
- [ ] Map accepted AI items into `MealItem`
- [ ] Save AI meals with source type `ai_scan`
- [ ] Verify saved AI meals update Home, Daily Targets, and Progress

#### 4.7 Error handling and fallback paths

- [ ] Missing API key state
- [ ] No image captured state
- [ ] Network/API failure state
- [ ] Invalid model output state
- [ ] Retry behavior where practical
- [ ] Manual entry remains the fallback when scanning fails

#### 4.8 Verification

- [ ] Build with `.\gradlew.bat :app:assembleDebug`
- [ ] Smoke test camera permission flow
- [ ] Smoke test real image capture
- [ ] Smoke test camera -> nutrition navigation
- [ ] Smoke test Gemini scan with a valid user-provided API key
- [ ] Verify accepted AI results save correctly into Room
- [ ] Verify saved AI meals appear on Home, Daily Targets, and Progress

---

## Recommended Next Order

1. Implement CameraX still capture.
2. Populate `SatiationViewModel.capturedImage`.
3. Make `NutritionDetailScreen` consume the real captured image path only.
4. Harden Gemini parsing and output structure.
5. Add review/confirm-save behavior.
6. Persist accepted AI meals into Room.
7. Run a full camera/Gemini smoke test.
