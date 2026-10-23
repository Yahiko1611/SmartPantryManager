package za.co.smartpantry.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import za.co.smartpantry.R;

public class SettingsActivity extends AppCompatActivity {
    private static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs", "slices", "pinch", "clove"};
    private SwitchCompat expirySwitch;
    private Spinner defaultUnitSpinner;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        NavigationHelper.bind(this, "settings");
        expirySwitch = findViewById(R.id.switch_expiry_alerts);
        defaultUnitSpinner = findViewById(R.id.spinner_default_unit);
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        defaultUnitSpinner.setAdapter(unitAdapter);

        android.content.SharedPreferences preferences = getSharedPreferences("pantry_settings", MODE_PRIVATE);
        expirySwitch.setChecked(preferences.getBoolean("expiry_alerts", true));
        String selectedUnit = preferences.getString("default_unit", "g");
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equalsIgnoreCase(selectedUnit)) {
                defaultUnitSpinner.setSelection(i);
                break;
            }
        }

        Button save = findViewById(R.id.btn_save_settings);
        save.setOnClickListener(v -> {
            String unit = defaultUnitSpinner.getSelectedItem() == null ? "g" : defaultUnitSpinner.getSelectedItem().toString();
            preferences.edit()
                    .putBoolean("expiry_alerts", expirySwitch.isChecked())
                    .putString("default_unit", unit)
                    .apply();
            Toast.makeText(this, "Preferences saved", Toast.LENGTH_SHORT).show();
        });
    }
}
