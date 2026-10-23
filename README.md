# Smart Pantry Manager

A Java Android application for reducing household food waste. Users record ingredients available at home and receive only recipes they can make with every required ingredient in sufficient quantity.

## Current project status

This is a prepared starter project, **not yet a verified final submission**. The code includes the five required Activities, SQLite data layer, pantry list/custom adapters, strict-matching utility and unit tests. The Android Gradle build, APK launch, Android database CRUD/persistence, visual UI behavior and emulator/device behavior were not verified in the preparation environment. Run the build and all applicable manual checks before making claims about the app.

## Requirements

Read the official assignment supplied at [`docs/ASSIGNMENT_BRIEF.pdf`](docs/ASSIGNMENT_BRIEF.pdf) and the searchable summary at [`docs/ASSIGNMENT_REQUIREMENTS.md`](docs/ASSIGNMENT_REQUIREMENTS.md). The summary does not replace the official brief.

## Technology decisions

- **Language/UI:** Java + Android XML layouts.
- **Persistence:** Android SQLite through `SQLiteOpenHelper`, stored locally on device.
- **Settings:** `SharedPreferences` for small key-value preferences.
- **Lists:** RecyclerView and custom adapters.
- **Build:** Gradle wrapper is included in this repository (Gradle 8.7; Android Gradle Plugin 8.6.1).

SQLite keeps the core app local and usable offline, avoids requiring a backend/server, and matches the local-persistence approach taught in the module.

## Open and run in Android Studio

1. Install Android Studio from <https://developer.android.com/studio>.
2. During initial setup, install the Android SDK Platform 35 (SDK Manager) and use **JDK 17** for Gradle. Android Studio's bundled JBR is usually appropriate; check **Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK**.
3. Extract the project package. In Android Studio, choose **Open** and select the `SmartPantryManager` project root folder—the one containing `settings.gradle`, `build.gradle`, `gradlew`, `gradlew.bat`, `gradle/`, `app/`, `AGENTS.md`, and `README.md`.
4. Do **not** create a second Empty Activity project, copy these files into a newly generated project, or replace the included Gradle wrapper. The wrapper scripts and wrapper JAR are already supplied.
5. Let Gradle sync and download dependencies. If Android Studio reports a missing SDK Platform 35, install it in SDK Manager and sync again.
6. Create/start an Android emulator (API 23 or later; a recent API is preferable) or connect an Android device with USB debugging enabled. Select the `app` run configuration and press **Run**.
7. On first launch, SQLite creates the tables and seeds the recipe catalogue. The pantry should start empty.

## Run automated checks on Windows

Open PowerShell or a terminal **in this project root** (the folder containing `gradlew.bat`) and run:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

For macOS/Linux, run:

```bash
./gradlew testDebugUnitTest assembleDebug
```

The first run may download Gradle/Android dependencies and needs an internet connection. If it fails, retain the complete error output and resolve the actual cause—do not report the build or tests as passing. A successful unit-test task is not a substitute for a real emulator/device run.

Run the additional source/resource validation check:

```bash
python docs/validate_project.py
```

The validation script is a lightweight static check; it does **not** build the APK or prove UI/database runtime behavior.

## Strict matching

`DatabaseHelper.getSuggestedRecipes()` delegates to `RecipeMatcher.canMake()`. A recipe is included only if all requirements are available in sufficient quantity after safe normalization/conversion. Missing ingredients, insufficient quantities and incompatible units must exclude the recipe. Singular/plural names should match where normalization supports them. Mass-to-volume and piece-to-weight conversions must never be guessed.

## Project structure

```text
SmartPantryManager/
├── AGENTS.md
├── app/
│   └── src/
│       ├── main/java/za/co/smartpantry/
│       │   ├── data/DatabaseHelper.java
│       │   ├── model/
│       │   ├── ui/                 # Five Activities + custom adapters
│       │   └── util/               # IngredientNormalizer, RecipeMatcher
│       ├── main/res/               # XML layouts/resources
│       └── test/java/...           # StrictMatchingTest
├── docs/                           # Brief, requirement summary, checklists, diagrams
├── gradle/wrapper/
├── gradlew
├── gradlew.bat
├── settings.gradle
├── build.gradle
└── README.md
```

## Manual tests and evidence

See [`docs/TESTING_CHECKLIST.md`](docs/TESTING_CHECKLIST.md), [`docs/SUBMISSION_CHECKLIST.md`](docs/SUBMISSION_CHECKLIST.md), and [`docs/DEMO_SCRIPT.md`](docs/DEMO_SCRIPT.md). Every manual result must remain “Not yet run” until actually observed. Record the real device/emulator and API version when testing. Screenshots for the report must come from the running app, not mockups or placeholders.

## GitHub and academic integrity

The brief calls for a public repository (or adding the marker as a collaborator if the lecturer permits a private repository) and at least 10 meaningful, genuine incremental commits with descriptive messages. The prepared source was not developed under GitHub from the start, so importing it in one commit cannot honestly prove the earlier development timeline. Do not backdate commits or create empty/fake commits. Commit real changes and tests as you inspect, fix and verify the project. The student must understand and be able to explain the code.

## Final Moodle package

The final submission is one ZIP, at most 50 MB, containing the complete Android project source (not generated `build/` or `.gradle/` caches), final report, and narrated 5–7 minute MP4. Follow [`docs/ASSIGNMENT_REQUIREMENTS.md`](docs/ASSIGNMENT_REQUIREMENTS.md) and the official PDF for file naming and evidence requirements. **Do not submit this starter project as final before the app, report and video are verified.**
