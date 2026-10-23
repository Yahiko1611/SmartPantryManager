# Smart Pantry Manager

A Java Android application for reducing household food waste. Users record ingredients available at home and receive only recipes they can make with every required ingredient in sufficient quantity.

## Current project status

Automated checks were run in this workspace on 2026-10-23 with Gradle 8.7, Temurin JDK 21.0.8, Android SDK Platform 35 and Build Tools 34.0.0. `python docs\validate_project.py` passed; `.\gradlew.bat testDebugUnitTest assembleDebug` passed with all 14 strict-matching unit tests passing and produced the debug APK; `.\gradlew.bat lintDebug` completed with 98 warnings. On the `Medium_Phone_API_37.0` emulator, fresh database creation and seed counts (20 recipes, 84 ingredient rows), add/edit/delete and persistence, form checks, Settings/expiry alerts, and strict-match inclusion/exclusion/restoration were observed. These checks are partial; see `docs/TESTING_CHECKLIST.md` and `docs/STATUS_BEFORE_SUBMISSION.md` for exact results and remaining work. This is not yet a verified final submission.

The Android SDK location is machine-specific. Keep it in the ignored `local.properties` file (or let Android Studio create that file); never commit that path.

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

See [`docs/TESTING_CHECKLIST.md`](docs/TESTING_CHECKLIST.md), [`docs/SUBMISSION_CHECKLIST.md`](docs/SUBMISSION_CHECKLIST.md), and [`docs/DEMO_SCRIPT.md`](docs/DEMO_SCRIPT.md). Mark manual results only after they are actually observed and record the real device/emulator and API version. Screenshots for the report must come from the running app, not mockups or placeholders.

The audit's direct API 37 emulator captures are in [`docs/evidence/`](docs/evidence/). To regenerate the editable report draft on Windows, install its Python dependency with `python -m pip install -r docs\report-requirements.txt`, then run `python docs\generate_report.py`. The generator writes `SmartPantryManager_Report_Draft.docx` at the project root and embeds the available evidence images. It is a draft: enter your own details, sign the declaration yourself, check citations/page numbers, and write your own truthful reflection before submission.

## GitHub and academic integrity

The brief calls for a public repository (or adding the marker as a collaborator if the lecturer permits a private repository) and at least 10 meaningful, genuine incremental commits with descriptive messages. The prepared source was not developed under GitHub from the start, so importing it in one commit cannot honestly prove the earlier development timeline. Do not backdate commits or create empty/fake commits. Commit real changes and tests as you inspect, fix and verify the project. The student must understand and be able to explain the code.

## Final Moodle package

The final submission is one ZIP, at most 50 MB, containing the complete Android project source (not generated `build/` or `.gradle/` caches), final report, and narrated 5–7 minute MP4. Follow [`docs/ASSIGNMENT_REQUIREMENTS.md`](docs/ASSIGNMENT_REQUIREMENTS.md) and the official PDF for file naming and evidence requirements. **Do not submit this starter project as final before the app, report and video are verified.**
