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

## VERIFIED in the sandbox (JDK 21, plain javac + JUnit 4.13.2)
- 14/14 unit tests pass: empty pantry, all present, one missing, one insufficient, extra qty, tomato/tomatoes both directions, kg/g, tbsp/tsp to ml, g vs ml NOT converted, pcs vs g NOT converted, duplicate rows summed, name/unit normalisation.
- Schema SQL + seed data executed in SQLite 3.45 (Python): 20 recipes, 84 ingredient rows, units only g/ml/pcs/slices, duplicate recipe name rejected, quantity <= 0 rejected.
- `python3 -I docs/validate_project.py`: XML parses, R.* references resolve, 20 seeds, no Kotlin.

## NOT verified (sandbox could not reach services.gradle.org, dl.google.com, maven.google.com or Maven Central)
- Gradle sync/build, APK, lint, any emulator or device run, any Espresso/instrumented test.
- DatabaseHelper behaviour on Android (CRUD, persistence after relaunch, seeding once), UI appearance, navigation flow, back-stack behaviour.
- Everything in `docs/TESTING_CHECKLIST.md` still reads "Not yet run" and must stay that way until observed.

## Known risks to check first when building
- Spinner dropdown arrow: fixed with bg_spinner.xml (layer-list + vector arrow). Visually UNVERIFIED, check on an emulator.
- No launcher icon (default Android icon is used). Optional polish.
- Each Activity creates its own `DatabaseHelper` and queries run on the UI thread (fine for coursework; mention in the report as a limitation).
- Piece-to-weight is deliberately not converted: a pantry "3 tomatoes" in pcs will NOT satisfy a recipe needing "120 g tomato". Tell the marker/user in the report and video; the Add form already hints to use g/ml. Tomato/onion/carrot are g in seeds; banana/apple/egg are pcs.
- Expired items still count as stock (not specified by the brief).
- `hummus` and similar are stored unchanged; matching is consistent because both sides are normalised.

## Remaining work, in priority order
1. Open in Android Studio, sync, fix any build error, run `StrictMatchingTest` and the app on an emulator (API 23+). Record real results in `docs/TESTING_CHECKLIST.md` (device/API, pass/fail).
2. Optionally add Robolectric or instrumented tests for DatabaseHelper (seed once, CRUD, persistence). Only claim them if actually run.
3. Fix defects found; commit each as a real, separate, descriptively named commit (see `docs/GITHUB_WORKFLOW.md`). Need >= 10 genuine commits on a PUBLIC repo; do not backdate or fabricate.
4. Report (a separate starter report draft, `SmartPantryManager_Report_Draft.docx`, was prepared outside this source repository and must be opened separately; rubric order: cover, TOC, intro, system design with screen flow + ER diagram, real screenshots of every screen/core action with captions, 3-5 code snippets, 2-3 real challenges, conclusion/reflection, references). Times New Roman 12 pt, 1.5 spacing, Harvard, signed declaration. Snippet 4.2 in the draft is now stale: strict matching lives in `RecipeMatcher.canMake()` (called from `DatabaseHelper.getSuggestedRecipes()`). Update line/method references. Remove all "working draft" notices and amber placeholders only after real screenshots exist. Challenges section must describe problems the student actually met.
5. Student only: name, student number, qualification/year/semester, dates, GitHub URL, signature, screenshots, 5-7 minute narrated MP4 (script in `docs/DEMO_SCRIPT.md`; strict-matching reference is now `RecipeMatcher`).
6. Package: `Studentnumber_Surname_MobileAppDev700_Assignment.zip` (<= 50 MB) with full source (no build/ or .gradle/), the report named `Studentnumber_Surname_MobileAppDev700_Assignment.docx`, and the MP4.
