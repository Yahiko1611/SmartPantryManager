package za.co.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import za.co.smartpantry.model.PantryItem;
import za.co.smartpantry.model.Recipe;
import za.co.smartpantry.model.RecipeIngredient;
import za.co.smartpantry.util.IngredientNormalizer;
import za.co.smartpantry.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** SQLite persistence for pantry CRUD and the preloaded recipe catalogue. */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), unit TEXT NOT NULL, " +
                "expiry_date TEXT NULL, created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, " +
                "description TEXT NOT NULL, instructions TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, required_quantity REAL NOT NULL CHECK(required_quantity > 0), " +
                "unit TEXT NOT NULL, FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
        db.execSQL("CREATE INDEX idx_recipe_ingredients_recipe ON recipe_ingredients(recipe_id)");
        seedRecipes(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 is the initial schema. NEVER drop tables here: add ALTER TABLE / CREATE TABLE
        // migrations per version step so existing pantry_items rows survive an app update.
    }

    public long insertPantryItem(String name, double quantity, String unit, String expiryDate) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", IngredientNormalizer.canonicalUnit(unit));
        if (expiryDate == null || expiryDate.trim().isEmpty()) values.putNull("expiry_date");
        else values.put("expiry_date", expiryDate.trim());
        return getWritableDatabase().insertOrThrow("pantry_items", null, values);
    }

    public int updatePantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", IngredientNormalizer.canonicalUnit(unit));
        if (expiryDate == null || expiryDate.trim().isEmpty()) values.putNull("expiry_date");
        else values.put("expiry_date", expiryDate.trim());
        return getWritableDatabase().update("pantry_items", values, "id=?", new String[]{String.valueOf(id)});
    }

    public int deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry_items", "id=?", new String[]{String.valueOf(id)});
    }

    public PantryItem getPantryItem(long id) {
        try (Cursor cursor = getReadableDatabase().query("pantry_items",
                new String[]{"id", "name", "quantity", "unit", "expiry_date"}, "id=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor.moveToFirst()) return pantryFromCursor(cursor);
        }
        return null;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("pantry_items",
                new String[]{"id", "name", "quantity", "unit", "expiry_date"}, null, null,
                null, null, "name COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) items.add(pantryFromCursor(cursor));
        }
        return items;
    }

    private PantryItem pantryFromCursor(Cursor cursor) {
        return new PantryItem(cursor.getLong(0), cursor.getString(1), cursor.getDouble(2),
                cursor.getString(3), cursor.isNull(4) ? null : cursor.getString(4));
    }

    public List<Recipe> getAllRecipes() { return queryRecipes(false); }

    public Recipe getRecipe(long id) {
        List<Recipe> result = queryRecipes(true, id);
        return result.isEmpty() ? null : result.get(0);
    }

    private List<Recipe> queryRecipes(boolean one, long... id) {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT r.id, r.name, r.description, r.instructions, " +
                "(SELECT COUNT(*) FROM recipe_ingredients ri WHERE ri.recipe_id=r.id) AS ingredient_count " +
                "FROM recipes r" + (one ? " WHERE r.id=?" : "") + " ORDER BY r.name COLLATE NOCASE";
        try (Cursor cursor = one
                ? getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(id[0])})
                : getReadableDatabase().rawQuery(sql, null)) {
            while (cursor.moveToNext()) recipes.add(new Recipe(cursor.getLong(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getInt(4)));
        }
        return recipes;
    }

    public List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("recipe_ingredients",
                new String[]{"ingredient_name", "required_quantity", "unit"}, "recipe_id=?",
                new String[]{String.valueOf(recipeId)}, null, null, "id ASC")) {
            while (cursor.moveToNext()) ingredients.add(new RecipeIngredient(cursor.getString(0), cursor.getDouble(1), cursor.getString(2)));
        }
        return ingredients;
    }

    /** Returns only recipes for which every ingredient is present in sufficient quantity. */
    public List<Recipe> getSuggestedRecipes() {
        List<RecipeMatcher.Line> pantry = new ArrayList<>();
        for (PantryItem item : getAllPantryItems()) {
            pantry.add(new RecipeMatcher.Line(item.getName(), item.getQuantity(), item.getUnit()));
        }
        Map<String, Double> available = RecipeMatcher.buildAvailable(pantry);

        List<Recipe> suggestions = new ArrayList<>();
        for (Recipe recipe : getAllRecipes()) {
            List<RecipeMatcher.Line> required = new ArrayList<>();
            for (RecipeIngredient ri : getIngredientsForRecipe(recipe.getId())) {
                required.add(new RecipeMatcher.Line(ri.getName(), ri.getQuantity(), ri.getUnit()));
            }
            if (RecipeMatcher.canMake(available, required)) suggestions.add(recipe);
        }
        return suggestions;
    }

    /** Used by the settings banner to show items expiring in the next few days. */
    public List<PantryItem> getItemsExpiringWithinDays(int days) {
        List<PantryItem> soon = new ArrayList<>();
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        format.setLenient(false);
        java.util.Calendar today = java.util.Calendar.getInstance();
        today.set(java.util.Calendar.HOUR_OF_DAY, 0);
        today.set(java.util.Calendar.MINUTE, 0);
        today.set(java.util.Calendar.SECOND, 0);
        today.set(java.util.Calendar.MILLISECOND, 0);
        long start = today.getTimeInMillis();
        today.add(java.util.Calendar.DAY_OF_YEAR, days);
        long end = today.getTimeInMillis();
        for (PantryItem item : getAllPantryItems()) {
            if (item.getExpiryDate() == null || item.getExpiryDate().isEmpty()) continue;
            try {
                java.util.Date expiry = format.parse(item.getExpiryDate());
                if (expiry != null && expiry.getTime() >= start && expiry.getTime() <= end) soon.add(item);
            } catch (java.text.ParseException ignored) {
                // Invalid dates cannot be considered for expiry reminders; form validation prevents new ones.
            }
        }
        return soon;
    }

    private static class SeedIngredient {
        final String name, unit; final double quantity;
        SeedIngredient(String name, double quantity, String unit) { this.name=name; this.quantity=quantity; this.unit=unit; }
    }
    private static class SeedRecipe {
        final String name, description, instructions; final SeedIngredient[] ingredients;
        SeedRecipe(String name, String description, String instructions, SeedIngredient... ingredients) {
            this.name=name; this.description=description; this.instructions=instructions; this.ingredients=ingredients;
        }
    }
    private static SeedIngredient i(String name, double quantity, String unit) { return new SeedIngredient(name, quantity, unit); }

    private void seedRecipes(SQLiteDatabase db) {
        SeedRecipe[] recipes = new SeedRecipe[]{
            new SeedRecipe("Tomato Egg Scramble", "A quick savoury breakfast.", "1. Dice the tomato. 2. Beat the eggs. 3. Warm a pan, cook the tomato briefly, add eggs and stir until set. Season and serve.", i("egg",2,"pcs"),i("tomato",120,"g"),i("onion",30,"g"),i("salt",1,"g")),
            new SeedRecipe("Garlic Tomato Pasta", "A simple tomato and garlic pasta.", "1. Cook the pasta according to its packet. 2. Warm olive oil and gently cook chopped garlic. 3. Add chopped tomato and salt. 4. Toss the drained pasta through the sauce.", i("pasta",150,"g"),i("tomato",150,"g"),i("garlic",5,"g"),i("olive oil",10,"ml"),i("salt",2,"g")),
            new SeedRecipe("Banana Oat Pancakes", "Soft pancakes made with pantry staples.", "1. Mash the banana. 2. Mix with oats, egg and milk. 3. Spoon into a lightly oiled warm pan and cook both sides until golden.", i("banana",1,"pcs"),i("oats",80,"g"),i("egg",1,"pcs"),i("milk",100,"ml")),
            new SeedRecipe("Cheese Omelette", "A soft omelette with melted cheese.", "1. Beat eggs with milk and pepper. 2. Pour into a lightly greased pan. 3. When nearly set, add grated cheese, fold and cook until melted.", i("egg",2,"pcs"),i("cheese",30,"g"),i("milk",15,"ml"),i("black pepper",1,"g")),
            new SeedRecipe("Peanut Butter Banana Toast", "A fast no-fuss snack.", "1. Toast the bread. 2. Spread peanut butter evenly. 3. Slice the banana and arrange it over the toast.", i("bread",2,"slices"),i("peanut butter",30,"g"),i("banana",1,"pcs")),
            new SeedRecipe("Vegetable Fried Rice", "A flexible fried rice with vegetables.", "1. Use cooked rice. 2. Dice the carrot and heat oil in a pan. 3. Cook carrot and peas, then add rice. 4. Stir in beaten egg and soy sauce until the egg is cooked through.", i("rice",180,"g"),i("carrot",60,"g"),i("peas",50,"g"),i("egg",1,"pcs"),i("soy sauce",15,"ml"),i("oil",10,"ml")),
            new SeedRecipe("Chickpea Salad", "A fresh lemon-dressed salad.", "1. Drain and rinse chickpeas. 2. Chop cucumber and tomato. 3. Mix lemon juice with olive oil. 4. Toss everything together and serve.", i("chickpea",150,"g"),i("cucumber",100,"g"),i("tomato",100,"g"),i("lemon juice",15,"ml"),i("olive oil",10,"ml")),
            new SeedRecipe("Crispy Potato Wedges", "Oven-baked potato wedges.", "1. Heat the oven to 220 C. 2. Cut potato into wedges and coat with olive oil, paprika and salt. 3. Bake for 25-35 minutes, turning once, until tender inside and crisp outside.", i("potato",250,"g"),i("olive oil",15,"ml"),i("paprika",2,"g"),i("salt",2,"g")),
            new SeedRecipe("Tuna Mayo Sandwich", "A simple lunch sandwich.", "1. Drain the tuna. 2. Mix tuna with mayonnaise. 3. Add lettuce to the bread, spoon in the tuna mixture and close the sandwich.", i("bread",2,"slices"),i("tuna",100,"g"),i("mayonnaise",20,"g"),i("lettuce",20,"g")),
            new SeedRecipe("Cheese Quesadilla", "A toasted tortilla filled with cheese.", "1. Place grated cheese and chopped onion on one tortilla. 2. Cover with the second tortilla. 3. Toast in a dry pan, flipping once, until golden and the cheese melts. Cut into wedges.", i("tortilla",2,"pcs"),i("cheese",60,"g"),i("onion",20,"g")),
            new SeedRecipe("Spinach Garlic Pasta", "Pasta with greens and garlic.", "1. Cook pasta and reserve a little cooking water. 2. Warm olive oil and briefly cook garlic. 3. Add spinach until wilted. 4. Toss with pasta and parmesan, adding a splash of pasta water if needed.", i("pasta",150,"g"),i("spinach",60,"g"),i("garlic",5,"g"),i("olive oil",10,"ml"),i("parmesan",15,"g")),
            new SeedRecipe("Apple Cinnamon Oats", "Warm oats with apple and cinnamon.", "1. Add oats and milk to a saucepan. 2. Grate or dice the apple and stir it in. 3. Simmer gently until creamy. 4. Sprinkle with cinnamon before serving.", i("oats",60,"g"),i("apple",1,"pcs"),i("milk",150,"ml"),i("cinnamon",2,"g")),
            new SeedRecipe("Yogurt Fruit Bowl", "A chilled fruit and yogurt bowl.", "1. Spoon yogurt into a bowl. 2. Slice the banana and apple. 3. Top the yogurt with fruit and oats.", i("yogurt",150,"g"),i("banana",1,"pcs"),i("apple",1,"pcs"),i("oats",30,"g")),
            new SeedRecipe("Lentil Soup", "A warming lentil and vegetable soup.", "1. Dice carrot and onion. 2. Gently cook them with garlic in a pot. 3. Add lentils and stock. 4. Simmer until lentils are tender, adding water if needed.", i("lentil",100,"g"),i("carrot",60,"g"),i("onion",40,"g"),i("stock",500,"ml"),i("garlic",5,"g")),
            new SeedRecipe("Garlic Mushroom Toast", "Golden mushrooms served on toast.", "1. Slice mushrooms and mince garlic. 2. Melt butter in a pan and cook mushrooms until browned. 3. Add garlic for the final minute. 4. Serve over toasted bread.", i("bread",2,"slices"),i("mushroom",100,"g"),i("butter",10,"g"),i("garlic",3,"g")),
            new SeedRecipe("Simple Tomato Soup", "A smooth tomato soup without added extras.", "1. Chop tomato and onion. 2. Cook onion in olive oil until soft. 3. Add tomato and stock and simmer for 15 minutes. 4. Blend carefully until smooth and season to taste.", i("tomato",300,"g"),i("onion",50,"g"),i("stock",250,"ml"),i("olive oil",10,"ml")),
            new SeedRecipe("Egg Fried Noodles", "Quick noodles with vegetables and egg.", "1. Cook noodles and drain. 2. Stir-fry carrot in oil. 3. Add noodles and soy sauce. 4. Push noodles aside, scramble the eggs in the pan, then mix everything together until cooked.", i("noodle",150,"g"),i("egg",2,"pcs"),i("carrot",50,"g"),i("soy sauce",15,"ml"),i("oil",10,"ml")),
            new SeedRecipe("Buttery Baked Sweet Potato", "A tender baked sweet potato side.", "1. Heat oven to 200 C. 2. Prick the sweet potato and bake until tender, about 40-55 minutes depending on size. 3. Split open and add butter and salt.", i("sweet potato",250,"g"),i("butter",10,"g"),i("salt",2,"g")),
            new SeedRecipe("Hummus Crunch Wrap", "A fresh wrap with hummus and vegetables.", "1. Spread hummus down the centre of the tortilla. 2. Add sliced cucumber and lettuce. 3. Fold the sides in and roll tightly.", i("tortilla",1,"pcs"),i("hummus",60,"g"),i("cucumber",60,"g"),i("lettuce",20,"g")),
            new SeedRecipe("Berry Yogurt Smoothie", "A quick fruit smoothie.", "1. Add frozen berries, yogurt, milk and honey to a blender. 2. Blend until smooth. 3. Add a little more milk only if needed for texture.", i("frozen berries",100,"g"),i("yogurt",150,"g"),i("milk",100,"ml"),i("honey",10,"g"))
        };

        db.beginTransaction();
        try {
            for (SeedRecipe recipe : recipes) {
                ContentValues values = new ContentValues();
                values.put("name", recipe.name);
                values.put("description", recipe.description);
                values.put("instructions", recipe.instructions);
                long recipeId = db.insertOrThrow("recipes", null, values);
                for (SeedIngredient ingredient : recipe.ingredients) {
                    ContentValues ingredientValues = new ContentValues();
                    ingredientValues.put("recipe_id", recipeId);
                    ingredientValues.put("ingredient_name", ingredient.name);
                    ingredientValues.put("required_quantity", ingredient.quantity);
                    ingredientValues.put("unit", IngredientNormalizer.canonicalUnit(ingredient.unit));
                    db.insertOrThrow("recipe_ingredients", null, ingredientValues);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
