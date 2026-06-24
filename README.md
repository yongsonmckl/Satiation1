# Satiation

Satiation is a native Android nutrition and health tracking application built in Kotlin with Jetpack Compose. It was developed as a capstone project to provide a streamlined, mobile-first experience for logging meals, tracking body metrics, monitoring nutrition targets, and reviewing progress through interactive analytics.

## Overview

The application combines day-to-day food logging with longer-term progress tracking in a single Compose-based interface. Users can record meals manually, manage reusable preset foods, update profile and target settings, log weight changes, and review trends across dedicated Progress, Calendar, and Stats views.

## Core Features

- Manual meal logging with support for multiple food items per meal
- Reusable preset food library for faster entry
- Daily calorie and macro tracking
- Editable profile, name, pronouns, height, weight, and nutrition targets
- Weight logging and weight trend visualization
- Dedicated Progress dashboard with Calendar and Stats sections
- Interactive chart views for calories, macro split, favorite foods, and weight trends
- Day-range filtering with quick range shortcuts
- Per-day meal review and meal editing flows
- Local Gemini API key management for AI-assisted nutrition workflows
- Modern Android UI built fully with Jetpack Compose and Material 3

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Android Navigation Compose
- Room Database
- Kotlin Coroutines and Flow
- CameraX
- Google Gemini client SDK

## Architecture

The project follows a straightforward Android architecture centered around a shared `SatiationViewModel`, Room-backed persistence, and Compose-driven screens.

- `MainActivity` configures system UI and hosts the app
- `AppNavigation` defines routes and navigation flow
- `SatiationViewModel` manages shared UI state and data operations
- `AppDatabase` provides Room entities, DAOs, and local persistence
- Compose screens handle onboarding, dashboard flows, progress views, camera flows, and settings

## Project Structure

```text
app/
  src/main/java/com/mckl/satiation1/
    MainActivity.kt
    Constants.kt
    database/
    navigation/
    ui/screens/
```

## Running The Project

### Requirements

- Android Studio
- Android SDK 24+
- Gradle support enabled through Android Studio or the wrapper

### Build

```powershell
.\gradlew.bat :app:assembleDebug
```

### Run

1. Open the project in Android Studio.
2. Sync Gradle dependencies.
3. Launch an emulator or connect an Android device.
4. Run the `app` configuration.

## User Experience Highlights

- A simplified onboarding flow for initial setup
- A dashboard-driven home experience for daily nutrition tracking
- Quick-access meal logging from the main app flow
- Dedicated calendar-based review for historical intake
- Visual statistics screens for trends and behavior analysis
- Settings screens for profile personalization and nutrition target management

## Notes

- All core data is stored locally using Room.
- The app is designed for a native Android workflow rather than a web-backed experience.
- Gemini configuration is handled from within the app settings so API credentials remain device-local.

## License

This project was created as an academic capstone submission.
