package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.enums.AvailableDifficultiesForSort;
import ar.edu.itba.paw.enums.ShowRecipePages;
import ar.edu.itba.paw.enums.SortOptions;
import ar.edu.itba.paw.models.deletion.DeletionData;
import ar.edu.itba.paw.models.recipe.Recipe;
import ar.edu.itba.paw.models.ingredient.RecipeIngredient;
import ar.edu.itba.paw.models.recipe.FullRecipe;
import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.RecipeDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.*;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@Sql(scripts = { "classpath:RecipeDaoImplTest.sql"})
public class RecipeDaoImplTest extends GlobalTestVariables {

    @Autowired
    private DataSource ds;

    @Autowired
    private RecipeDao recipeDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }



    @Test
    public void testCreate() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_recipe");

        recipeDao.create(RECIPE_TITLE, DESCRIPTION, USER_ID, IS_PRIVATE, TOTAL_MINUTES, DIFFICULTY, SERVINGS, INSTRUCTIONS_ARRAY );

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_recipe"));

    }

    @Test
    public void testGetRecipeDeletionData(){
        Optional<DeletionData> deletionDataOptional = recipeDao.getRecipeDeletionData(RECIPE_ID);
        assertTrue(deletionDataOptional.isPresent());
        assertEquals(RECIPE_TITLE, deletionDataOptional.get().getRecipeName());
        assertEquals(EMAIL, deletionDataOptional.get().getUserMail());
    }


    @Test
    public void testRemoveRecipe(){
        recipeDao.removeRecipe(RECIPE_ID);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_recipe"));
    }

    @Test
    public void testRemoveRecipeWhenRecipeDoesNotExist(){
        recipeDao.removeRecipe(NON_EXISTENT_RECIPE_ID);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_recipe"));
    }

    @Test
    public void testGetRecipeById(){
        Optional<Recipe> recipe = recipeDao.getRecipe(RECIPE_ID);

        assertTrue(recipe.isPresent());
        assertEquals(RECIPE_TITLE, recipe.get().getTitle());
        assertEquals(DESCRIPTION, recipe.get().getDescription());
        assertEquals(USER_ID, recipe.get().getUserId());
        assertEquals(IS_PRIVATE, recipe.get().getIsPrivate());
        assertEquals(MINUTES, recipe.get().getMinutes());
        assertEquals(HOURS, recipe.get().getHours());
        assertEquals(DIFFICULTY, recipe.get().getDifficulty());
        assertEquals(SERVINGS, recipe.get().getServings());
    }

    @Test
    public void testGetRecipeByIdWhenRecipeDoesNotExist(){
        Optional<Recipe> recipe = recipeDao.getRecipe(NON_EXISTENT_RECIPE_ID);
        assertFalse(recipe.isPresent());
    }

    @Test
    public void testGetIngredientsById(){
        List<RecipeIngredient> ingredientsList = recipeDao.getIngredientsById(RECIPE_ID);
        assertEquals(1, ingredientsList.size());
        assertEquals(INGREDIENT_STR_1, ingredientsList.get(0).getName());
        assertEquals(UNIT_STR_1, ingredientsList.get(0).getUnits());
        assertEquals(1.0, ingredientsList.get(0).getQuantity(), 0.001);
    }


    @Test
    public void testGetCategoriesById(){
        List<Category> categoryList = recipeDao.getCategoriesById(RECIPE_ID);
        assertEquals(1, categoryList.size());
        assertEquals(CATEGORY_STR_1, categoryList.get(0).getName());
        assertEquals(CATEGORY_ID_1, categoryList.get(0).getCategoryId());
    }
    @Test
    public void testGetPhotoIdsByRecipeId(){
        List<Long> photoIdList = recipeDao.getPhotoIdsByRecipeId(1);
        assertEquals(1, photoIdList.size());
    }

    @Test
    public void testGetFullRecipe(){
        Optional<FullRecipe> recipe = recipeDao.getFullRecipe(RECIPE_ID);
        assertTrue(recipe.isPresent());
        assertEquals(RECIPE_TITLE, recipe.get().getTitle());
        assertEquals(DESCRIPTION, recipe.get().getDescription());
        assertEquals(USER_ID, recipe.get().getUserId());
        assertEquals(IS_PRIVATE, recipe.get().getIsPrivate());
        assertEquals(MINUTES, recipe.get().getMinutes());
        assertEquals(HOURS, recipe.get().getHours());
        assertEquals(DIFFICULTY, recipe.get().getDifficulty());
        assertEquals(SERVINGS, recipe.get().getServings());
        assertEquals(1, recipe.get().getLikesCount());
        assertEquals(1, recipe.get().getDislikesCount());
        assertArrayEquals(INSTRUCTIONS_ARRAY, recipe.get().getInstructions());
    }

    @Test
    public void testGetFullRecipeWhenRecipeIdDoesNotExist(){
        Optional<FullRecipe> recipe = recipeDao.getFullRecipe(NON_EXISTENT_RECIPE_ID);
        assertFalse(recipe.isPresent());
    }

    @Test
    public void testUpdateRecipe(){
        boolean rowsAffected = recipeDao.updateRecipe("title2", DESCRIPTION, IS_PRIVATE, TOTAL_MINUTES, DIFFICULTY,
                SERVINGS, INSTRUCTIONS_ARRAY,USER_ID,RECIPE_ID);
        assertTrue(rowsAffected);
    }

    @Test
    public void testGetRecipeInstructions(){
        Optional<String[]> instructionsArray = recipeDao.getRecipeInstructions(RECIPE_ID);
        assertTrue(instructionsArray.isPresent());
        assertArrayEquals(INSTRUCTIONS_ARRAY, instructionsArray.get());
    }


    // intentamos testear las queries de getRecipesByFilter y getTotalRecipes, pero hsqldb no reconoce ni el operador ILIKE ni el ALL (?), con ? siendo un array
