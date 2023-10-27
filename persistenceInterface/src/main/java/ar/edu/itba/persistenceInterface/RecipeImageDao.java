package ar.edu.itba.persistenceInterface;

import ar.edu.itba.paw.models.recipe.RecipeImage;

import java.util.List;
import java.util.Optional;

public interface RecipeImageDao {
    void createImage(long recipeId, byte[] serializedImage, boolean isMainImage);

    Optional<RecipeImage> getImage(long imageId);

    List<Long> getImagesForRecipe(long recipeId);

    boolean removeImages(long recipeId);

}
