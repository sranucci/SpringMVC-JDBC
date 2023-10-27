package ar.edu.itba.paw.persistence.category;

import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.category.RecipeCategory;
import ar.edu.itba.persistenceInterface.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CategoryDaoImpl implements CategoryDao {

    private static final RowMapper<Category> CATEGORYMAPPER = ((rs, rnum) -> new Category(rs.getLong("category_id"), rs.getString("category_name")));
    private static final RowMapper<RecipeCategory> RECIPECATEGORYMAPPER = ((rs, rnum) -> new RecipeCategory(rs.getLong("recipe_id"), rs.getLong("category_id")));
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertCategoryToRecipe;

    @Autowired
    public CategoryDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsertCategoryToRecipe = new SimpleJdbcInsert(ds)
                .withTableName("tbl_recipe_category");
    }


    @Override
    public List<RecipeCategory> getAllRecipeCategories(long recipeId) {
        return jdbcTemplate.query("SELECT * FROM " +
                "(SELECT * FROM tbl_recipe_category WHERE recipe_id = ?) as recipe_data " +
                "NATURAL JOIN tbl_category", RECIPECATEGORYMAPPER, recipeId);
    }

    @Override
    public List<Category> getAllCategories() {
        return jdbcTemplate.query("SELECT * FROM tbl_category", CATEGORYMAPPER);
    }

    @Override
    public long createRecipeCategory(long recipeId, long categoryId) {
        Map<String, Object> data = new HashMap<>();
        data.put("recipe_id", recipeId);
        data.put("category_id", categoryId);
        return jdbcInsertCategoryToRecipe.execute(data);
    }

    @Override
    public Optional<Category> getCategoryById(long category_id) {
        return jdbcTemplate.query("SELECT * FROM tbl_category WHERE category_id = ?", CATEGORYMAPPER, category_id).stream().findFirst();
    }

    @Override
    public List<Category> getAllCategoriesForRecipeForm(long recipeId) {
        return jdbcTemplate.query("SELECT * FROM tbl_category JOIN tbl_recipe_category ON tbl_category.category_id = tbl_recipe_category.category_id WHERE recipe_id = ?", CATEGORYMAPPER, recipeId);
    }

    @Override
    public boolean deleteCategories(long recipeId) {
        return jdbcTemplate.update("DELETE FROM tbl_recipe_category WHERE recipe_id = ?",recipeId) > 0;
    }

}
