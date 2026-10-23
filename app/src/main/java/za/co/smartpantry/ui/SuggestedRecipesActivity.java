package za.co.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import za.co.smartpantry.R;
import za.co.smartpantry.data.DatabaseHelper;
import za.co.smartpantry.model.Recipe;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {
    private DatabaseHelper database;
    private RecipeAdapter adapter;
    private RecyclerView recycler;
    private View emptyState;
    private TextView count;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        database = new DatabaseHelper(this);
        NavigationHelper.bind(this, "recipes");
        recycler = findViewById(R.id.recycler_recipes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this);
        recycler.setAdapter(adapter);
        emptyState = findViewById(R.id.empty_recipes);
        count = findViewById(R.id.txt_recipe_count);
    }

    @Override protected void onResume() {
        super.onResume();
        refreshRecipes();
    }

    private void refreshRecipes() {
        List<Recipe> recipes = database.getSuggestedRecipes();
        adapter.setRecipes(recipes);
        count.setText(recipes.size() + (recipes.size() == 1 ? " recipe you can make" : " recipes you can make"));
        boolean isEmpty = recipes.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override public void onRecipeSelected(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }

    @Override protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
