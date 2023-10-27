
package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.recipe.RecipeImage;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.RecipeImageDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@Sql(scripts = { "classpath:recipeImageDaoImplTest.sql"})
public class RecipeImageDaoImplTest extends GlobalTestVariables {

    @Autowired
    private DataSource ds;

    @Autowired
    private RecipeImageDao recipeImageDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateImage(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_recipe_photo");

        recipeImageDao.createImage(RECIPE_ID, IMG_BYTEA, true);

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_recipe_photo"));
    }

    @Test
    public void testGetImage(){
        Optional<RecipeImage> ri = recipeImageDao.getImage(RECIPE_ID);
        assertTrue(ri.isPresent());
    }
    @Test
    public void testGetImageWhenRecipeIdDoesNotExist(){
        Optional<RecipeImage> ri = recipeImageDao.getImage(0);
        assertFalse(ri.isPresent());
    }

    @Test
    public void testGetImagesForRecipe(){
        List<Long> imagesIdList = recipeImageDao.getImagesForRecipe(RECIPE_ID);
        assertEquals(1, imagesIdList.size());
    }

    @Test
    public void testGetImagesForRecipeWhenRecipeIdDoesNotExist(){
        List<Long> imagesIdList = recipeImageDao.getImagesForRecipe(0);
        assertEquals(0, imagesIdList.size());
    }

    @Test
    public void testRemoveImages(){
        boolean isDeleted = recipeImageDao.removeImages(RECIPE_ID);
        assertTrue(isDeleted);
    }
    @Test
    public void testRemoveImagesWhenRecipeDoesNotExist(){
        boolean isDeleted = recipeImageDao.removeImages(0);
        assertFalse(isDeleted);
    }

}
