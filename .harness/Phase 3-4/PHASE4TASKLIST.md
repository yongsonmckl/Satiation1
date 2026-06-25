# Phase 4 Task List

This file is the implementation guide and completion record for Phase 4: Gemini food recognition and AI meal logging.

Latest implementation update:
- Date: 2026-06-25
- Phase status: implemented and verified for the current scope
- Preferred model strategy:
  - try `gemini-2.5-pro` first
  - automatically fall back to `gemini-2.5-flash` when the current key cannot access Pro or the Pro tier is unavailable
- Emulator verified on: `emulator-5554`
- Build verified with:
  - `.\gradlew.bat :app:assembleDebug`
  - `.\gradlew.bat :app:assembleAndroidTest`

Phase 4 goal:
- take a real photo from the in-app camera
- allow manual image import for debugging and normal use
- send the captured image to Gemini using the user-stored API key
- support optional user hints before analysis
- show a reviewable nutrition result with totals, item-level breakdown, and confidences
- let the user confirm, adjust, or retake the result
- save the accepted AI meal into Room so the rest of the app updates normally

## Current Baseline

Already present in the app:
- Gemini API key editing in settings
- local persistence of the Gemini API key
- camera entry point in the add menu
- camera preview screen
- CameraX still-image capture
- manual image import entry on the camera screen
- separate add-menu import-photo entry
- Gemini dependency in Gradle
- `NutritionDetailScreen`
- `SatiationViewModel.capturedImage` state holder
- shared captured-image helpers in `SatiationViewModel`
- defensive Gemini parsing helpers in `GeminiNutritionSupport`
- runtime Gemini request client with model fallback and transient retry handling
- inline AI review/edit UI
- transactional AI meal save helper in `SatiationViewModel`
- debug raw-response panel that exposes model output but never secrets

Remaining known limitations:
- the scan UI still contains substantial orchestration logic and can be split further later if Phase 5 needs more AI complexity

## Definition Of Done

Phase 4 is complete when all of the following are true:
- user can grant camera permission and stay in a stable camera flow
- user can take a real photo with CameraX still capture
- user can manually choose an image without using live camera capture
- captured photo is converted into the Gemini request input
- nutrition screen only operates on a real captured image from the current session
- Gemini result includes totals and item-level macro data
- malformed or partial Gemini output does not crash the app
- user can review the AI result before save
- accepted AI result is saved into Room as `MealLog` plus `MealItem`
- saved AI meal appears on Home, Daily Targets, and Progress
- missing key, missing image, API failure, and parsing failure all show user-facing states
- app still builds with `.\gradlew.bat :app:assembleDebug`

## Task Breakdown

### 4.1 Camera capture pipeline

- [x] Wire `ImageCapture` into the camera preview lifecycle.
- [x] Capture a real still image instead of navigating on a fake button press.
- [x] Save the captured image to an app-local temporary file.
- [x] Handle success, failure, and duplicate taps safely.

Implemented:
- still capture is written to app cache as `phase4_scan_input.jpg`
- capture stays disabled until CameraX is ready
- processing overlays prevent duplicate actions

### 4.2 Captured image conversion and shared state

- [x] Use one canonical in-memory image representation for both capture and import.
- [x] Route both capture and import into `SatiationViewModel.capturedImage`.
- [x] Clear stale captured-image state before each scan session.
- [x] Remove old fallback paths that let nutrition open without current image state.
- [x] Keep the current `Bitmap` approach intentionally, since the camera and review flows both need immediate local preview rendering.

Implemented:
- `ScanImageLoader` now owns URI-to-Bitmap decoding
- nutrition reads only the shared ViewModel image state
- the scanned image preview is shown before analysis and during review

### 4.3 Gemini request construction

- [x] Fail early when the API key is blank.
- [x] Fail early when no captured image is present.
- [x] Build requests from the current captured image only.
- [x] Use a strict JSON contract that maps onto the Room meal schema.
- [x] Support an optional user hint before analysis.
- [x] Track which hint and which model were used for each analysis pass.

