# Status and remaining work

Prepared files include a Java Android project source tree, SQLite schema/seeds, activity/layout code, README, test checklist, report draft, screen-flow/data-model diagrams and a narration script.

## Automated verification recorded

Executed on 2026-10-23 with Gradle 8.7, Temurin JDK 21.0.8, Android SDK Platform 35 and Build Tools 34.0.0:

| Check | Result |
|---|---|
| `python docs\validate_project.py` | Exit 0. Parsed 21 XML files, checked resource references in 14 Java files, found 20 recipe seeds, confirmed Java-only source, and passed 5 name/2 unit-conversion smoke checks. |
| `.\gradlew.bat testDebugUnitTest assembleDebug` | Exit 0. Android app and unit tests compiled; 16 strict-matching tests passed (0 failures/errors/skips); debug APK assembled. |
| `.\gradlew.bat lintDebug` | Exit 0. Lint completed and reported 98 warnings, mainly hardcoded strings, possible overdraw, and adapter update efficiency; see `app/build/reports/lint-results-debug.html`. |

The first Gradle invocation had no SDK path and failed with “SDK location not found.” The installed SDK was then configured in the ignored, machine-local `local.properties`. The next build exposed duplicate Kotlin stdlib classes; dependency versions were aligned with the Kotlin 1.8.22 BOM, after which the test/build command passed.

## Emulator observations

On the `Medium_Phone_API_37.0` emulator, a fresh app-data reset produced an empty pantry and created 20 recipes with 84 recipe-ingredient rows, instructions on all 20 recipes, and no duplicate recipe names. A second launch left those seed counts unchanged. The empty-pantry zero-match state, Pantry/Recipes/Settings navigation, recipe detail, add/edit/delete, and pantry persistence after force-stop/relaunch were observed.

Tomato Egg Scramble appeared with tomato `120 g`, onion `30 g`, salt `1 g`, and two separate egg rows of `1 pcs` each. Deleting salt or reducing tomato to `100 g` removed the recipe; restoring salt or tomato to `120 g` brought it back. Tomato at `150 g` still matched. The form rejected a blank name, quantity `0`, and malformed expiry date `2026-15-40`. A test expiry date showed the reminder banner when alerts were enabled; disabling alerts hid it, and the preference persisted after revisiting Settings. The default unit `kg` persisted and was preselected in Add Ingredient. Cancelling a delete kept the pantry item. The fractional-quantity check also confirmed that `0.001 kg` displays without rounding to zero.

The emulator currently contains seven manual test pantry rows for the recipe demo, including duplicate egg rows and two temporary validation/display test items. See `docs/TESTING_CHECKLIST.md` for every observed and outstanding scenario.

## Not yet verified

Non-numeric quantity entry remains outstanding because the numeric keyboard accepts numeric input only. Zero and negative quantity validation were confirmed: attempts to save `0` and `-1` were rejected and kept the form open. Plural-name matching and unit conversion were also checked through the UI: `tomatoes` matched the recipe ingredient `tomato`; `0.12 kg` satisfied `120 g`, `0.1 kg` was insufficient, and `120 ml` did not satisfy the mass requirement. The original pantry row was restored after testing. Rotation to landscape and return to portrait were confirmed without a crash; pantry data remained visible after restoring portrait. The force-stop/relaunch persistence test also passed. Automated matcher tests cover strict matching, invalid numeric quantities, normalization and compatible/incompatible units alongside these UI observations. The GitHub repository is public and eleven genuine commits are pushed after the prepared starter import; the assignment's commit-count requirement is met, though the student should review and be able to explain each change. Fourteen real API 37 emulator screenshots are stored in `docs/evidence/` and embedded in the generated report draft. The student must review them, supply personal details and a signed declaration, write a truthful reflection/challenges section, and record the narrated MP4.

The generated DOCX draft is `SmartPantryManager_Report_Draft.docx` at the repository root; it is intentionally ignored by Git because it still needs student-owned details and reflection. Regenerate it after installing `docs/report-requirements.txt`, and update the contents page numbers after final edits.

Do not upload this as a finished Moodle submission yet. Complete the checklist in `SUBMISSION_CHECKLIST.md` and place the full project source, final report and 5–7 minute narrated MP4 inside the one ZIP required by the brief.
