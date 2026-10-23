package za.co.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import za.co.smartpantry.R;
import za.co.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    public interface Listener { void onRecipeSelected(Recipe recipe); }
    private final List<Recipe> recipes = new ArrayList<>();
    private final Listener listener;

    public RecipeAdapter(Listener listener) { this.listener = listener; }
    public void setRecipes(List<Recipe> values) {
        recipes.clear();
        recipes.addAll(values);
        notifyDataSetChanged();
    }

    @NonNull @Override public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.name.setText(recipe.getName());
        holder.description.setText(recipe.getDescription());
        holder.ingredients.setText(recipe.getIngredientCount() + " required ingredients  ·  Tap to view method");
        holder.itemView.setOnClickListener(v -> listener.onRecipeSelected(recipe));
    }

    @Override public int getItemCount() { return recipes.size(); }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView name, description, ingredients;
        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_recipe_name);
            description = itemView.findViewById(R.id.txt_recipe_description);
            ingredients = itemView.findViewById(R.id.txt_recipe_ingredients);
        }
    }
}
