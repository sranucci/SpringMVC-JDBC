package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.UploadedRecipeFormDto;
import ar.edu.itba.paw.enums.UserInteraction;
import ar.edu.itba.paw.models.UserComment;
import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.ingredient.RecipeIngredient;
import ar.edu.itba.paw.models.recipe.FullRecipe;
import ar.edu.itba.paw.servicesInterface.ImageService;
import ar.edu.itba.paw.servicesInterface.MailService;
import ar.edu.itba.paw.servicesInterface.category.CategoryService;
import ar.edu.itba.paw.servicesInterface.comments.CommentsService;
import ar.edu.itba.paw.servicesInterface.ingredient.IngredientsService;
import ar.edu.itba.persistenceInterface.RecipeDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;

@RunWith(MockitoJUnitRunner.class)
public class RecipeServiceImplTest {

    private static final long USER_ID = 1;


    private static final long RECIPE_ID = 1;
    protected static final String RECIPE_TITLE = "title";
    protected static final String DESCRIPTION = "description";
    protected static final boolean IS_PRIVATE = false;
    protected static final int TOTAL_MINUTES = 75;

    protected static final Long DIFFICULTY = 1L;
    protected static final int SERVINGS = 4;
    protected static final String[] INSTRUCTIONS_ARRAY = {"Instruction 1", "Instruction 2", "Ins3"};
    protected static final byte[] IMG_BYTEA= new byte[]{0x00};
    protected static final Long[] CATEGORIES= new Long[]{1L};
    protected static final Float[] QUANTITIES= new Float[]{1F};
    protected static final String[] INGREDIENTS= new String[]{"salt"};
    protected static final Long[] MEASURE_IDS= new Long[]{1L};
    protected static final Long IMAGE_ID =  1L;



    @Mock
    private RecipeDao recipeDao;
    @Mock
    private IngredientsService ingredientsService;
    @Mock
    private CategoryService categoryService;

    @Mock
    private ImageService imageService;


    @InjectMocks
    private RecipeServiceImpl rs;

    @Test
    public void testCreate() {
        List<byte[]> recipeImages= new ArrayList();
        recipeImages.add(IMG_BYTEA);

        List<Long> imagesIds = new ArrayList<>();
        imagesIds.add(IMAGE_ID);

        UploadedRecipeFormDto uploadedRecipeFormDto = new UploadedRecipeFormDto(
                SERVINGS, CATEGORIES, RECIPE_TITLE, recipeImages, DESCRIPTION,
                QUANTITIES, INGREDIENTS, MEASURE_IDS, INSTRUCTIONS_ARRAY, TOTAL_MINUTES,
                DIFFICULTY, IS_PRIVATE, imagesIds);

        when(recipeDao.create(RECIPE_TITLE,DESCRIPTION,USER_ID,
                IS_PRIVATE,TOTAL_MINUTES,DIFFICULTY,SERVINGS,INSTRUCTIONS_ARRAY))
                .thenReturn(RECIPE_ID);

        doNothing().when(imageService).createImages(RECIPE_ID, recipeImages);


        when(ingredientsService.createRecipeIngredient(RECIPE_ID,INGREDIENTS[0],QUANTITIES[0],MEASURE_IDS[0]))
                .thenReturn(1);
        when(categoryService.createRecipeCategory(RECIPE_ID, CATEGORIES[0])).thenReturn(1L);

        long recipeId = rs.create(uploadedRecipeFormDto, USER_ID);
        assertEquals(1, recipeId);

    }

    @Test
    public void testRecoverUserRecipeWhenError(){
       when(recipeDao.getFullRecipe(RECIPE_ID)).thenReturn(Optional.empty());
       Optional<UploadedRecipeFormDto> recipe = rs.recoverUserRecipe(RECIPE_ID,USER_ID);
       assertFalse(recipe.isPresent());
    }


}