Implemented:
- prompt construction moved into `GeminiNutritionSupport.buildRequest(...)`
- the request includes totals, item macros, category, confidence, and notes
- optional user hints are included as non-authoritative context only

### 4.4 Response parsing hardening

- [x] Parse Gemini output in a dedicated helper.
- [x] Strip markdown fences and tolerate noisy outer text.
- [x] Validate totals, items, names, and numeric fields.
- [x] Reject negative or non-finite values.
- [x] Preserve enough debug context for development without exposing secrets.

Implemented:
- `GeminiNutritionSupport.parse(...)` produces typed success/failure results
- invalid output becomes explicit UI state
- raw model output is visible in a debug panel on the scan screen, but only the model response is shown

### 4.5 Result model and UI state design

- [x] Define typed AI result and draft models.
- [x] Separate typed results from raw Gemini text.
- [x] Add explicit UI states for idle, loading, missing key, missing image, API failure, invalid output, save in progress, save success, and save failure.
- [x] Keep transient state stable across recomposition.
- [x] Ensure one failed scan does not poison the next scan attempt.

Implemented:
- `NutritionScanUiState` covers the full scan lifecycle
- the screen can reanalyze repeatedly with different hints without leaving stale state behind

### 4.6 Review-before-save experience

- [x] Show the scanned image before save.
- [x] Show top-level totals clearly.
- [x] Show per-item macros and confidences.
- [x] Distinguish low-confidence results and prompt the user to review them.
- [x] Support inline editing before save.
- [x] Add a retake path from the AI review screen.
- [x] Add a manual-entry fallback path from the AI flow.

Implemented:
- users can edit names, categories, calories, protein, carbs, fats, and confidence
- users can add and remove detected items
- totals are recomputed from edited items
- low-confidence items are flagged
- the review screen supports `Retake Photo`, `Use Manual Entry Instead`, and reanalysis with a hint

### 4.7 Save accepted AI results into Room

- [x] Reuse the same persistence core for manual and AI meals where practical.
- [x] Map AI totals into `MealLog`.
- [x] Map AI items into `MealItem`.
- [x] Save AI meals with `sourceType = "ai_scan"`.
- [x] Use save-confirm time as the current Phase 4 logging timestamp.
- [x] Keep saves transactional.
- [x] Clear temporary scan state after a successful save.
- [x] Return the user to the main flow after save.
- [x] Verify saved AI meals propagate through existing Room-backed flows.

Implemented:
- `persistMeal(...)` is now the shared transaction core for insert, update, and AI save cases
- successful AI saves clear the temporary captured image and return the user to Home

### 4.8 Error handling and fallback paths

- [x] Missing API key shows a clear error and links to the key editor.
- [x] Missing image blocks analysis and offers recovery.
- [x] Capture failure surfaces a user-facing retry path.
- [x] API/network/model failures surface a retry path without dropping the image.
- [x] Invalid model output is recoverable.
- [x] Save failure remains on-screen and can be retried.
- [x] Manual entry remains a usable fallback when AI scan is not appropriate.
- [x] Manual image import remains available when live camera capture is inconvenient or fails.

Implemented:
- `GeminiNutritionClient` retries transient 503/high-demand errors
- `GeminiNutritionClient` falls back from Pro to Flash when the key or tier cannot use Pro

### 4.9 Manual image import flow

- [x] Keep a visible import entry on the camera screen.
- [x] Add a separate add-menu import-photo entry for faster debugging and normal use.
- [x] Decode imported images safely.
- [x] Route imports through the same scan path as camera capture.
- [x] Handle cancellation and unreadable image content.

Implemented:
- users can open import directly from the add menu or from the camera sheet
- unreadable-image behavior is covered by instrumentation

### 4.10 Navigation and flow cleanup

