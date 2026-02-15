# QuitC
QuitC is an Android app for tracking smoke-free progress day by day.  
It is built with Jetpack Compose and stores data locally on-device (no backend).

## Current App Behavior (Code Analysis)

- Home screen shows a month calendar and supports date-level updates: `CLEAN`, `HEART`, or clear.
- Quick actions for today: `Clean Today` and `Use Token`.
- Streak metrics shown on home: current streak and longest historical streak.
- Monthly metrics shown in stats: success rate, tokens left, fails, and historical summary.
- Stats screen includes a full data reset action
- Daily reminder notification is scheduled with WorkManager
- Progress is persisted locally using DataStore Preferences

## Tech Stack

- Kotlin + Java 17
- Android SDK config: `compileSdk = 34`, `targetSdk = 34`, `minSdk = 26`
- Jetpack Compose + Material3
- Navigation Compose
- DataStore Preferences
- Kotlinx Serialization
- WorkManager (periodic reminder worker)

## Project Notes

- App display name: `QuitC`
- Root Gradle project name: `QuitC`
- Source package names currently use `com.example.quitc`
- Android namespace/applicationId: `com.example.quitc`

## How Data Is Modeled

- `DayStatus` enum: `CLEAN`, `HEART`
- Stored as `Map<LocalDate, DayStatus>` in DataStore (JSON-encoded)
- Tokens left are derived as `3 - tokensUsedInSelectedMonth`
- Success rate counts both `CLEAN` and `HEART` as successful days for the selected month

## Build and Run

### Prerequisites

- Android Studio (latest stable recommended)
- Android SDK 34 installed
- JDK 17

### Commands

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Debug APK output:

- `app/build/outputs/apk/debug/app-debug.apk`

Repository also includes:

- `quitc.apk` (prebuilt APK in project root)

## Permissions and Notifications

- `android.permission.VIBRATE`
- `android.permission.POST_NOTIFICATIONS` (Android 13+ runtime permission)
- Daily reminder is enqueued as unique periodic work (`daily_reminder`) every 24 hours with a 1-hour initial delay

## Known Gaps

- No automated unit/UI tests are present yet
- Token usage is computed reactively but not strictly blocked in all flows (can go below zero with manual date edits)
