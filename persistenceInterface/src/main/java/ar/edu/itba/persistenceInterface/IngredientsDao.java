package ar.edu.itba.persistenceInterface;

import ar.edu.itba.paw.models.ingredient.Ingredient;
import ar.edu.itba.paw.models.ingredient.RecipeIngredient;
import ar.edu.itba.paw.models.ingredient.RecipeIngredientRecover;

import java.util.List;
import java.util.Optional;

public interface IngredientsDao {

    List<RecipeIngredient> getAllRecipeIngredients(long recipeId);

    Optional<Ingredient> getIngredient(long ingredientId);

    List<Ingredient> getAllIngredients();

    int createRecipeIngredient(long recipeId, long ingredientId, float qty, long unitId);

    Optional<Long> getIngredientId(String ingredientName);


    List<RecipeIngredientRecover> getAllRecipeIngredientsRecover(long recipeId);

    boolean deleteRecipeIngredients(long recipeId);

}
