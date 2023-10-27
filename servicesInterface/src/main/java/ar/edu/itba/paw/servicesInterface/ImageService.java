package ar.edu.itba.paw.servicesInterface;

import ar.edu.itba.paw.models.recipe.RecipeImage;

import java.util.List;
import java.util.Optional;

public interface ImageService {
    void createImages(long recipeId, List<byte[]> serializedImages);

    Optional<RecipeImage> getImage(long imageId);

    List<Long> getImagesForRecipe(long recipeId);

    boolean removeImages(long recipeId);
}
