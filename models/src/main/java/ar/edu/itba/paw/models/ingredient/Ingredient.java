package ar.edu.itba.paw.models.ingredient;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Ingredient {

    private final long ingredientId;
    private final String name;

}
