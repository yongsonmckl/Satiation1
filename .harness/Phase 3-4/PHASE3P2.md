# Phase 3 Progress Patch 2

Date: 2026-06-18
Device tested: Android emulator `emulator-5556`
Build: `:app:assembleDebug` passed

## Implemented

- Reworked the Progress screen into a weekly calendar-first layout
- Added a `Pick Date` calendar dialog
- Added range presets for `7`, `14`, `30`, and `90` day trailing windows
- Moved charts to the first cards after the Progress header
- Replaced the calorie trend with a custom adaptive bar chart so data shows even on sparse histories
- Added a nutritional split donut chart that updates with the selected range
- Added an all-time Favorite Foods bar chart that does not change with the selected range
- Added a custom weight trend chart for the selected range
- Added `Log New Weight` to the add menu
- Verified `Log New Weight` opens `Change Weight` and returns to the previous Progress context after save
- Added visible marker dots above calendar dates with annotations
- Darkened the green accent in light mode only; dark mode green remains unchanged
- Standardized the passive scrollbar thumb height so it no longer resizes while scrolling

## Issues Found During Testing

1. The first custom calorie chart implementation still looked blank on long ranges.
- Cause:
  - Fixed inter-bar spacing pushed most bars off-screen on 30-day windows.
- Fix:
  - Switched to width-per-slot layout so all bars fit within the chart width.

2. Light mode protein/green accents were too low-contrast.
- Fix:
  - Moved light-mode green usage onto a theme-aware helper that uses the darker light-theme primary while preserving the previous dark-theme green.

3. Add-menu manual tap testing initially hit the wrong item.
- Cause:
  - Manual coordinate guess during emulator testing, not a code defect.
- Verification:
  - Retried using the accessibility tree and confirmed `Log New Weight` works correctly.

## Smoke / Usability Checks Completed

- Home screen loads with real meal and macro data
- Progress screen renders without blank cards
- Calorie bars render with visible data
- Weekly date strip renders and shows marker dots
- Trends & Stats card shows real values
- Nutritional Split chart renders with data
- Favorite Foods chart renders with data
- Weight Trend chart renders with data
- Add menu shows `Log New Weight`
- Weight save returns to the previous Progress location
- Dark mode verified, then restored back to light mode

## Remaining Notes

- The date-range control is currently preset-based (`7/14/30/90` days) and anchored to the selected date, not a freeform custom start/end picker.
- Average calories are computed across the full selected range, so sparse logging can make the value look lower than "average logged day" expectations.
- No blocking crashes or blank-screen regressions were reproduced in this pass.
