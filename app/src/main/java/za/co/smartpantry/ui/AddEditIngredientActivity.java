package za.co.smartpantry.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import za.co.smartpantry.R;
import za.co.smartpantry.data.DatabaseHelper;
import za.co.smartpantry.model.PantryItem;
import za.co.smartpantry.util.IngredientNormalizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditIngredientActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM_ID = "item_id";
    private static final String[] UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "pcs", "slices", "pinch", "clove"};

    private DatabaseHelper database;
    private EditText nameInput, quantityInput, expiryInput;
    private Spinner unitSpinner;
    private long itemId = -1L;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Offload database read/write operations off the main thread to prevent ANR

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        database = new DatabaseHelper(this);
        NavigationHelper.bind(this, "");
        nameInput = findViewById(R.id.input_ingredient_name);
        quantityInput = findViewById(R.id.input_ingredient_quantity);
        expiryInput = findViewById(R.id.input_expiry_date);
        unitSpinner = findViewById(R.id.spinner_ingredient_unit);
        TextView title = findViewById(R.id.txt_form_title);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, UNITS);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(unitAdapter);
        String defaultUnit = getSharedPreferences("pantry_settings", MODE_PRIVATE).getString("default_unit", "g");
        selectUnit(defaultUnit);

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1L);
        if (itemId >= 0) {
            title.setText("Edit ingredient");
            Button save = findViewById(R.id.btn_save_ingredient);
            save.setText("Update ingredient");
            executorService.execute(() -> {
                PantryItem item = database.getPantryItem(itemId);
                if (isFinishing() || isDestroyed()) return;
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    if (item == null) {
                        Toast.makeText(this, "Ingredient could not be found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    nameInput.setText(item.getName());
                    quantityInput.setText(PantryAdapter.formatQuantity(item.getQuantity()));
                    selectUnit(item.getUnit());
                    if (item.getExpiryDate() != null) expiryInput.setText(item.getExpiryDate());
                });
            });
        }

        findViewById(R.id.btn_pick_expiry).setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btn_save_ingredient).setOnClickListener(v -> saveIngredient());
        findViewById(R.id.btn_cancel_ingredient).setOnClickListener(v -> finish());
    }

    private void selectUnit(String value) {
        for (int index = 0; index < UNITS.length; index++) {
            if (IngredientNormalizer.canonicalUnit(UNITS[index]).equals(IngredientNormalizer.canonicalUnit(value))) {
                unitSpinner.setSelection(index);
                return;
            }
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        String existing = expiryInput.getText().toString().trim();
        if (!existing.isEmpty()) {
            try {
                Date parsed = parseDate(existing);
                calendar.setTime(parsed);
            } catch (ParseException ignored) { }
        }
        DatePickerDialog dialog = new DatePickerDialog(this, (picker, year, month, day) ->
                expiryInput.setText(String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveIngredient() {
        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        String unit = unitSpinner.getSelectedItem() == null ? "" : unitSpinner.getSelectedItem().toString();
        String expiry = expiryInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Enter an ingredient name");
            nameInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(quantityText)) {
            quantityInput.setError("Enter a quantity greater than zero");
            quantityInput.requestFocus();
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException exception) {
            quantityInput.setError("Enter a valid number");
            quantityInput.requestFocus();
            return;
        }
        if (Double.isNaN(quantity) || Double.isInfinite(quantity) || quantity <= 0) {
            quantityInput.setError("Quantity must be greater than zero");
            quantityInput.requestFocus();
            return;
        }
        if (!expiry.isEmpty()) {
            try {
                parseDate(expiry);
            } catch (ParseException exception) {
                expiryInput.setError("Use YYYY-MM-DD, for example 2026-10-31");
                expiryInput.requestFocus();
                return;
            }
        }

        Button saveBtn = findViewById(R.id.btn_save_ingredient);
        saveBtn.setEnabled(false);

        executorService.execute(() -> {
            try {
                if (itemId >= 0) {
                    database.updatePantryItem(itemId, name, quantity, unit, expiry);
                    if (isFinishing() || isDestroyed()) return;
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    database.insertPantryItem(name, quantity, unit, expiry);
                    if (isFinishing() || isDestroyed()) return;
                    runOnUiThread(() -> {
                        if (isFinishing() || isDestroyed()) return;
                        Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            } catch (RuntimeException exception) {
                if (isFinishing() || isDestroyed()) return;
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    saveBtn.setEnabled(true);
                    Toast.makeText(this, "Could not save ingredient. Please check the details and try again.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private Date parseDate(String value) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        formatter.setLenient(false);
        Date date = formatter.parse(value);
        if (date == null || !formatter.format(date).equals(value)) throw new ParseException("Invalid date", 0);
        return date;
    }

    @Override protected void onDestroy() {
        // Ensure database closes after pending background writes finish
        executorService.execute(database::close);
        executorService.shutdown();
        super.onDestroy();
    }
}
