package ar.edu.itba.paw.persistence.category;

import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.category.RecipeCategory;
import ar.edu.itba.paw.persistence.GlobalTestVariables;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.CategoryDao;
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
@Sql(scripts = { "classpath:populateCategoryDaoImplTest.sql"})
public class CategoryDaoImplTest extends GlobalTestVariables {

    @Autowired
    private DataSource ds;
    @Autowired
    private CategoryDao categoryDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testGetAllRecipeCategoriesWhenRecipeExists(){
        List<RecipeCategory> recipeCategories;

        recipeCategories = categoryDao.getAllRecipeCategories(1);
        assertEquals(2, recipeCategories.size());
    }

    @Test
    public void testGetAllRecipeCategoriesWhenRecipeNotExists(){
        List<RecipeCategory> recipeCategories;

        recipeCategories = categoryDao.getAllRecipeCategories(99);
        assertEquals(0, recipeCategories.size());
    }

    @Test
    public void testGetAllCategories(){
        List<Category> categoryList = categoryDao.getAllCategories();
        assertEquals(3,categoryList.size());
        assertEquals(CATEGORY_STR_1, categoryList.get(0).getName());
        assertEquals(CATEGORY_ID_1, categoryList.get(0).getCategoryId());
        assertEquals(CATEGORY_STR_2, categoryList.get(1).getName());
        assertEquals(CATEGORY_ID_2, categoryList.get(1).getCategoryId());
    }

    @Test
    public void testCreateRecipeCategoryWhenValid(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_recipe_category");
        assertEquals(1, categoryDao.createRecipeCategory(RECIPE_ID, CATEGORY_ID_1));
    }

    @Test
    public void testCreateRecipeCategoryWhenInvalidRecipeId(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_recipe_category");
        assertThrows(DataIntegrityViolationException.class, ()->categoryDao.createRecipeCategory(99, 1));
    }

    @Test
    public void testCreateRecipeCategoryWhenInvalidCategoryId(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_recipe_category");
        assertThrows(DataIntegrityViolationException.class, ()->categoryDao.createRecipeCategory(1, 99));
    }

    @Test
    public void testGetCategoryById() {
        Optional<Category> category = categoryDao.getCategoryById(CATEGORY_ID_1);
        assertTrue(category.isPresent());
        assertEquals(CATEGORY_STR_1, category.get().getName());
    }

    @Test
    public void testGetCategoryByIdWhenIdDoesNotExist() {
        Optional<Category> category = categoryDao.getCategoryById(0);
        assertFalse(category.isPresent());
    }

    @Test
    public void testGetAllCategoriesForRecipeFormWhenRecipeExists(){
        List<Category> categoryList;
        categoryList = categoryDao.getAllCategoriesForRecipeForm(RECIPE_ID);
        assertEquals(2, categoryList.size());
    }

    @Test
    public void testGetAllCategoriesForRecipeFormWhenRecipeNotExists(){
        List<Category> categoryList;
        categoryList = categoryDao.getAllCategoriesForRecipeForm(99L);
        assertEquals(0, categoryList.size());
    }

    @Test
    public void testDeleteCategories(){
        boolean isDeleted = categoryDao.deleteCategories(RECIPE_ID);
        assertTrue(isDeleted);
    }

}
