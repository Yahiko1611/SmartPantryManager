package za.co.smartpantry.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Normalises common ingredient plurals and converts comparable measurement units. */
public final class IngredientNormalizer {
    private IngredientNormalizer() { }

    private static final Map<String, String> IRREGULAR = new HashMap<>();
    static {
        String[][] pairs = {
            {"tomatoes", "tomato"}, {"potatoes", "potato"}, {"sweet potatoes", "sweet potato"},
            {"eggs", "egg"}, {"onions", "onion"}, {"carrots", "carrot"}, {"mushrooms", "mushroom"},
            {"bananas", "banana"}, {"apples", "apple"}, {"lemons", "lemon"}, {"berries", "berry"},
            {"strawberries", "strawberry"}, {"blueberries", "blueberry"}, {"blackberries", "blackberry"},
            {"raspberries", "raspberry"}, {"cherries", "cherry"}, {"leaves", "leaf"},
            {"chickpeas", "chickpea"}, {"lentils", "lentil"}, {"noodles", "noodle"},
            {"tortillas", "tortilla"}, {"slices", "slice"}, {"cloves", "clove"}, {"peas", "pea"},
            {"oats", "oat"}, {"frozen berries", "frozen berry"}, {"garlic cloves", "garlic"},
            {"garlic clove", "garlic"}, {"extra virgin olive oil", "olive oil"}, {"black peppers", "black pepper"}
        };
        for (String[] pair : pairs) IRREGULAR.put(pair[0], pair[1]);
    }

    public static String normalizeName(String raw) {
        if (raw == null) return "";
        String name = raw.toLowerCase(Locale.ROOT).trim().replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ");
        if (IRREGULAR.containsKey(name)) return IRREGULAR.get(name);
        // Words that already end in "s" in the singular, or whose plural is "-ies" with a singular "-ie".
        if (name.endsWith("hummus") || name.endsWith("couscous") || name.endsWith("asparagus")
                || name.endsWith("molasses") || name.endsWith("watercress")) return name;
        if (name.endsWith("cookies") || name.endsWith("brownies") || name.endsWith("veggies")
                || name.endsWith("smoothies") || name.endsWith("pies")) return name.substring(0, name.length() - 1);
        if (name.endsWith("ies") && name.length() > 3) return name.substring(0, name.length() - 3) + "y";
        if (name.endsWith("oes") && name.length() > 3) return name.substring(0, name.length() - 2);
        if ((name.endsWith("ches") || name.endsWith("shes") || name.endsWith("xes") || name.endsWith("zes")) && name.length() > 4) {
            return name.substring(0, name.length() - 2);
        }
        if (name.endsWith("s") && !name.endsWith("ss") && name.length() > 2) return name.substring(0, name.length() - 1);
        return name;
    }

    public static String canonicalUnit(String raw) {
        String unit = raw == null ? "" : raw.toLowerCase(Locale.ROOT).trim();
        switch (unit) {
            case "gram": case "grams": case "g": return "g";
            case "kilogram": case "kilograms": case "kgs": case "kg": return "kg";
            case "millilitre": case "millilitres": case "milliliter": case "milliliters": case "ml": return "ml";
            case "litre": case "litres": case "liter": case "liters": case "l": return "l";
            case "teaspoon": case "teaspoons": case "tsp": return "tsp";
            case "tablespoon": case "tablespoons": case "tbsp": return "tbsp";
            case "cup": case "cups": return "cup";
            case "piece": case "pieces": case "pc": case "pcs": case "each": return "pcs";
            case "slice": case "slices": return "slices";
            case "pinches": case "pinch": return "pinch";
            case "clove": case "cloves": return "clove";
            default: return unit;
        }
    }

    public static Measure toMeasure(double quantity, String rawUnit) {
        String unit = canonicalUnit(rawUnit);
        switch (unit) {
            case "kg": return new Measure("mass", quantity * 1000.0, "g");
            case "g": return new Measure("mass", quantity, "g");
            case "l": return new Measure("volume", quantity * 1000.0, "ml");
            case "ml": return new Measure("volume", quantity, "ml");
            case "tsp": return new Measure("volume", quantity * 5.0, "ml");
            case "tbsp": return new Measure("volume", quantity * 15.0, "ml");
            case "cup": return new Measure("volume", quantity * 240.0, "ml");
            case "pcs": return new Measure("count", quantity, "pcs");
            case "slices": return new Measure("slices", quantity, "slices");
            case "pinch": return new Measure("pinch", quantity, "pinch");
            case "clove": return new Measure("clove", quantity, "clove");
            default: return new Measure("unit:" + unit, quantity, unit);
        }
    }

    public static final class Measure {
        public final String family;
        public final double baseQuantity;
        public final String baseUnit;

        public Measure(String family, double baseQuantity, String baseUnit) {
            this.family = family;
            this.baseQuantity = baseQuantity;
            this.baseUnit = baseUnit;
        }
    }
}
