package ar.edu.itba.paw.persistence.ingredient;

import ar.edu.itba.paw.models.ingredient.Ingredient;
import ar.edu.itba.paw.models.ingredient.RecipeIngredient;
import ar.edu.itba.paw.models.ingredient.RecipeIngredientRecover;
import ar.edu.itba.persistenceInterface.IngredientsDao;
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
public class IngredientDaoImpl implements IngredientsDao {
    private static final RowMapper<Long> INGREDIENTIDMAPPER = (rs, rnum) -> rs.getLong("ingredient_id");
    private static final RowMapper<Ingredient> INGREDIENTMAPPER = (rs, rnum) -> new Ingredient(rs.getLong("ingredient_id"), rs.getString("ingredient_name"));
    private static final RowMapper<RecipeIngredient> RECIPEINGREDIENTMAPPER = (rs, rnum) -> new RecipeIngredient(rs.getString("ingredient_name"), rs.getString("unit_name"), rs.getFloat("quantity"));
    private static final RowMapper<RecipeIngredientRecover> RECIPE_INGREDIENT_RECOVER_ROW_MAPPER =
            (rs, rnum) -> new RecipeIngredientRecover(rs.getString("ingredient_name"), rs.getLong("unit_id"), rs.getFloat("quantity"));
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsertRecipeToIngredient;

    @Autowired
    public IngredientDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsertRecipeToIngredient = new SimpleJdbcInsert(ds)
                .withTableName("tbl_recipe_ingredient");

    }


    @Override
    public Optional<Ingredient> getIngredient(long ingredientId) {
        return jdbcTemplate.query("SELECT * FROM tbl_ingredient WHERE ingredient_id = ?", INGREDIENTMAPPER, ingredientId)
                .stream().findFirst();
    }

    @Override
    public List<Ingredient> getAllIngredients() {
        return jdbcTemplate.query("SELECT * FROM tbl_ingredient", INGREDIENTMAPPER);
    }

    @Override
    public List<RecipeIngredient> getAllRecipeIngredients(long recipeId) {
        return jdbcTemplate.query("SELECT * FROM " +
                "(SELECT * FROM tbl_recipe_ingredient WHERE recipe_id = ?) as recipe_data " +
                "NATURAL JOIN tbl_units NATURAL JOIN tbl_ingredient", RECIPEINGREDIENTMAPPER, recipeId);
    }

    @Override
    public int createRecipeIngredient(long recipeId, long ingredientId, float qty, long unitId) {
        Map<String, Object> data = new HashMap<>();
        data.put("recipe_id", recipeId);
        data.put("ingredient_id", ingredientId);
        data.put("unit_id", unitId);
        data.put("quantity", qty);
        return jdbcInsertRecipeToIngredient.execute(data);
    }

    @Override
    public Optional<Long> getIngredientId(String ingredientName) {
        return jdbcTemplate.query("SELECT * FROM tbl_ingredient WHERE ingredient_name = ? ", INGREDIENTIDMAPPER, ingredientName).stream().findFirst();
    }

    @Override
    public List<RecipeIngredientRecover> getAllRecipeIngredientsRecover(long recipeId) {
        return jdbcTemplate.query("SELECT * FROM tbl_recipe_ingredient NATURAL JOIN tbl_ingredient WHERE recipe_id = ?", RECIPE_INGREDIENT_RECOVER_ROW_MAPPER, recipeId);
    }

    @Override
    public boolean deleteRecipeIngredients(long recipeId) {
        return jdbcTemplate.update("DELETE FROM tbl_recipe_ingredient WHERE recipe_id = ?", recipeId) > 0;
    }

}
