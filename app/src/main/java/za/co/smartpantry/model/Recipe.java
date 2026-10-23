package za.co.smartpantry.model;

public class Recipe {
    private final long id;
    private final String name;
    private final String description;
    private final String instructions;
    private final int ingredientCount;

    public Recipe(long id, String name, String description, String instructions, int ingredientCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.ingredientCount = ingredientCount;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getInstructions() { return instructions; }
    public int getIngredientCount() { return ingredientCount; }
}
