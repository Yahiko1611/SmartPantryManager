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
- Fresh-install behavior, full Android SQLite schema/seed-once verification, settings persistence/expiry alerts, validation failures, deletion cancellation, rotation/background handling, and all manual scenarios in `docs/TESTING_CHECKLIST.md`.
- No Espresso/instrumented tests have been added or run.

## Emulator state
- Partial manual checks were performed on `Medium_Phone_API_37.0`: launch, empty/zero-match state, navigation, recipe detail, add/edit/delete, strict match inclusion/exclusion, and pantry persistence after force-stop/relaunch.
- The fractional display fix was verified on the emulator: `0.001 kg` is displayed without rounding to `0 kg`.
- The emulator database currently contains manually created test rows, including `fractiontest` and duplicate egg rows. Clear app data before fresh-install testing or capturing clean evidence. The scenario-by-scenario record is in `docs/TESTING_CHECKLIST.md`.

## Known risks to check first when building
- Spinner dropdown arrow: fixed with bg_spinner.xml (layer-list + vector arrow). Visually UNVERIFIED, check on an emulator.
- No launcher icon (default Android icon is used). Optional polish.
- Each Activity creates its own `DatabaseHelper` and queries run on the UI thread (fine for coursework; mention in the report as a limitation).
- Piece-to-weight is deliberately not converted: a pantry "3 tomatoes" in pcs will NOT satisfy a recipe needing "120 g tomato". Tell the marker/user in the report and video; the Add form already hints to use g/ml. Tomato/onion/carrot are g in seeds; banana/apple/egg are pcs.
- Expired items still count as stock (not specified by the brief).
- `hummus` and similar are stored unchanged; matching is consistent because both sides are normalised.

## Remaining work, in priority order
1. Continue the remaining manual scenarios in `docs/TESTING_CHECKLIST.md` on a clean emulator/device and record only observed results.
2. Optionally add Robolectric or instrumented tests for `DatabaseHelper` (seed once, CRUD, persistence). Only claim them if actually run.
3. Commit real, separate, descriptively named changes (see `docs/GITHUB_WORKFLOW.md`). The assignment calls for >= 10 genuine commits on a public repo; do not backdate or fabricate history.
4. Report (a separate starter report draft, `SmartPantryManager_Report_Draft.docx`, was prepared outside this source repository and must be opened separately; rubric order: cover, TOC, intro, system design with screen flow + ER diagram, real screenshots of every screen/core action with captions, 3-5 code snippets, 2-3 real challenges, conclusion/reflection, references). Times New Roman 12 pt, 1.5 spacing, Harvard, signed declaration. Snippet 4.2 in the draft is now stale: strict matching lives in `RecipeMatcher.canMake()` (called from `DatabaseHelper.getSuggestedRecipes()`). Update line/method references. Remove all "working draft" notices and amber placeholders only after real screenshots exist. Challenges section must describe problems the student actually met.
5. Student only: name, student number, qualification/year/semester, dates, GitHub URL, signature, screenshots, 5-7 minute narrated MP4 (script in `docs/DEMO_SCRIPT.md`; strict-matching reference is now `RecipeMatcher`).
6. Package: `Studentnumber_Surname_MobileAppDev700_Assignment.zip` (<= 50 MB) with full source (no build/ or .gradle/), the report named `Studentnumber_Surname_MobileAppDev700_Assignment.docx`, and the MP4.
