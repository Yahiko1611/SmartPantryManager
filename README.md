# Smart Pantry Manager

Smart Pantry Manager is a Java Android app for tracking pantry ingredients and suggesting recipes that can be prepared using the ingredients already available.

## Database

The app uses SQLite through Android's `SQLiteOpenHelper` to keep pantry and recipe data on the device. SQLite provides persistent, offline storage without requiring a separate server.

## Setup and run

1. Install Android Studio and open this project folder.
2. Set the Gradle JDK to JDK 17 in Android Studio's Gradle settings.
3. In SDK Manager, install Android SDK Platform 35.
4. Allow Gradle sync to finish and resolve any requested dependencies.
5. Start an emulator or connect an Android device, select the `app` run configuration, and click **Run**.

## Run unit tests

From the project root, run:

```powershell
.\gradlew.bat testDebugUnitTest
```

On macOS or Linux, run:

```bash
./gradlew testDebugUnitTest
```

## Strict recipe matching

A recipe is suggested only when every required ingredient is present in the pantry in at least the required quantity. Recipes with a missing or insufficient ingredient are excluded. Ingredient names support basic singular/plural normalization, and quantities are converted only between compatible unit families; incompatible units are not guessed.
