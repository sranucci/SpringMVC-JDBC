package ar.edu.itba.paw.persistence.ingredient;

import ar.edu.itba.paw.models.ingredient.Ingredient;
import ar.edu.itba.paw.models.ingredient.RecipeIngredient;
import ar.edu.itba.paw.models.ingredient.RecipeIngredientRecover;
import ar.edu.itba.paw.persistence.GlobalTestVariables;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.IngredientsDao;
import ar.edu.itba.persistenceInterface.LikesDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@Sql(scripts = { "classpath:populateIngredientDaoImplTest.sql"})
public class IngredientDaoImplTest extends GlobalTestVariables {
    @Autowired
    private DataSource ds;

    @Autowired
    private IngredientsDao ingredientsDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }
    @Test
    public void testGetAllRecipeIngredientsWhenRecipeHasIngredients(){
        List<RecipeIngredient> recipeIngredientList;
        recipeIngredientList = ingredientsDao.getAllRecipeIngredients(1);
        assertEquals(2, recipeIngredientList.size());
    }

    @Test
    public void testGetAllRecipeIngredientsWhenRecipeHasNoIngredients(){
        List<RecipeIngredient> recipeIngredientList;
        recipeIngredientList = ingredientsDao.getAllRecipeIngredients(2);
        assertEquals(0, recipeIngredientList.size());
    }

    @Test
    public void testGetAllRecipeIngredientsWhenRecipeDoesNotExist(){
        List<RecipeIngredient> recipeIngredientList;
        recipeIngredientList = ingredientsDao.getAllRecipeIngredients(99);
        assertEquals(0, recipeIngredientList.size());
    }

    @Test
    public void testGetIngredientWhenIngredientExists(){
        Optional<Ingredient> maybeIngredient;
        maybeIngredient = ingredientsDao.getIngredient(1);
        assertTrue(maybeIngredient.isPresent());
        assertEquals(1, maybeIngredient.get().getIngredientId());
    }

    @Test
    public void testGetIngredientWhenIngredientDoesNotExist(){
        Optional<Ingredient> maybeIngredient;
        maybeIngredient = ingredientsDao.getIngredient(99);
        assertFalse(maybeIngredient.isPresent());
    }

    @Test
    public void testGetAllIngredients(){
        List<Ingredient> ingredients;

        //retrieving the 2 ingredients that exist
        ingredients = ingredientsDao.getAllIngredients();
        assertEquals(2, ingredients.size());
    }

    @Test
    public void testCreateRecipeIngredientWhenRecipeExists(){
        assertEquals(1, ingredientsDao.createRecipeIngredient(2, 1, 1, 1));
    }

    @Test
    public void testCreateRecipeIngredientWhenRecipeDoesNotExist(){
        assertThrows(DataIntegrityViolationException.class, ()->ingredientsDao.createRecipeIngredient(99, 1, 1, 1));
    }

    @Test
    public void testGetIngredientIdWhenIngredientExists(){
        Optional<Long> maybeId;
        maybeId = ingredientsDao.getIngredientId("salt");
        assertTrue(maybeId.isPresent());
        assertEquals(1, maybeId.get().longValue());
    }

    @Test
    public void testGetIngredientIdWhenIngredientDoesNotExist(){
        Optional<Long> maybeId;
        maybeId = ingredientsDao.getIngredientId("inexistent");
        assertFalse(maybeId.isPresent());
    }

    @Test
    public void testGetAllRecipeIngredientsRecoverWhenRecipeExists(){
        List<RecipeIngredientRecover> list;
        list = ingredientsDao.getAllRecipeIngredientsRecover(1);
        assertEquals(2, list.size());
    }

    @Test
    public void testGetAllRecipeIngredientsRecoverWhenRecipeDoesNotExist(){
        List<RecipeIngredientRecover> list;
        list = ingredientsDao.getAllRecipeIngredientsRecover(99);
        assertEquals(0, list.size());
    }

    @Test
    public void testDeleteRecipeIngredientsWhenIngredientExists(){
        assertTrue(ingredientsDao.deleteRecipeIngredients(1));
    }

    @Test
    public void testDeleteRecipeIngredientsWhenIngredientDoesNotExist(){
        assertTrue(ingredientsDao.deleteRecipeIngredients(1));
        assertFalse(ingredientsDao.deleteRecipeIngredients(1));
    }

    @Test
    public void testDeleteRecipeIngredientsWhenRecipeDoesNotExist(){
        assertFalse(ingredientsDao.deleteRecipeIngredients(99));
    }
}
