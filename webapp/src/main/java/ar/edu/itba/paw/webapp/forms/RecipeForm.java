package ar.edu.itba.paw.webapp.forms;

import ar.edu.itba.paw.dtos.UploadedRecipeFormDto;
import ar.edu.itba.paw.webapp.annotations.ValidImages;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class RecipeForm extends AbstractRecipeFormTemplate {


    @ValidImages
    private List<MultipartFile> recipeImages;

    @Override
    protected List<MultipartFile> getImageList() {
        return recipeImages;
    }

    @Override
    public UploadedRecipeFormDto asUploadedFormDto() {
        return new UploadedRecipeFormDto(getServings(), getCategories(), getTitle(),
                getImagesAsBytes(), getDescription(), getCleanQuantitys(), getCleanIngredients(),
                getCleanMeasureIds(), getCleanInstructions(), getTotalRecipeTime(), getDifficulty(), getVisibility().equals("Private"), null);
    }


}
