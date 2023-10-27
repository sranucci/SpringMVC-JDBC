package ar.edu.itba.paw.servicesInterface.ingredient;

import ar.edu.itba.paw.models.ingredient.Ingredient;
import ar.edu.itba.paw.models.ingredient.RecipeIngredient;
import ar.edu.itba.paw.models.ingredient.RecipeIngredientRecover;

import java.util.List;
import java.util.Optional;

public interface IngredientsService {
    List<Ingredient> getAllIngredients();
    List<RecipeIngredientRecover> getAllRecipeIngredientsRecover(long recipeId);
    int createRecipeIngredient(long recipeId, String ingredient, float qty, long unitId);
    boolean deleteRecipeIngredients(long recipeId);

}
