# Narrated video demonstration script (target: about 6 minutes)

The assignment requires one narrated screen recording of 5–7 minutes. Speak throughout; your face is not required. Make text readable and export an MP4 using H.264. Keep the final video file inside the same submission ZIP as the project and report.

## 0:00–1:00 — GitHub walkthrough

Open the public repository. Show the README and scroll through the real commit/push history. Explain in your own words how the project evolved through the commits. Do not claim a commit was made earlier than it was.

## 1:00–3:20 — Run the app and demonstrate the core functionality

1. Start the app and show the pantry list.
2. Add an ingredient with quantity, unit and an optional expiry date; show it on the list.
3. Edit its quantity or expiry date and save.
4. Delete a test ingredient and confirm the deletion.
5. Close the app and launch it again to show that saved pantry data persists.
6. Open Settings, briefly show the saved preferences, and return to the pantry.

## 3:20–4:20 — Prove strict matching (the most important business rule)

1. Pick one seeded recipe and add every required ingredient at the listed quantity.
2. Open Suggested Recipes and show that recipe appears.
3. Remove one ingredient or reduce one quantity below the recipe requirement.
4. Return to Suggested Recipes and show that the recipe disappears.
5. Restore the ingredient/quantity and show that the recipe appears again.
6. Open recipe details and show the full ingredient list and preparation steps.

Make sure the test result is real. If the observed outcome differs from the expected result, fix the defect before recording.

## 4:20–5:30 — Explain three concepts using actual source code

Open the relevant Java files and point at the lines while speaking. Choose at least three:

- **SQLite end-to-end:** `DatabaseHelper` creates tables, seeds the recipe catalogue, and provides CRUD queries; changes remain after restart because data is on-device.
- **RecyclerView and Adapter:** `MainActivity` supplies pantry records to `PantryAdapter`; the adapter binds each item's name, quantity, unit and expiry date to the row layout and wires Edit/Delete actions.
- **Intents:** `MainActivity.onEdit()` passes `item_id`; `SuggestedRecipesActivity.onRecipeSelected()` passes `recipe_id`; the destination screen reads the ID and loads the corresponding record.
- **Strict matching:** `RecipeMatcher.canMake()` (called by `DatabaseHelper.getSuggestedRecipes()`) checks each recipe's required ingredients, normalises names, converts compatible units, and rejects a recipe as soon as any requirement is missing or insufficient.
- **Lifecycle:** `onCreate()` sets up the activity, while `onResume()` reloads saved pantry/recipe data when returning from another screen.

## 5:30–6:00 — Justify SQLite

Explain that SQLite is local, works offline, persists across app restarts and is proportionate for this single-device pantry app. It avoids the additional setup and network dependency of a backend for the assignment's core features. Settings use `SharedPreferences` because they are small key-value preferences.

## Before recording

Run the test checklist, use a clean app state, close irrelevant windows, prepare the ingredients for the strict-match demo, ensure voice narration is captured, and check the exported MP4 length and sound before putting it in the ZIP.
