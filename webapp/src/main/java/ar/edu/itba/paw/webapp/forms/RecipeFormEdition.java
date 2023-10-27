package ar.edu.itba.paw.webapp.forms;


import ar.edu.itba.paw.dtos.UploadedRecipeFormDto;
import ar.edu.itba.paw.webapp.annotations.ValidEditionImages;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class RecipeFormEdition extends AbstractRecipeFormTemplate {


    private List<Long> imageIdLists;

    @ValidEditionImages
    private List<MultipartFile> recipeImages;


    public void setEditionVisibility(boolean isPrivate) {
        setVisibility(isPrivate ? "Private" : "Public");
    }

    public void setTimeFromTotalMinutes(int totalMinutes) {
        setTotalHours(totalMinutes / 60);
        setTotalMinutes(totalMinutes % 60);
    }

    public void setFromRecipeFormDto(UploadedRecipeFormDto formDto) {
        setEditionVisibility(formDto.isPrivate());
        setCategories(formDto.getCategories());
        setTitle(formDto.getTitle());
        setDescription(formDto.getDescription());
        setQuantitys(formDto.getQuantitys());
        setIngredients(formDto.getIngredients());
        setMeasureIds(formDto.getMeasureIds());
        setInstructions(formDto.getInstructions());
        setServings(formDto.getServings());
        setTimeFromTotalMinutes(formDto.getTotalTime());
        setDifficulty(formDto.getDifficultyId());
        setImageIdLists(formDto.getImageIds());

    }

    @Override
    public UploadedRecipeFormDto asUploadedFormDto() {
        return new UploadedRecipeFormDto(getServings(), getCategories(), getTitle(),
                getImagesAsBytes(), getDescription(), getCleanQuantitys(), getCleanIngredients(),
                getCleanMeasureIds(), getCleanInstructions(), getTotalRecipeTime(), getDifficulty(), getVisibility().equals("Private"), imageIdLists);
    }


    @Override
    protected List<MultipartFile> getImageList() {
        return recipeImages;
    }

}
