package ar.edu.itba.persistenceInterface;

public interface LikesDao {
    Boolean isRecipeLikedById(long userId, long recipeId);

    Boolean isRecipeDislikedById(long userId, long recipeId);

    int makeRecipeLiked(long userId, long recipeId);

    int makeRecipeDisliked(long userId, long recipeId);

    int removeRecipeRating(long userId, long recipeId);

    long getLikes(long userId);

}
