package za.co.smartpantry.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure-Java strict-matching rule (no Android classes, so it can be unit tested on the JVM).
 *
 * A recipe qualifies ONLY if every required ingredient is available in the pantry, in a
 * compatible measurement family, in at least the required amount.
 */
public final class RecipeMatcher {
    private RecipeMatcher() { }

    /** Minimal view of a stock or requirement line: name, quantity and unit. */
    public static final class Line {
        public final String name;
        public final double quantity;
        public final String unit;
        public Line(String name, double quantity, String unit) {
            this.name = name; this.quantity = quantity; this.unit = unit;
        }
    }

    /** Totals pantry stock per "normalisedName|unitFamily" key (duplicates are summed). */
    public static Map<String, Double> buildAvailable(List<Line> pantry) {
        Map<String, Double> available = new HashMap<>();
        for (Line item : pantry) {
            if (item == null || !isValidQuantity(item.quantity)) continue;
            IngredientNormalizer.Measure m = IngredientNormalizer.toMeasure(item.quantity, item.unit);
            String key = IngredientNormalizer.normalizeName(item.name) + "|" + m.family;
            Double existing = available.get(key);
            available.put(key, (existing == null ? 0.0 : existing) + m.baseQuantity);
        }
        return available;
    }

    /** True only when EVERY required line is covered. An empty requirement list never matches. */
    public static boolean canMake(Map<String, Double> available, List<Line> required) {
        if (required.isEmpty()) return false;
        for (Line need : required) {
            if (need == null || !isValidQuantity(need.quantity)) return false;
            IngredientNormalizer.Measure m = IngredientNormalizer.toMeasure(need.quantity, need.unit);
            String key = IngredientNormalizer.normalizeName(need.name) + "|" + m.family;
            Double have = available.get(key);
            if (have == null || !Double.isFinite(have) || have < 0
                    || !Double.isFinite(m.baseQuantity) || have + 0.000001 < m.baseQuantity) {
                return false; // missing, invalid or short stock rejects the whole recipe
            }
        }
        return true;
    }

    private static boolean isValidQuantity(double quantity) {
        return Double.isFinite(quantity) && quantity > 0;
    }
}
