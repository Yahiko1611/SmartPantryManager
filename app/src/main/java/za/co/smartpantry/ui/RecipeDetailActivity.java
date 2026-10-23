package za.co.smartpantry.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import za.co.smartpantry.R;
import za.co.smartpantry.data.DatabaseHelper;
import za.co.smartpantry.model.Recipe;
import za.co.smartpantry.model.RecipeIngredient;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_ID = "recipe_id";
    private DatabaseHelper database;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        database = new DatabaseHelper(this);
        NavigationHelper.bind(this, "");
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1L);
        Recipe recipe = database.getRecipe(recipeId);
        if (recipe == null) {
            Toast.makeText(this, "Recipe could not be found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        ((TextView)findViewById(R.id.txt_detail_recipe_name)).setText(recipe.getName());
        ((TextView)findViewById(R.id.txt_detail_recipe_description)).setText(recipe.getDescription());
        ((TextView)findViewById(R.id.txt_detail_instructions)).setText(recipe.getInstructions());
        ((TextView)findViewById(R.id.txt_detail_ingredients)).setText(formatIngredients(database.getIngredientsForRecipe(recipeId)));
    }

    private String formatIngredients(List<RecipeIngredient> ingredients) {
        StringBuilder output = new StringBuilder();
        for (RecipeIngredient ingredient : ingredients) {
            if (output.length() > 0) output.append('\n');
            output.append("•  ").append(PantryAdapter.formatQuantity(ingredient.getQuantity())).append(' ')
                    .append(ingredient.getUnit()).append("  ").append(ingredient.getName());
        }
        return output.toString();
    }

    @Override protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
