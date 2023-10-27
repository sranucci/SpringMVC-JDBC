package ar.edu.itba.persistenceInterface;

public interface SavedRecipesDao {
    Boolean isRecipeSavedByUser(long userId, long recipeId);

    int saveRecipe(long userId, long recipeId);

    int deleteSavedRecipe(long userId, long recipeId);

}
