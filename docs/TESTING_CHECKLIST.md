# Smart Pantry Manager — manual test checklist

Run these tests in Android Studio on an emulator or physical Android device. Mark each status only after seeing the result. Add the device/API version and any defect or fix you observe.

## Automated checks already run

On 2026-10-23, `python docs\validate_project.py` exited 0 (21 XML files parsed, resource references checked, 20 recipe seeds found, Java-only source check and pure-Java normalizer smoke tests passed). `.\gradlew.bat testDebugUnitTest assembleDebug` exited 0; the JUnit report contains 16 tests, 0 failures, 0 errors and 0 skipped. `.\gradlew.bat lintDebug` exited 0, with 98 lint warnings.

Manual observations were made on the `Medium_Phone_API_37.0` emulator. Fresh app data produced an empty pantry and a SQLite database containing 20 recipes, 84 recipe-ingredient rows, nonblank instructions for all 20 recipes, and no duplicate recipe names. A second app launch left those counts unchanged. The current emulator database contains test pantry rows for Tomato Egg Scramble, including two separate one-egg rows; clear app data before capturing a clean empty-pantry screenshot. The results below distinguish full observations from partial/unit-only checks; unobserved behavior remains **Not yet run**.

| ID | Scenario | Expected result | Actual result |
|---|---|---|---|
| T01 | Launch after fresh install | Pantry is empty; app opens without a crash | After `pm clear`, the app opened without a crash and showed `0 ingredients saved`; fresh SQLite contained 20 recipes and 84 ingredient rows |
| T02 | Open Suggested Recipes with empty pantry | Friendly no-match message; no recipe is suggested | Friendly no-match state observed with an empty pantry |
| T03 | Add an ingredient with a valid name and positive quantity | Save returns to pantry list and the item appears | Added tomato, onion, salt and egg rows; each saved item appeared in the pantry. The separate `0.001 kg` display check also showed `0.001 kg` rather than `0 kg` |
| T04 | Try saving with a blank name | Form validation blocks save and explains the field | Tapped Save with a blank name; form stayed open and focus returned to the name field |
| T05 | Try quantity 0, a negative number or non-numeric input | Form validation blocks save | Quantities `0` and `-1` were rejected and the form stayed open. The signed numeric keyboard exposed a minus key. Non-numeric UI entry remains unverified because the keyboard accepts numeric input only |
| T06 | Enter malformed expiry date (for example `2026-15-40`) | Form blocks save and asks for `YYYY-MM-DD` | Entered `2026-15-40`; Save was blocked and the form remained open |
| T07 | Edit an existing item name, quantity, unit or expiry date | List shows the updated values; no duplicate item is created | Edited tomato quantity from `120 g` to `100 g`, then `120 g` and `150 g`; each saved value affected matching as expected. The edit did not create an extra tomato row |
| T08 | Delete an item and confirm | Item disappears from the pantry list | Deleted required salt; it disappeared and Tomato Egg Scramble was excluded |
| T09 | Delete an item and cancel | Item remains in the pantry list | Cancelled deletion of `safetycheck`; its row remained |
| T10 | Close and reopen the app after adding items | Pantry data is still present | Five pantry rows remained after force-stop/relaunch; SQLite recipe and ingredient seed counts remained 20 and 84 |
| T11 | Add every required ingredient and sufficient quantity for one recipe | Recipe appears in Suggested Recipes | Tomato Egg Scramble appeared with tomato `120 g`, onion `30 g`, salt `1 g`, and two separate egg rows of `1 pcs` each |
| T12 | Remove one required ingredient from that recipe | Recipe disappears from Suggested Recipes | Deleting salt produced the zero-match state; restoring salt made Tomato Egg Scramble reappear |
| T13 | Keep all ingredients but reduce one required amount below its recipe amount | Recipe disappears from Suggested Recipes | Reducing tomato to `100 g` removed the recipe; restoring `120 g` made it reappear |
| T14 | Add more than the recipe's required amount | Recipe is still suggested | Tomato at `150 g` still produced the Tomato Egg Scramble suggestion |
| T15 | Use a plural name such as `tomatoes` for recipe ingredient `tomato` | Name normalisation permits the correct ingredient match | Renamed the pantry tomato row to `tomatoes`; Tomato Egg Scramble remained suggested. Restored the singular name after testing |
| T16 | Enter mass as kg when recipe needs g, or tbsp when recipe needs ml | Compatible unit conversion allows matching only if converted amount is sufficient | With the required tomato stock at `120 g`, editing it to `0.12 kg` kept Tomato Egg Scramble suggested. Reducing it to `0.1 kg` removed the recipe. Changing the amount to `120 ml` also removed it, confirming no mass-to-volume conversion. Restored the row to `tomato`, `120 g`; the recipe appeared again |
| T17 | Select a suggested recipe | Detail screen shows every required ingredient, quantity and method | Tomato Egg Scramble detail showed all four requirements and the preparation method |
| T18 | Change default unit in Settings, then add an item | Selected default is preselected; user can still change it | Set default to `kg`; it remained selected on reopening Settings and was preselected in Add Ingredient |
| T19 | Enable/disable expiry alerts and create an item expiring within three days | Pantry warning appears only when alerts are enabled | With a test item expiring `2026-10-24`, the banner appeared when alerts were on, disappeared when off, and returned when re-enabled; the off preference persisted after reopening Settings |
| T20 | Navigate using Pantry, Recipes and Settings buttons | Each navigation destination opens correctly | Pantry, Recipes, Settings and recipe detail navigation observed |
| T21 | Rotate screen or background and return (if using device/emulator rotation) | No crash; saved database values remain intact | On the API 37 emulator, the display rotated to landscape (`rotation=1`) with `MainActivity` still resumed. After returning to portrait (`rotation=0`), the pantry still showed 7 saved ingredients; no crash observed |

## Critical strict-matching demonstration

Use a recipe with several ingredients (for example, Garlic Tomato Pasta). First add the precise required quantities of every recipe ingredient. Verify it appears. Then remove just the garlic or reduce another single ingredient below the required amount. Return to Suggested Recipes and verify the recipe is absent. Finally, restore the missing/insufficient ingredient and verify it reappears. Capture actual screenshots for the report and perform the same sequence during the narrated video.
