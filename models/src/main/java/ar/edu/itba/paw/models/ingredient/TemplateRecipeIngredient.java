package ar.edu.itba.paw.models.ingredient;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class TemplateRecipeIngredient {

    private final String name;
    private final float quantity;
}
