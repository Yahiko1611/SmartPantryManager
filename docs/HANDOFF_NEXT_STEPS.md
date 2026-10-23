# Handoff for the next agent / student: what is done, what is not

Context: Richfield Mobile App Development 700, Smart Pantry Manager (Java, SQLite). Brief and rubric: `ASSIGNMENT_BRIEF.pdf` and searchable summary `ASSIGNMENT_REQUIREMENTS.md` (100 marks: functionality 20, database 15, code quality 25, UI 10, GitHub 10, video 10, report 10).
Non-negotiables: strict matching, SQLite, 5 screens, full CRUD, 20 seeded recipes, Java only, no maps/GPS/payments.
Never fabricate: screenshots, commits or commit dates, test results, personal details, signature, video.

## Changes already made (this session)
1. Added Gradle wrapper (Gradle 8.7, generated with Gradle 8.14.3 tooling; `gradlew` is executable).
2. Extracted the strict-matching rule into pure-Java `util/RecipeMatcher.java`; `DatabaseHelper.getSuggestedRecipes()` now calls it. Behaviour is unchanged. An empty requirement list now never matches.
3. `IngredientNormalizer`: fixed "pies/cookies/brownies/veggies/smoothies" (were mangled to "py/cooky") and stopped mangling "hummus/couscous/asparagus/molasses/watercress".
4. `AddEditIngredientActivity`: edit mode now exits with a toast if the item id no longer exists.
5. Moved `android:windowLightNavigationBar` (API 27) into `values-v27/styles.xml` (minSdk is 23).
6. Added `app/src/test/.../StrictMatchingTest.java` (14 JUnit tests) and `testImplementation 'junit:junit:4.13.2'`.
7. `onUpgrade` now documents the never-drop-tables rule. README setup steps corrected.
8. Added the Kotlin 1.8.22 BOM to align AndroidX's transitive Kotlin runtime and fix duplicate Kotlin stdlib classes during Android builds.

## Verified in this workspace (2026-10-23; JDK 21, Gradle 8.7, SDK Platform 35)
- `.\gradlew.bat testDebugUnitTest assembleDebug` succeeded: 14/14 matcher tests passed, Android app compiled, and debug APK assembled.
- `.\gradlew.bat lintDebug` succeeded with 98 warnings; the report is `app/build/reports/lint-results-debug.html`.
- `python docs\validate_project.py` succeeded: XML parses, resource references resolve, 20 seeds are present, no Kotlin, and normalizer smoke tests pass.
- The SDK is configured in ignored, machine-local `local.properties`; no SDK path is committed.

## Not yet verified
- Negative/non-numeric quantity validation, plural-name matching through the UI, unit conversion through the UI, and a confirmed rotation test.
- No Espresso/instrumented tests have been added or run.

## Emulator state
- Fresh app data on `Medium_Phone_API_37.0` showed an empty pantry. The app-created SQLite database contained 20 recipes, 84 recipe-ingredient rows, instructions for every recipe, and no duplicate recipe names; counts were unchanged after another launch.
- Observed on-device: add/edit/delete/cancel, blank-name/zero-quantity/malformed-expiry validation, settings persistence, expiry alert banner toggle, detail/navigation, strict match include/exclude/restore, duplicate pantry rows summed, extra quantity still matching, and persistence after force-stop/relaunch.
- The fractional display fix was verified on the emulator: `0.001 kg` is displayed without rounding to `0 kg`.
- The emulator currently contains five manually created pantry rows for the Tomato Egg Scramble scenario, including two `egg` rows of `1 pcs` each. Clear app data before capturing a clean empty-pantry screenshot. The scenario-by-scenario record is in `docs/TESTING_CHECKLIST.md`.

## Known risks to check first when building
- Spinner dropdown arrow: fixed with bg_spinner.xml (layer-list + vector arrow). Visually UNVERIFIED, check on an emulator.
- No launcher icon (default Android icon is used). Optional polish.
- Each Activity creates its own `DatabaseHelper` and queries run on the UI thread (fine for coursework; mention in the report as a limitation).
- Piece-to-weight is deliberately not converted: a pantry "3 tomatoes" in pcs will NOT satisfy a recipe needing "120 g tomato". Tell the marker/user in the report and video; the Add form already hints to use g/ml. Tomato/onion/carrot are g in seeds; banana/apple/egg are pcs.
- Expired items still count as stock (not specified by the brief).
- `hummus` and similar are stored unchanged; matching is consistent because both sides are normalised.

## Remaining work, in priority order
1. Finish remaining form edge tests and verify a real rotation if feasible; mark results in `docs/TESTING_CHECKLIST.md`.
2. Review the five new genuine audit commits on GitHub; make further commits only for distinct real work. The public repository currently has fewer than the required 10 incremental commits; do not fabricate the missing history.
3. Complete the separate report draft, `SmartPantryManager_Report_Draft.docx`, with the student's actual identity, signed declaration, real app screenshots and genuine challenges/reflection. Keep the required Times New Roman 12 pt, 1.5 spacing, Harvard references, and update the strict-matching example to `RecipeMatcher.canMake()` if it is stale.
4. The student must record the narrated 5–7 minute MP4 using `docs/DEMO_SCRIPT.md`; the assistant cannot supply the student's voice or personal GitHub-history claims.
5. Package the full project source (excluding generated `build/` and `.gradle/` caches), final report, and MP4 in one ZIP named `Studentnumber_Surname_MobileAppDev700_Assignment.zip` (<= 50 MB).