//    @Test
//    public void testGetTotalRecipes(){
//        List<Integer> categories = new ArrayList<>();
//        categories.add(1);
//        List<Recipe> recipeList = recipeDao.getRecipesByFilter(
//                Optional.of(AvailableDifficultiesForSort.EASY),
//                Optional.of("salt"),
//                categories,
//                Optional.of(SortOptions.ALPHABETIC_ASC),
//                Optional.empty(),
//                ShowRecipePages.DISCOVER,
//                Optional.of(USER_ID),
//                Optional.of(1L),
//                Optional.of(5)
//        );
//
//    }
//
//    @Test
//    public void testGetTotalRecipesWhenInexistentIngredient(){
//        List<Integer> categories = new ArrayList<>();
//        categories.add(1);
//        long total = recipeDao.getTotalNumberRecipesByFilterForPagination(
//                Optional.empty(),
//                Optional.empty(),
//                categories,
//                Optional.of(SortOptions.ALPHABETIC_ASC),
//                Optional.empty(),
//                ShowRecipePages.DISCOVER,
//                Optional.empty(),
//                Optional.of(1L),
//                Optional.of(5)
//        );
//
//        assertEquals(0, total);
//    }
//
//    @Test
//    public void testGetTotalRecipesWhenInexistentCategory(){
//        List<Integer> categories = new ArrayList<>();
//        categories.add(28);
//
//        assertThrows(DataIntegrityViolationException.class,
//                () -> recipeDao.getTotalNumberRecipesByFilterForPagination(
//                    Optional.of(AvailableDifficultiesForSort.EASY),
//                    Optional.of("Salt"),
//                    categories,
//                    Optional.of(SortOptions.ALPHABETIC_ASC),
//                    Optional.empty(),
//                    ShowRecipePages.DISCOVER,
//                    Optional.of(USER_ID),
//                    Optional.of(1L),
//                    Optional.of(5)
//                )
//        );
//    }
//
//    @Test
//    public void testGetTotalRecipesWhenInexistentUserId(){
//        List<Integer> categories = new ArrayList<>();
//        categories.add(28);
//
//        assertThrows(DataIntegrityViolationException.class,
//                () -> recipeDao.getTotalNumberRecipesByFilterForPagination(
//                        Optional.of(AvailableDifficultiesForSort.EASY),
//                        Optional.of("Salt"),
//                        categories,
//                        Optional.of(SortOptions.ALPHABETIC_ASC),
//                        Optional.empty(),
//                        ShowRecipePages.DISCOVER,
//                        Optional.of(99L),
//                        Optional.of(1L),
//                        Optional.of(5)
//                )
//        );
//    }

}