- [x] Keep add menu -> camera -> nutrition -> save flow stable.
- [x] Prevent navigation to nutrition before a valid image exists.
- [x] Keep back behavior from nutrition aligned with the camera review flow.
- [x] Ensure a second scan starts cleanly.
- [x] Ensure cancellation does not save unintended data.

Implemented:
- the review screen now explicitly analyzes after user confirmation instead of silently auto-running on arrival
- prompt changes can trigger fresh reanalysis without leaving the screen

### 4.11 Code organization and maintainability

- [x] Identify the heavy AI/camera responsibilities in `CameraScreens.kt`.
- [x] Extract image decoding into `ScanImageLoader`.
- [x] Extract Gemini request execution and model fallback into `GeminiNutritionClient`.
- [x] Keep Room persistence in the ViewModel layer.
- [x] Remove save-logic duplication between manual and AI persistence where practical.
- [x] Keep secrets out of code and logs.
- [x] Avoid adding comments unless the code actually needs them.

## Suggested Implementation Order

- [x] 1. Inspect current camera, nutrition, and ViewModel code paths.
- [x] 2. Define one canonical image-state path for both capture and import.
- [x] 3. Implement real CameraX still capture.
- [x] 4. Implement manual image import.
- [x] 5. Populate and reset `SatiationViewModel.capturedImage` correctly.
- [x] 6. Make nutrition depend on real captured image state only.
- [x] 7. Refine the Gemini prompt and request construction.
- [x] 8. Implement defensive parsing into a typed result model.
- [x] 9. Build explicit loading/error/success UI state.
- [x] 10. Finish review-before-save UI.
- [x] 11. Persist accepted AI results into Room.
- [x] 12. Clean up navigation and stale-state edge cases.
- [x] 13. Run build and end-to-end verification for the implemented scope.

## Verification Checklist

- [x] Run `.\gradlew.bat :app:assembleDebug`.
- [x] Run `.\gradlew.bat :app:assembleAndroidTest`.
- [x] Verify the app still launches.
- [x] Re-verify the denied-camera-permission branch in the same depth as the other 2026-06-25 checks.
- [x] Verify granting permission enables preview and capture.
- [x] Verify `Take Photo` captures a real image.
- [x] Verify manual image import can select a valid image and continue the same scan flow.
- [x] Verify captured image is visible before analysis.
- [x] Verify imported image is visible before analysis.
- [x] Verify blank API key produces the intended error state.
- [x] Verify scan does not proceed without a captured image.
- [x] Verify scan does not proceed with unreadable imported image content.
- [x] Verify Gemini request can succeed with a valid user-provided API key.
- [x] Verify hinted live Gemini analysis works with a valid user-provided API key.
- [x] Re-verify repeated no-hint live Gemini analysis as stable across fresh runs.
- [x] Verify malformed model output does not crash the app.
- [x] Verify success state shows totals, item-level entries, and confidences.
- [x] Verify confirm/save stores one `MealLog` and its `MealItem` rows.
- [x] Verify saved AI meal appears on Home.
- [x] Verify saved AI meal appears on Daily Targets.
- [x] Verify saved AI meal appears on Progress.
- [x] Verify cancel/back does not save unintended data.
- [x] Verify a second scan starts from clean temporary state.

## Optional Add-Ons

- [x] Add a retake action from the nutrition review screen.
- [x] Add an optional user prompt field before sending the image to Gemini.
- [x] Allow manual editing of AI-detected items before save.
- [x] Add confidence-based warnings for low-certainty scans.
- [x] Add a lightweight developer debug panel for raw Gemini response inspection during implementation.

## Verification Notes

Verified on 2026-06-25 with emulator `emulator-5554`:
- app and instrumentation APKs built and installed successfully
- parser and validation instrumentation suite passed:
  - `GeminiNutritionSupportInstrumentedTest`
  - `AiMealPersistenceInstrumentedTest`
  - `ScanImageLoaderInstrumentedTest`
