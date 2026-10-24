package za.co.smartpantry.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import za.co.smartpantry.util.RecipeMatcher.Line;

/** JVM unit tests for the strict-matching rule and name/unit normalisation. */
public class StrictMatchingTest {
    private static Line l(String n, double q, String u) { return new Line(n, q, u); }
    private static boolean can(List<Line> pantry, Line... need) {
        Map<String, Double> a = RecipeMatcher.buildAvailable(pantry);
        return RecipeMatcher.canMake(a, Arrays.asList(need));
    }

    @Test public void emptyPantryMatchesNothing() {
        assertFalse(can(Arrays.<Line>asList(), l("egg", 2, "pcs")));
    }
    @Test public void allIngredientsPresentMatches() {
        assertTrue(can(Arrays.asList(l("egg", 2, "pcs"), l("tomato", 120, "g")),
                l("egg", 2, "pcs"), l("tomato", 120, "g")));
    }
    @Test public void oneMissingIngredientRejectsWholeRecipe() {
        assertFalse(can(Arrays.asList(l("egg", 5, "pcs")), l("egg", 2, "pcs"), l("tomato", 120, "g")));
    }
    @Test public void oneInsufficientQuantityRejectsWholeRecipe() {
        assertFalse(can(Arrays.asList(l("egg", 2, "pcs"), l("tomato", 100, "g")),
                l("egg", 2, "pcs"), l("tomato", 120, "g")));
    }
    @Test public void extraQuantityStillMatches() {
        assertTrue(can(Arrays.asList(l("tomato", 500, "g")), l("tomato", 120, "g")));
    }
    @Test public void tomatoPluralMatchesSingular() {
        assertTrue(can(Arrays.asList(l("Tomatoes", 200, "g")), l("tomato", 120, "g")));
        assertTrue(can(Arrays.asList(l("tomato", 200, "g")), l("tomatoes", 120, "g")));
    }
    @Test public void kgAndGramsAreCompatible() {
        assertTrue(can(Arrays.asList(l("pasta", 1, "kg")), l("pasta", 150, "g")));
        assertFalse(can(Arrays.asList(l("pasta", 0.1, "kg")), l("pasta", 150, "g")));
    }
    @Test public void tablespoonConvertsToMillilitres() {
        assertTrue(can(Arrays.asList(l("olive oil", 1, "tbsp")), l("olive oil", 10, "ml")));
        assertFalse(can(Arrays.asList(l("olive oil", 1, "tsp")), l("olive oil", 10, "ml")));
    }
    @Test public void gramsAndMillilitresAreNeverCrossConverted() {
        assertFalse(can(Arrays.asList(l("milk", 500, "g")), l("milk", 100, "ml")));
    }
    @Test public void piecesAndGramsAreNeverCrossConverted() {
        assertFalse(can(Arrays.asList(l("tomato", 3, "pcs")), l("tomato", 120, "g")));
    }
    @Test public void duplicatePantryRowsAreSummed() {
        assertTrue(can(Arrays.asList(l("flour", 60, "g"), l("Flour", 60, "g")), l("flour", 120, "g")));
    }
    @Test public void emptyRequirementListNeverMatches() {
        assertFalse(can(Arrays.asList(l("egg", 2, "pcs"))));
    }
    @Test public void invalidPantryQuantitiesNeverSatisfyRequirements() {
        assertFalse(can(Arrays.asList(l("egg", Double.NaN, "pcs")), l("egg", 1, "pcs")));
        assertFalse(can(Arrays.asList(l("egg", Double.POSITIVE_INFINITY, "pcs")), l("egg", 1, "pcs")));
        assertFalse(can(Arrays.asList(l("egg", -1, "pcs")), l("egg", 1, "pcs")));
    }
    @Test public void invalidRequirementQuantitiesNeverMatch() {
        List<Line> pantry = Arrays.asList(l("egg", 2, "pcs"));
        assertFalse(can(pantry, l("egg", Double.NaN, "pcs")));
        assertFalse(can(pantry, l("egg", Double.POSITIVE_INFINITY, "pcs")));
        assertFalse(can(pantry, l("egg", -1, "pcs")));
    }

    @Test public void normalisation() {
        assertEquals("tomato", IngredientNormalizer.normalizeName("Tomatoes"));
        assertEquals("potato", IngredientNormalizer.normalizeName("potatoes"));
        assertEquals("berry", IngredientNormalizer.normalizeName("berries"));
        assertEquals("sweet potato", IngredientNormalizer.normalizeName("Sweet Potatoes"));
        assertEquals("egg", IngredientNormalizer.normalizeName(" EGGS "));
        assertEquals("pie", IngredientNormalizer.normalizeName("pies"));
        assertEquals("cookie", IngredientNormalizer.normalizeName("cookies"));
        assertEquals("hummus", IngredientNormalizer.normalizeName("hummus"));
        assertEquals("cheese", IngredientNormalizer.normalizeName("cheese"));
        assertEquals("peach", IngredientNormalizer.normalizeName("peaches"));
    }
    @Test public void unitCanonicalisation() {
        assertEquals("g", IngredientNormalizer.canonicalUnit("Grams"));
        assertEquals("ml", IngredientNormalizer.canonicalUnit("millilitres"));
        assertEquals("pcs", IngredientNormalizer.canonicalUnit("pieces"));
        assertEquals(2000.0, IngredientNormalizer.toMeasure(2, "kg").baseQuantity, 0.001);
        assertEquals(30.0, IngredientNormalizer.toMeasure(2, "tbsp").baseQuantity, 0.001);
    }
}
