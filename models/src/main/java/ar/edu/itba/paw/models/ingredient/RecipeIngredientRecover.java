package ar.edu.itba.paw.models.ingredient;

import lombok.Getter;

@Getter
public class RecipeIngredientRecover extends TemplateRecipeIngredient {

    private final long unitId;

    public RecipeIngredientRecover(String name, long unitId, float quantity) {
        super(name, quantity);
        this.unitId = unitId;
    }
}
