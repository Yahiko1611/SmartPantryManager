# Status and remaining work

Prepared files include a Java Android project source tree, SQLite schema/seeds, activity/layout code, README, test checklist, report draft, screen-flow/data-model diagrams and a narration script.

## Automated verification recorded

Executed on 2026-10-23 with Gradle 8.7, Temurin JDK 21.0.8, Android SDK Platform 35 and Build Tools 34.0.0:

| Check | Result |
|---|---|
| `python docs\validate_project.py` | Exit 0. Parsed 21 XML files, checked resource references in 14 Java files, found 20 recipe seeds, confirmed Java-only source, and passed 5 name/2 unit-conversion smoke checks. |
| `.\gradlew.bat testDebugUnitTest assembleDebug` | Exit 0. Android app and unit tests compiled; 14 strict-matching tests passed (0 failures/errors/skips); debug APK assembled. |
| `.\gradlew.bat lintDebug` | Exit 0. Lint completed and reported 98 warnings, mainly hardcoded strings, possible overdraw, and adapter update efficiency; see `app/build/reports/lint-results-debug.html`. |

The first Gradle invocation had no SDK path and failed with “SDK location not found.” The installed SDK was then configured in the ignored, machine-local `local.properties`. The next build exposed duplicate Kotlin stdlib classes; dependency versions were aligned with the Kotlin 1.8.22 BOM, after which the test/build command passed.

## Emulator observations

On the `Medium_Phone_API_37.0` emulator, the app launched without a crash; the empty-pantry zero-match state, Pantry/Recipes/Settings navigation, recipe detail, add/edit/delete, and pantry persistence after force-stop/relaunch were observed. Tomato Egg Scramble appeared with sufficient stock and disappeared after deleting salt or reducing tomato below the required `0.12 kg`. The final fractional-quantity check saved `fractiontest` at `0.001 kg` and confirmed that the pantry displays `0.001 kg`, not `0 kg`.

These observations were not a fresh-install test or a complete database audit. The emulator's current database contains manual test data, including `fractiontest` and duplicate egg rows. Clear app data before repeating fresh-install tests or capturing clean evidence. See `docs/TESTING_CHECKLIST.md` for each observed and unobserved scenario.

## Not yet verified

Validation of blank/invalid forms, malformed expiry dates, deletion cancellation, settings persistence and alert behavior, expiry warnings, rotation/background behavior, and first-run seed-once behavior on Android remains outstanding. Automated matcher tests cover the strict matching and normalization cases listed in the test report, but do not replace manual UI checks. GitHub hosting/history, real screenshots, personally observed challenges/reflection, originality signature and the required narrated MP4 also remain student-owned work.

The DOCX contains explicit amber screenshot evidence placeholders. Replace them with screenshots from the running app; the assignment prohibits placeholder images or mockups as evidence. Update the report after testing and recheck the contents page numbers.

Do not upload this as a finished Moodle submission yet. Complete the checklist in `SUBMISSION_CHECKLIST.md` and place the full project source, final report and 5–7 minute narrated MP4 inside the one ZIP required by the brief.
