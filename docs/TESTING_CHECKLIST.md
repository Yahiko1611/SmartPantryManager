# Smart Pantry Manager — manual test checklist

Run these tests in Android Studio on an emulator or physical Android device. Mark each status only after seeing the result. Add the device/API version and any defect or fix you observe.

## Automated checks already run

On 2026-10-23, `python docs\validate_project.py` exited 0 (21 XML files parsed, resource references checked, 20 recipe seeds found, Java-only source check and pure-Java normalizer smoke tests passed). `.\gradlew.bat testDebugUnitTest assembleDebug` exited 0; the JUnit report contains 14 tests, 0 failures, 0 errors and 0 skipped. `.\gradlew.bat lintDebug` exited 0, with 98 lint warnings.

Manual observations were made on the `Medium_Phone_API_37.0` emulator. The app's current test database is not a fresh install and contains manual test data, including a `fractiontest` row and duplicate egg rows. Clear app data before repeating the scenarios or capturing clean evidence. The actual results below distinguish full observations from partial/unit-only checks; unobserved behavior remains **Not yet run**.

| ID | Scenario | Expected result | Actual result |
|---|---|---|---|
| T01 | Launch after fresh install | Pantry is empty; app opens without a crash | App launch was observed without a crash; fresh-install/empty-database behavior not tested |
| T02 | Open Suggested Recipes with empty pantry | Friendly no-match message; no recipe is suggested | Friendly no-match state observed with an empty pantry |
| T03 | Add an ingredient with a valid name and positive quantity | Save returns to pantry list and the item appears | Saved `fractiontest` at `0.001 kg`; row appeared as `0.001 kg` |
| T04 | Try saving with a blank name | Form validation blocks save and explains the field | Not yet run |
| T05 | Try quantity 0, a negative number or non-numeric input | Form validation blocks save | Not yet run |
| T06 | Enter malformed expiry date (for example `2026-15-40`) | Form blocks save and asks for `YYYY-MM-DD` | Not yet run |
| T07 | Edit an existing item name, quantity, unit or expiry date | List shows the updated values; no duplicate item is created | Edited tomato from `0.12 kg` to `0.1 kg`; updated amount was used in recipe matching. Other edit fields not tested |
| T08 | Delete an item and confirm | Item disappears from the pantry list | Deleted a required ingredient; it disappeared and its recipe was no longer suggested |
| T09 | Delete an item and cancel | Item remains in the pantry list | Not yet run |
| T10 | Close and reopen the app after adding items | Pantry data is still present | Pantry rows remained after force-stop and relaunch |
| T11 | Add every required ingredient and sufficient quantity for one recipe | Recipe appears in Suggested Recipes | Tomato Egg Scramble appeared with egg `2 pcs`, tomato `0.12 kg`, onion `0.03 kg` and salt `0.001 kg` |
| T12 | Remove one required ingredient from that recipe | Recipe disappears from Suggested Recipes | Deleting salt removed Tomato Egg Scramble from suggestions |
| T13 | Keep all ingredients but reduce one required amount below its recipe amount | Recipe disappears from Suggested Recipes | Reducing tomato from `0.12 kg` to `0.1 kg` removed the recipe |
| T14 | Add more than the recipe's required amount | Recipe is still suggested | Not yet run |
| T15 | Use a plural name such as `tomatoes` for recipe ingredient `tomato` | Name normalisation permits the correct ingredient match | Plural normalization covered by automated matcher tests; UI scenario not tested |
| T16 | Enter mass as kg when recipe needs g, or tbsp when recipe needs ml | Compatible unit conversion allows matching only if converted amount is sufficient | Compatible/incompatible unit behavior covered by automated matcher tests; UI scenario not tested |
| T17 | Select a suggested recipe | Detail screen shows every required ingredient, quantity and method | Recipe detail observed with required ingredients and preparation method |
| T18 | Change default unit in Settings, then add an item | Selected default is preselected; user can still change it | Settings screen opened; preference persistence and preselection not tested |
| T19 | Enable/disable expiry alerts and create an item expiring within three days | Pantry warning appears only when alerts are enabled | Not yet run |
| T20 | Navigate using Pantry, Recipes and Settings buttons | Each navigation destination opens correctly | Pantry, Recipes, Settings and recipe detail navigation observed |
| T21 | Rotate screen or background and return (if using device/emulator rotation) | No crash; saved database values remain intact | Not yet run |

## Critical strict-matching demonstration

Use a recipe with several ingredients (for example, Garlic Tomato Pasta). First add the precise required quantities of every recipe ingredient. Verify it appears. Then remove just the garlic or reduce another single ingredient below the required amount. Return to Suggested Recipes and verify the recipe is absent. Finally, restore the missing/insufficient ingredient and verify it reappears. Capture actual screenshots for the report and perform the same sequence during the narrated video.
