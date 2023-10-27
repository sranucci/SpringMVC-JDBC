package ar.edu.itba.persistenceInterface;

import ar.edu.itba.paw.enums.AvailableDifficultiesForSort;
import ar.edu.itba.paw.enums.ShowRecipePages;
import ar.edu.itba.paw.enums.SortOptions;
import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.deletion.DeletionData;
import ar.edu.itba.paw.models.ingredient.RecipeIngredient;
import ar.edu.itba.paw.models.recipe.FullRecipe;
import ar.edu.itba.paw.models.recipe.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeDao {
    //Notar que no pongo id de dificultad porque esto es lo que se muestra!, importa para crear en base de datos
    //me interesa mostrar la dificultad
    long create(String title, String description, long userId, boolean isPrivate, int totalMinutes, long difficultyId, int servings, String[] instructions);

    Optional<String[]> getRecipeInstructions(long recipeId);

    Optional<Recipe> getRecipe(long recipeId);

    Optional<DeletionData> getRecipeDeletionData(long recipeId);

    boolean removeRecipe(long recipeId);

    boolean removeRecipe(long recipeId, long userId);

    List<RecipeIngredient> getIngredientsById(long recipeId);

    List<Category> getCategoriesById(long recipeId);

    List<Long> getPhotoIdsByRecipeId(long recipeId);

    List<Recipe> getRecipesByFilter(Optional<AvailableDifficultiesForSort> difficulty, Optional<String> ingredients, List<Integer> categories,
                                    Optional<SortOptions> sort, Optional<String> searchQuery, ShowRecipePages pageToShow,
                                    Optional<Long> userId, Optional<Long> page, Optional<Integer> limit);

    long getTotalNumberRecipesByFilterForPagination(Optional<AvailableDifficultiesForSort> difficulty, Optional<String> ingredients, List<Integer> categories, Optional<SortOptions> sort, Optional<String> query, ShowRecipePages pageToShow, Optional<Long> userId, Optional<Long> page, Optional<Integer> pageSize);

    Optional<FullRecipe> getFullRecipe(long recipeId);


    boolean updateRecipe(String title, String description, boolean isPrivate, int totalMinutes, long difficultyId, int servings, String[] instructions, long userId, long recipeId);
}
