package ar.edu.itba.paw.servicesInterface;

public interface SavedRecipesService {
    Boolean isRecipeSavedByUser(long userId, long recipeId);
    int deleteSavedRecipe(long userId, long recipeId);
    int saveRecipe(long userId, long recipeId);
}