- malformed live model output is now handled inside `GeminiNutritionClient` before the UI sees a scan as successful
- live Gemini hinted verification passed against the provided meal photo after moving the image into app-private storage
- the provided API key could not use `gemini-2.5-pro` on this tier, so the runtime correctly fell back to `gemini-2.5-flash`
- earlier live no-hint result from the same overall implementation pass:
  - model used: `gemini-2.5-flash`
  - detected `Braised Pork Belly`, `Scrambled Eggs`, `Stir-fried Leafy Greens`, `White Rice`, `Iced Tea`, and `Chopped Green Chillies`
  - confidences were populated and ranged from `0.70` to `0.90`
  - this earlier pass over-counted by assuming visible rice under the dishes
- repeated live no-hint reruns after the retry fix both passed:
  - rerun 1 model used: `gemini-2.5-flash`
  - rerun 1 totals were `984.0 / 61.1 / 28.0 / 75.0`
  - rerun 1 detected `Braised Pork Belly`, `Stir-fried Greens`, `Scrambled Eggs with Carrots`, `Chopped Green Chilies`, and `Iced Tea, Lightly Sweetened`
  - rerun 2 model used: `gemini-2.5-flash`
  - rerun 2 totals were `1130.0 / 47.0 / 100.0 / 67.0`
  - rerun 2 detected `Braised Pork Belly`, `Stir-fried Leafy Greens`, `Scrambled Eggs with Carrots`, `White Rice`, and `Sweetened Iced Tea`
- live hinted result summary:
  - model used: `gemini-2.5-flash`
  - hint used during test: `BraisedPorkEggVegetables`
  - detected `Braised Pork Belly`, `Scrambled Eggs with Carrots`, `Stir-fried Leafy Green Vegetables`, and `Sweet Iced Tea`
  - confidences were populated and ranged from `0.70` to `0.90`
  - this pass excluded invisible rice and produced a more believable breakdown for the provided image
- denied-camera-permission branch was re-verified through the emulator UI:
  - camera permission dialog appeared after entering `Scan Food (Camera)` with permission revoked
  - choosing `Don’t allow` returned to the camera screen without crashing
  - the screen showed `Camera permission is unavailable. Import a photo to continue.`
  - the `Take Photo` button was disabled and `Import Photo` remained enabled
- persisted live AI result was saved into Room and surfaced in UI:
  - Home showed `1910 Kcal` total for the current day and listed the saved AI meal entry
  - Daily Targets showed the updated calories and macros plus the saved meal card
  - Progress showed `Braised Pork Belly` as the favorite food, confirming downstream aggregation

## Primary Files Changed

- `app/src/main/java/com/mckl/satiation1/ui/screens/CameraScreens.kt`
- `app/src/main/java/com/mckl/satiation1/ui/screens/DashboardScreens.kt`
- `app/src/main/java/com/mckl/satiation1/navigation/SatiationViewModel.kt`
- `app/src/main/java/com/mckl/satiation1/ai/GeminiNutritionSupport.kt`
- `app/src/main/java/com/mckl/satiation1/ai/GeminiNutritionClient.kt`
- `app/src/main/java/com/mckl/satiation1/ai/ScanImageLoader.kt`
- `app/src/androidTest/java/com/mckl/satiation1/GeminiNutritionSupportInstrumentedTest.kt`
- `app/src/androidTest/java/com/mckl/satiation1/AiMealPersistenceInstrumentedTest.kt`
- `app/src/androidTest/java/com/mckl/satiation1/ScanImageLoaderInstrumentedTest.kt`
- `app/src/androidTest/java/com/mckl/satiation1/GeminiLiveScanInstrumentedTest.kt`

## Risks To Watch

- the provided key tier may continue to reject `gemini-2.5-pro`, so the Flash fallback path is operationally important
- model outputs can still drift, so the review/edit step must remain in the product
- `CameraScreens.kt` still owns a large portion of the feature orchestration and may be worth splitting further if Phase 5 expands AI tooling
