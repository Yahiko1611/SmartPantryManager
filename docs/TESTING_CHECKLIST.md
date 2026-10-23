# Smart Pantry Manager — manual test checklist

Run these tests in Android Studio on an emulator or physical Android device. Mark each status only after seeing the result. Add the device/API version and any defect or fix you observe.

| ID | Scenario | Expected result | Actual result |
|---|---|---|---|
| T01 | Launch after fresh install | Pantry is empty; app opens without a crash | Not yet run |
| T02 | Open Suggested Recipes with empty pantry | Friendly no-match message; no recipe is suggested | Not yet run |
| T03 | Add an ingredient with a valid name and positive quantity | Save returns to pantry list and the item appears | Not yet run |
| T04 | Try saving with a blank name | Form validation blocks save and explains the field | Not yet run |
| T05 | Try quantity 0, a negative number or non-numeric input | Form validation blocks save | Not yet run |
| T06 | Enter malformed expiry date (for example `2026-15-40`) | Form blocks save and asks for `YYYY-MM-DD` | Not yet run |
| T07 | Edit an existing item name, quantity, unit or expiry date | List shows the updated values; no duplicate item is created | Not yet run |
| T08 | Delete an item and confirm | Item disappears from the pantry list | Not yet run |
| T09 | Delete an item and cancel | Item remains in the pantry list | Not yet run |
| T10 | Close and reopen the app after adding items | Pantry data is still present | Not yet run |
| T11 | Add every required ingredient and sufficient quantity for one recipe | Recipe appears in Suggested Recipes | Not yet run |
| T12 | Remove one required ingredient from that recipe | Recipe disappears from Suggested Recipes | Not yet run |
| T13 | Keep all ingredients but reduce one required amount below its recipe amount | Recipe disappears from Suggested Recipes | Not yet run |
| T14 | Add more than the recipe's required amount | Recipe is still suggested | Not yet run |
| T15 | Use a plural name such as `tomatoes` for recipe ingredient `tomato` | Name normalisation permits the correct ingredient match | Not yet run |
| T16 | Enter mass as kg when recipe needs g, or tbsp when recipe needs ml | Compatible unit conversion allows matching only if converted amount is sufficient | Not yet run |
| T17 | Select a suggested recipe | Detail screen shows every required ingredient, quantity and method | Not yet run |
| T18 | Change default unit in Settings, then add an item | Selected default is preselected; user can still change it | Not yet run |
| T19 | Enable/disable expiry alerts and create an item expiring within three days | Pantry warning appears only when alerts are enabled | Not yet run |
| T20 | Navigate using Pantry, Recipes and Settings buttons | Each navigation destination opens correctly | Not yet run |
| T21 | Rotate screen or background and return (if using device/emulator rotation) | No crash; saved database values remain intact | Not yet run |

## Critical strict-matching demonstration

Use a recipe with several ingredients (for example, Garlic Tomato Pasta). First add the precise required quantities of every recipe ingredient. Verify it appears. Then remove just the garlic or reduce another single ingredient below the required amount. Return to Suggested Recipes and verify the recipe is absent. Finally, restore the missing/insufficient ingredient and verify it reappears. Capture actual screenshots for the report and perform the same sequence during the narrated video.
