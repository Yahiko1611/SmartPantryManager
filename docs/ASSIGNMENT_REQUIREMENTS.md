# Official assignment requirements — Smart Pantry Manager

**Course:** Mobile App Development 700 (Richfield Graduate Institute of Technology)  
**Project:** Smart Pantry Manager  
**Source of truth:** `docs/ASSIGNMENT_BRIEF.pdf`, the supplied 8-page practical assignment (the PDF has 9 physical pages including the cover/declaration page).

This file is a searchable summary for the student and coding agent. In any disagreement, consult the official PDF. Do not silently invent extra requirements.

## App requirements

Build a fully functional **native Android application written in Java**, using Android Studio conventions and XML layouts (not Kotlin, Flutter, React Native, or a web app). The app tracks ingredients that a user already has and suggests only recipes that can be prepared from those ingredients, without shopping.

Required screens/activities:
1. Pantry list, showing database-backed pantry ingredients.
2. Add/edit ingredient form.
3. Suggested recipes.
4. Recipe details, including the full required ingredient list and method.
5. Settings/profile screen.

The brief requires at least four distinct screens and explicitly identifies the above screens plus Settings. Required behavior includes:
- Add, view, edit, and delete pantry items (name, quantity, unit, optional expiry date).
- Persist data even after the app is closed and reopened.
- Store at least 15–20 recipes in the database, seeded on first run; use 20 valid recipes as the current project target. Each recipe needs required ingredients, quantities/units, and preparation steps.
- Use RecyclerView or ListView with a custom Adapter for a dynamic database-backed list.
- Use Intents for navigation and pass relevant IDs; provide a working navigation element.
- Validate form inputs and show useful feedback, including a friendly message when no recipes match.
- Keep the layout readable on a mobile screen.

## Core business rule: strict matching

A recipe belongs in **Suggested Recipes** only if **every required ingredient is in the user's pantry in at least the required quantity**. If one ingredient is absent or insufficient, the recipe must not appear. Do not place partial/“almost there” matches in the strict suggestions list. Basic singular/plural normalization and reasonable unit handling are required. Convert only compatible unit families (for example, g/kg and ml/L). Do not infer mass-to-volume conversions or piece-to-weight conversions. Unit conversions must not make an insufficient quantity look sufficient.

## Technology/data restrictions

- Use one approved persistence option. The existing project chooses local SQLite with `SQLiteOpenHelper`; keep that choice unless a genuine blocker means the project requirements cannot be met.
- `SharedPreferences` is appropriate for small settings.
- Do **not** add Google Maps, map SDKs, GPS/location services, nearby grocery store features, payment processing, or Play Store publication features.
- Avoid needless new dependencies and preserve useful work in the starter code.

## GitHub evidence

- Public GitHub repository, unless the lecturer/portal instead requires the marker to be added to a private repository.
- At least **10 genuine incremental commits** with descriptive messages, showing actual development. A single late upload, empty commits, fabricated dates, backdating, and invented history are not acceptable.
- README must include the app name/description, database choice and reason, and run/setup instructions.

The prepared starter has not been developed under GitHub from the start. Importing it now is not equivalent to having the required earlier history. Be honest; make authentic, useful commits while reviewing, fixing, testing, and documenting it, and do not fabricate a timeline.

## Narrated video demonstration: 5–7 minutes

Submit one narrated MP4, with the student's voice throughout (face need not be shown), readable screen recording, preferably at 1080p, H.264 if possible. It must demonstrate:
1. About one minute showing the actual public repo and real GitHub commit/push history.
2. The running app: pantry CRUD, close/reopen persistence, and a strict-matching test where one removed/insufficient ingredient makes a recipe disappear (and restoring it makes it reappear).
3. About 2–3 minutes explaining at least three real code concepts with actual source lines: Activity lifecycle, database end-to-end, Adapter/list binding, Intents/navigation, or strict matching.
4. Brief justification for the chosen persistence option (SQLite).

## Written report

Submit one Word or PDF report; follow the brief's specified cover page and include:
1. Cover page: app name, student's name, student number, module, date, and the required institutional cover/declaration details.
2. Table of contents.
3. Introduction: problem and intended users.
4. System design: screen-flow/wireframe diagram and database/data-model diagram.
5. Genuine screenshots from the **running app** for every screen and core operation (list, add, edit, delete, validation error, etc.), each with a short explanatory caption. Mockups/placeholders are not evidence.
6. Three to five short code snippets with explanation of purpose/design.
7. Two to three actual challenges and how they were solved.
8. Conclusion/reflection: what the student learned and what would be improved.
9. References in Harvard style.

Formatting rules: Times New Roman, 12 pt, 1.5 line spacing throughout; Harvard in-text citations/reference list. Include the signed declaration of originality. Personal details, challenges/reflection, and signature must be truthful and supplied by the student.

## Moodle submission package

Submit **one ZIP only**, named exactly in this pattern:

`Studentnumber_Surname_MobileAppDev700_Assignment.zip`

It must contain:
- The complete Android Studio project source (exclude generated `build/` and `.gradle/` cache folders to reduce size).
- The narrated 5–7 minute MP4 inside the ZIP, not merely a link.
- The final report inside the ZIP, named `Studentnumber_Surname_MobileAppDev700_Assignment.docx` per the brief's specified filename. (The brief elsewhere permits Word or PDF; follow the submission portal/lecturer's required format if clarified.)

The complete ZIP must be **50 MB or smaller**. The report must include a working GitHub repository URL, and the marker must be able to access the repository.

## Marking rubric (100 marks)

| Criterion | Marks |
|---|---:|
| App functionality and requirements | 20 |
| Database implementation and persistence | 15 |
| Code quality and Java fundamentals | 25 |
| UI/UX design | 10 |
| GitHub version control | 10 |
| Video demonstration | 10 |
| Written report | 10 |
| **Total** | **100** |

## Integrity and evidence rules

Do not claim build/tests/UI/device checks passed unless actually run. Do not fabricate screenshots, video, commit dates/history, student information, signature, or personal reflection. The student must understand and be able to explain the submitted code.
