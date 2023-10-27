package ar.edu.itba.paw.servicesInterface;

import ar.edu.itba.paw.dtos.RecipesAndSize;
import ar.edu.itba.paw.dtos.UploadedRecipeFormDto;
import ar.edu.itba.paw.enums.AvailableDifficultiesForSort;
import ar.edu.itba.paw.enums.ShowRecipePages;
import ar.edu.itba.paw.enums.SortOptions;
import ar.edu.itba.paw.models.Difficulty;
import ar.edu.itba.paw.models.recipe.FullRecipe;
import ar.edu.itba.paw.models.recipe.Recipe;
import ar.edu.itba.paw.models.recipe.RecipeImage;

import java.util.List;
import java.util.Optional;

public interface RecipeService {

    boolean updateRecipe(UploadedRecipeFormDto rf, long userId, long recipeId);

    Optional<RecipeImage> getImage(long imageId);

    boolean removeRecipeNotifyingUser(long recipeId, String deletionMotive);



    long create(UploadedRecipeFormDto rf, long userId);

    boolean removeRecipe(long recipeId, long userId);

    Optional<Recipe> getRecipe(long recipeId);

    Optional<UploadedRecipeFormDto> recoverUserRecipe(long recipeId, long userId);

    Optional<FullRecipe> getFullRecipe(long recipeId, Optional<Long> userId, Optional<Long> commentsToBring, Optional<Integer> pageNumber);

    RecipesAndSize getRecipesByFilter(Optional<AvailableDifficultiesForSort> difficulty, Optional<String> ingredients, List<Integer> categories, Optional<SortOptions> sort, Optional<String> query, ShowRecipePages pageToShow, Optional<Long> userId, Optional<Long> page, Optional<Integer> pageSize);
}
