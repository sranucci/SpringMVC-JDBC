package ar.edu.itba.paw.models.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipeImage {

    private final long imageId;
    private final byte[] image;
    private final boolean mainImage;

}
