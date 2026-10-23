# GitHub Copilot coding-agent task — Smart Pantry Manager

**Issue title:** Audit, fix, and validate Smart Pantry Manager against the Mobile App Development 700 assignment

You are working on the **existing** Java Android app in this repository. The official assignment is `docs/ASSIGNMENT_BRIEF.pdf`; its searchable requirements and rubric are in `docs/ASSIGNMENT_REQUIREMENTS.md`. Read those, root `AGENTS.md`, `README.md`, and all `docs/` files before editing. If requirements conflict, follow the official PDF. Work in this codebase; do not rebuild the app from scratch or migrate frameworks.

## Deadline-aware priority

Prioritize in this order: (1) make the existing project sync/build; (2) fix compile/runtime blockers and essential assignment features; (3) verify strict matching and persistence; (4) update docs/checklists truthfully; (5) only then consider cosmetic improvements. Do not spend time on optional redesigns while correctness is at risk.

## Phase 1 — inspect and report a baseline

Inspect Java, XML/resources, manifest, Gradle files/wrapper, unit tests, database schema/seed data, navigation, and documentation. Confirm the repository root and included Gradle wrapper are valid. Run the available static validator and Gradle tests/build **before** changing code if possible. Create a concise requirements matrix showing what passed, failed, was code-reviewed only, or cannot be tested in your environment. Do not stop after a plan: proceed with fixes.

## Phase 2 — implement/fix

1. Keep a native Android app written in Java with XML layouts; do not use Kotlin, Flutter, React Native, a web app, or a backend rewrite.
2. Preserve local SQLite via `SQLiteOpenHelper` and `SharedPreferences` for settings.
3. Ensure all five screens work: pantry list, add/edit ingredient, suggested recipes, recipe detail, settings.
4. Ensure full pantry CRUD and persistence after closing/reopening the app.
5. Check that first-run seeding creates at least 20 valid recipes and their ingredient rows once, without reseeding duplicates on later launches. Each recipe must have ingredient names, quantities/units, and preparation steps.
6. Strict matching is the highest priority: every required ingredient must be present with at least the required quantity. A missing ingredient or insufficient amount excludes the recipe. Do not place partial matches in the strict list. Keep simple singular/plural normalization and safe compatible conversions (e.g., g/kg, ml/L). Never infer mass-to-volume or piece-to-weight conversions.
7. Ensure database-backed dynamic RecyclerView/ListView screens use custom adapters.
8. Check Intents pass valid IDs, navigation controls work, back/empty states do not crash, and no missing-record edit flow crashes.
9. Validate blank ingredient names, zero/negative/non-numeric quantity, and malformed expiry dates with user-friendly feedback.
10. Verify the zero-match state, settings persistence, and expiry alert preference behavior.
11. Add/fix automated tests for the strict matcher, using existing dependencies unless an additional dependency is essential.
12. Correct documentation that is stale or contradicted by the source. Avoid needless dependencies, broad rewrites, and cosmetic risk.
13. Do not add any Maps/GPS/location, payment processing, or Play Store features.

## Phase 3 — validate with real commands/results

Run, as supported by the environment:

```bash
python docs/validate_project.py
./gradlew testDebugUnitTest assembleDebug
```

On Windows the equivalent is `python docs/validate_project.py` and `gradlew.bat testDebugUnitTest assembleDebug`. Run lint if feasible. Inspect test reports and the actual build result. Record the exact commands, tool versions if available, and actual pass/fail output. If the Android SDK, Gradle/network downloads, or emulator are unavailable, state that clearly and provide exact local steps. Never claim an APK/build/device run or UI inspection unless that actually occurred.

Check or test at least these scenarios:
- empty pantry means no suggestions and a friendly message;
- all recipe ingredients in sufficient quantity means a suggestion;
- one missing ingredient makes it disappear;
- one insufficient quantity makes it disappear;
- extra quantity still matches;
- singular/plural variation matches when intended;
- compatible units convert accurately;
- incompatible units do not match;
- duplicate pantry rows do not create incorrect results;
- add/edit/delete work and survive app relaunch;
- first-run recipe seeding occurs once;
- the five screens and navigation work.

## Phase 4 — update docs without faking evidence

Update `README.md`, `docs/TESTING_CHECKLIST.md`, `docs/STATUS_BEFORE_SUBMISSION.md`, and any related docs/code references to accurately match the final source. Keep manual tests marked “Not yet run” unless there is direct execution evidence. The repository has report-generation material but there is no verified evidence set yet.

**Never invent** real emulator/device screenshots, a narrated MP4, student name/number, signed declaration, personal reflection/challenges, GitHub history, test outputs, or commit dates. Do not replace genuine screenshot evidence with mockups. You may create a checklist of screenshots the student needs to capture. The student must add personal data, sign the originality declaration, truthfully describe challenges, capture actual screenshots, record the narration/video, and verify the final Moodle ZIP.

## Git / commit rules

Make changes in reviewable logical units when the tool workflow supports it. Never backdate or fabricate commits, create empty commits merely to reach 10, or imply that the prepared code was historically committed when it was not. The student's assignment requires at least 10 genuine incremental commits, but the imported starter's earlier history cannot be recreated honestly. Explain this limitation and suggest real, meaningful work increments only as they actually occur. Do not merge changes into the default branch unless the workflow requests it; produce a reviewable branch/PR and a clear diff summary.

## Final response must contain

1. Summary of actual source changes.
2. Actual build/test commands run and pass/fail results (separate pure Java/static checks from Android Gradle build and emulator/device checks).
3. Requirement matrix: verified by execution / code-reviewed only / not verified.
4. Remaining bugs and environment blockers.
5. Exact student steps for Android Studio tests, genuine screenshots, report/video, GitHub history and Moodle ZIP.
6. Any APK/report artifacts, only if truly generated, including real paths.

Do not stop after planning. Inspect, edit, test as much as this environment supports, and be explicit about what remains unverified.
