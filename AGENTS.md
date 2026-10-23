# Instructions for coding agents — Smart Pantry Manager

## Source of truth
Follow `docs/ASSIGNMENT_BRIEF.pdf` as the official Mobile App Development 700 assignment source and `docs/ASSIGNMENT_REQUIREMENTS.md` as its searchable summary. Read both before changing code; if there is a conflict, the PDF is authoritative. Do not silently invent requirements. This is a Java Android coursework project. Preserve the student's ability to explain and defend the implementation.

## Non-negotiable requirements
- Java only for Android app code; XML layouts are expected. Do not migrate to Kotlin, Flutter, React Native, or a web app.
- App name: Smart Pantry Manager.
- At least five working screens/Activities: Pantry List, Add/Edit Ingredient, Suggested Recipes, Recipe Detail, Settings.
- Use Android SQLite via `SQLiteOpenHelper` for persistent app records; use `SharedPreferences` for settings.
- Full pantry CRUD (create, read/list, update, delete) and data persists after app close/relaunch.
- At least 20 recipes seeded once on first database creation, with ingredients, quantities/units, and preparation steps.
- Strict recipe matching: a recipe is suggested only when EVERY required ingredient is in the pantry in sufficient quantity. Exclude partial matches and insufficient quantities. Handle basic singular/plural normalization. Convert units only within compatible families (e.g. kg/g, L/ml); do not guess mass-to-volume or piece-to-weight conversions.
- Use RecyclerView or ListView with custom Adapter for database-backed dynamic lists.
- Use Intents for navigation/pass IDs; visible working navigation; validate forms and show a helpful zero-match state.
- No maps/GPS/location features, payments, or Play Store publishing features.
- Keep code readable and add comments where they clarify non-obvious logic.

## Agent workflow
1. Inspect the entire project and existing docs before editing. Preserve useful work; do not rewrite everything without reason.
2. Report a short plan and identify likely build/test risks.
3. Fix build, resource, manifest, and runtime issues in small logical changes.
4. Add/maintain unit tests for strict matching, including empty pantry, all ingredients present, one missing ingredient, insufficient quantity, plural names, compatible unit conversions, incompatible units, and duplicate pantry rows.
5. Run every available validation: Gradle build/test, unit tests, XML/resource validation, and static checks. Never say a test passed unless you ran it and can provide the command/output.
6. If network access, Android SDK, emulator, signing, or external services prevent a test, state that clearly and provide exact manual steps. Do not fabricate screenshots, emulator results, UI observations, or device test results.
7. Keep `README.md`, test plan, status report, and report/code references aligned with the final code.
8. Do not fabricate personal details, signature, dates, GitHub history, or retrospective commits. The student will provide identity information and perform genuine review/commit actions.
9. Do not replace actual screenshot evidence with mockups. You may improve the report template and add labeled screenshot placeholders, but placeholders must be replaced by the student after running the app.
10. Avoid dependencies that require credentials or paid services. Do not introduce Firebase or a server; SQLite is the chosen approach.

## Definition of done for code
- Project syncs and builds successfully with documented JDK/Gradle/SDK versions, or blockers are precisely described.
- Unit tests pass and are relevant to the actual matching implementation.
- The five screens, navigation, CRUD, recipe details, settings, first-run seeding, and persistence have been inspected and corrected as far as the available environment allows.
- All 20 seeded recipes have valid ingredient rows and instructions.
- No Kotlin source files; no accidental secrets; `.gitignore` excludes local SDK paths and build outputs.
- README gives accurate setup/run instructions and distinguishes automated checks from manual tests not yet run.
- Final response lists files changed, commands run and results, known limitations, and what the student must still do.
