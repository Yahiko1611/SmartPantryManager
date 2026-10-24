package za.co.smartpantry.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import za.co.smartpantry.R;
import za.co.smartpantry.data.DatabaseHelper;
import za.co.smartpantry.model.PantryItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements PantryAdapter.Listener {
    private DatabaseHelper database;
    private PantryAdapter adapter;
    private RecyclerView recycler;
    private View emptyState;
    private TextView itemCount, expiryBanner;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Offload database operations off the main thread to prevent UI ANR

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new DatabaseHelper(this);
        NavigationHelper.bind(this, "pantry");
        recycler = findViewById(R.id.recycler_pantry);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(this);
        recycler.setAdapter(adapter);
        emptyState = findViewById(R.id.empty_pantry);
        itemCount = findViewById(R.id.txt_item_count);
        expiryBanner = findViewById(R.id.txt_expiry_banner);
        Button add = findViewById(R.id.btn_add_ingredient);
        add.setOnClickListener(v -> startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }

    @Override protected void onResume() {
        super.onResume();
        if (database != null) refreshPantry();
    }

    private void refreshPantry() {
        executorService.execute(() -> {
            List<PantryItem> items = database.getAllPantryItems();
            SharedPreferences preferences = getSharedPreferences("pantry_settings", MODE_PRIVATE);
            boolean alertsEnabled = preferences.getBoolean("expiry_alerts", true);
            List<PantryItem> expiring = alertsEnabled ? database.getItemsExpiringWithinDays(3) : java.util.Collections.emptyList();
            
            if (isFinishing() || isDestroyed()) return;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) return;
                adapter.setItems(items);
                itemCount.setText(items.size() + (items.size() == 1 ? " ingredient saved" : " ingredients saved"));
                boolean isEmpty = items.isEmpty();
                emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                recycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

                if (expiring.isEmpty()) {
                    expiryBanner.setVisibility(View.GONE);
                } else {
                    StringBuilder names = new StringBuilder();
                    for (PantryItem item : expiring) {
                        if (names.length() > 0) names.append(", ");
                        names.append(item.getName());
                    }
                    expiryBanner.setText("Use soon: " + names + " " + (expiring.size() == 1 ? "expires" : "expire") + " within 3 days.");
                    expiryBanner.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    @Override public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient?")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            database.deletePantryItem(item.getId());
                            if (isFinishing() || isDestroyed()) return;
                            runOnUiThread(() -> {
                                if (isFinishing() || isDestroyed()) return;
                                refreshPantry();
                                Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                            });
                        } catch (RuntimeException exception) {
                            if (isFinishing() || isDestroyed()) return;
                            runOnUiThread(() -> {
                                if (isFinishing() || isDestroyed()) return;
                                Toast.makeText(this, "Could not delete ingredient", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }).show();
    }

    @Override protected void onDestroy() {
        // Ensure database closes after pending background writes finish
        executorService.execute(database::close);
        executorService.shutdown();
        super.onDestroy();
    }
}
