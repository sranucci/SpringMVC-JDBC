package ar.edu.itba.paw.persistence;

import ar.edu.itba.persistenceInterface.SavedRecipesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SavedRecipesDaoImpl implements SavedRecipesDao {
    private final static RowMapper<Integer> IS_SAVED_MAPPER = (rs, rowNum) -> rs.getInt("entries");
    private final JdbcTemplate jdbcTemplate; //reduce el boilerplate
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public SavedRecipesDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("tbl_saved_by_user")
                .usingColumns("recipe_id", "user_id");
    }

    @Override
    public Boolean isRecipeSavedByUser(long userId, long recipeId) {
        int isSaved = jdbcTemplate.query("SELECT coalesce(count(*), 0) AS entries FROM tbl_saved_by_user s WHERE s.user_id = ? AND s.recipe_id = ?", IS_SAVED_MAPPER, userId, recipeId).stream().findFirst().get();
        return isSaved != 0;
    }

    @Override
    public int saveRecipe(long userId, long recipeId) {
        deleteSavedRecipe(userId, recipeId);
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("recipe_id", recipeId);
        return jdbcInsert.execute(data);
    }

    @Override
    public int deleteSavedRecipe(long userId, long recipeId) {
        String sql = "DELETE FROM tbl_saved_by_user WHERE user_id = ? AND recipe_id = ?";
        return jdbcTemplate.update(sql, userId, recipeId);
    }

}
