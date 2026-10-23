package za.co.smartpantry.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import za.co.smartpantry.R;

/** Shared navigation behaviour used by all main screens. */
public final class NavigationHelper {
    private NavigationHelper() { }

    public static void bind(Activity activity, String activeScreen) {
        Button pantry = activity.findViewById(R.id.btn_nav_pantry);
        Button recipes = activity.findViewById(R.id.btn_nav_recipes);
        Button settings = activity.findViewById(R.id.btn_nav_settings);
        configure(pantry, activity, activeScreen, "pantry", MainActivity.class);
        configure(recipes, activity, activeScreen, "recipes", SuggestedRecipesActivity.class);
        configure(settings, activity, activeScreen, "settings", SettingsActivity.class);
    }

    private static void configure(Button button, Activity activity, String active, String key, Class<?> target) {
        if (button == null) return;
        boolean selected = key.equals(active);
        button.setBackgroundResource(selected ? R.drawable.bg_soft : R.drawable.bg_nav);
        button.setTextColor(ContextCompat.getColor(activity,
                selected ? R.color.green_dark : R.color.text_secondary));
        button.setOnClickListener(v -> {
            if (!selected) {
                Intent intent = new Intent(activity, target);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                if (!(activity instanceof MainActivity)) {
                    activity.finish();
                }
            }
        });
    }
}
