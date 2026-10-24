# Narrated video demonstration guide

Target a single 5–7 minute MP4. The assignment requires your voice throughout; your face does not need to appear. This is a timed outline and sample wording, not evidence that any step has been recorded or passed. Use your own words and describe only what you actually observe.

## Before recording

- Complete the remaining manual checks in `docs/TESTING_CHECKLIST.md`.
- Use a clean emulator/device state for the demo. The existing API 37 emulator database contains manual test rows; clear app data and relaunch to start with an empty pantry and freshly seeded recipes.
- Prepare every required ingredient for the selected recipe, then test the match, remove or reduce one requirement, and restore it. Do not assume that restoring it works—verify it before recording.
- Have the real repository open. The assignment asks for at least 10 genuine incremental commits. Do not present checkpoint commits, empty commits, fabricated dates, or a single import as that development history. If the authentic history and accessible GitHub repository are not ready, finish them before recording the GitHub segment.
- Open the relevant Java files in an editor and increase the font size so code is legible in the recording.
- Record at a readable resolution, with microphone levels checked. Export as MP4 (H.264 if available), then play the exported file and verify its duration and audio.

## 0:00–0:20 — Introduction

**Show:** App title screen or pantry screen.

**Suggested narration:** “This is Smart Pantry Manager, a Java Android app for tracking ingredients at home and suggesting recipes that can be made from the available stock. I’ll show the repository, pantry operations, strict recipe matching, and how the main implementation works.”

## 0:20–1:10 — Real GitHub repository and history

**Show:** The actual repository page, README, and commit list. Briefly show descriptive incremental commits and the repository visibility/accessibility.

**Suggested narration:** “This is the project’s GitHub repository. The README explains how to set up and run the app. These commits show the real development changes made during this work. I have not backdated commits or created empty commits.”

Only use that wording if it accurately describes the repository when you record. Do not claim 10 qualifying commits unless at least 10 genuine incremental commits are visible.

## 1:10–2:40 — Pantry and persistence

**Show:** Pantry list; add a valid ingredient with quantity and unit; edit it; delete a separate test item with confirmation; force-stop and reopen the app to show data remains. Briefly show Settings only if its behavior has been checked.

**Suggested narration:** “The pantry is backed by the local database. I’m adding this ingredient with its quantity and unit. The list updates after saving. I’ll edit this item and then remove a separate test item. After closing and reopening the app, the remaining pantry data is still present.”

Perform each action on camera and narrate what the screen actually shows. Do not claim the validation, expiry, or Settings scenarios passed unless you tested them.

## 2:40–3:40 — Strict recipe matching

**Show:** A selected seeded recipe appearing with all requirements satisfied. Remove one required ingredient or lower its quantity below the requirement, refresh Suggested Recipes, then restore it and verify the recipe returns. Open its detail page.

**Suggested narration:** “This recipe is suggested while the pantry contains every required ingredient in sufficient quantity. I’m now removing one required ingredient [or lowering its amount]. The recipe no longer appears, because partial matches are excluded. After restoring enough stock, I’ve verified that it appears again. The detail screen shows the full ingredient list and preparation method.”

Use the bracketed alternative that matches the action performed. If the recipe does not disappear and return as described, stop and investigate before recording the final demo.

## 3:40–5:40 — Explain the implementation with actual source

Keep the code on screen and point to the relevant methods as you explain them. The paths and symbols below are in the current source; navigate to the current lines rather than relying on fixed line numbers.

**3:40–4:20 — SQLite and pantry operations**

**Show:** `app/src/main/java/za/co/smartpantry/data/DatabaseHelper.java`, especially `onCreate()`, the pantry insert/update/delete methods, and recipe seed code.

**Suggested narration:** “`DatabaseHelper` extends `SQLiteOpenHelper`. Its `onCreate()` creates the local tables and seeds the recipe catalogue when the database is first created. Pantry create, read, update, and delete operations use this helper, so pantry records persist locally across app restarts.”

**4:20–4:50 — List and lifecycle**

**Show:** `MainActivity.java`, `PantryAdapter.java`, and `MainActivity.onResume()`.

**Suggested narration:** “`MainActivity` reads pantry records and supplies them to a RecyclerView with the custom `PantryAdapter`. The adapter binds each pantry item to a row and exposes the edit and delete actions. When the activity resumes, `onResume()` refreshes the pantry list.”

**4:50–5:40 — Navigation and strict matching**

**Show:** `MainActivity.onEdit()`, `SuggestedRecipesActivity.onRecipeSelected()`, `DatabaseHelper.getSuggestedRecipes()`, and `RecipeMatcher.canMake()`.

**Suggested narration:** “Navigation uses explicit Intents and passes the pantry item or recipe ID so the destination can load that record. For suggestions, `DatabaseHelper.getSuggestedRecipes()` builds the available pantry stock and checks each recipe with `RecipeMatcher.canMake()`. The matcher rejects a recipe if any required ingredient is missing or short. Ingredient names are normalized, and quantities are converted only within compatible unit families; it does not guess mass-to-volume or piece-to-weight conversions.”

## 5:40–6:10 — Why SQLite

**Show:** App once more, or the database helper source.

**Suggested narration:** “SQLite is suitable here because the pantry is personal, local data that should remain available offline and after restarting the app. It avoids requiring a server or network connection for the core features. Small preferences are stored separately with `SharedPreferences`.”

## 6:10–6:20 — Close

**Show:** App or repository overview.

**Suggested narration:** “That concludes the demonstration of Smart Pantry Manager. The repository, running app, and implementation shown here are the actual project work.”

## Recording evidence checklist

- One continuous narrated MP4, 5–7 minutes, with audible voice throughout.
- Actual accessible repository and genuine history shown; no fabricated commit claims.
- Pantry create/edit/delete and close/reopen persistence demonstrated.
- Strict match shown present, absent after one missing/insufficient requirement, and present again after restoring stock.
- At least three code concepts explained using the current source.
- Exported video replayed to confirm its duration, audio, and readable text.
- MP4 included inside the final single ZIP with the full source and final report; ZIP is at most 50 MB and uses the assignment's required filename.
