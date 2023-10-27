package ar.edu.itba.paw.models.category;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipeCategory {

    private final long categoryId;

    private final long recipeId;

}
