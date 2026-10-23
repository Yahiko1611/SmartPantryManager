# GitHub workflow — create genuine development evidence

The assessment asks for at least 10 meaningful commits made incrementally during development. Commit history is evidence, so do not rewrite dates, fabricate a past timeline, or create empty commits simply to reach 10. The project bundle itself is a starting point; use the following real work increments while you review, run and improve it.

1. `Create Java Android Studio project and base theme` — initial Android project, manifest and theme.
2. `Add pantry and recipe SQLite schema` — tables and database helper.
3. `Seed initial recipe catalogue` — recipe seeds and ingredient rows.
4. `Implement pantry item models and CRUD methods` — insert, read, update and delete.
5. `Build pantry list with custom RecyclerView adapter` — list screen and row interactions.
6. `Add ingredient form validation and expiry date input` — create/edit screen.
7. `Implement strict recipe matching and unit normalisation` — matching logic and test fixes.
8. `Add suggested recipes list and detail screen` — recipe navigation and instructions.
9. `Persist settings and add expiring-soon reminder` — SharedPreferences and banner.
10. `Document setup and add manual test checklist` — README, design docs and test instructions.
11. `Fix [specific defect found during a real test]` — replace this with the actual defect and resolution.
12. `Capture verified evidence and finalise report` — after evidence is recorded.

These are suggested descriptions, not proof that the corresponding commits have already occurred. Stage and commit the work as you personally inspect and test each area. Push the commits to a public GitHub repository, verify the URL in a private/incognito browser, and include the URL in the report. If you are importing the prepared project in one go, be transparent about the starting point and create subsequent commits only for genuine changes you make.

## Basic terminal commands

```bash
git init
git add README.md .gitignore settings.gradle build.gradle gradle.properties app
git commit -m "Create Java Android project structure"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/SmartPantryManager.git
git push -u origin main
```

Use the commands only with your real repository URL. The first commit is not a substitute for the required genuine incremental history. If you work from Android Studio's Git interface, the same principle applies: commit actual logical changes with descriptive messages and push regularly.
