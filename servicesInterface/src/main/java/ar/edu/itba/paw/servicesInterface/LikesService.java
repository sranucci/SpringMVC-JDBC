package ar.edu.itba.paw.servicesInterface;

public interface LikesService {
    Boolean isRecipeLikedById(long userId, long recipeId);
    Boolean isRecipeDislikedById(long userId, long recipeId);

    int makeRecipeLiked(long userId, long recipeId);

    int makeRecipeDisliked(long userId, long recipeId);

    int removeRecipeRating(long userId, long recipeId);
}
