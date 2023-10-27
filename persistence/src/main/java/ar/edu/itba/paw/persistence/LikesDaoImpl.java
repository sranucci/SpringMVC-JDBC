package ar.edu.itba.paw.persistence;

import ar.edu.itba.persistenceInterface.LikesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class LikesDaoImpl implements LikesDao {
    private final static RowMapper<Integer> IS_LIKED_MAPPER = (rs, rowNum) -> rs.getInt("likesCount");
    private final static RowMapper<Long> LIKES_MAPPER = (rs, rowNum) -> rs.getLong("likesCount");
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public LikesDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.simpleJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("tbl_like_dislike")
                .usingColumns("recipe_id", "user_id", "is_like");
    }

    @Override
    public Boolean isRecipeLikedById(long userId, long recipeId) {
        long isLiked = jdbcTemplate.query("SELECT coalesce(count(*), 0) likesCount FROM tbl_like_dislike l WHERE l.user_id = ? AND l.recipe_id = ? AND l.is_like = true", IS_LIKED_MAPPER, userId, recipeId).stream().findFirst().get();
        return isLiked != 0;
    }

    @Override
    public Boolean isRecipeDislikedById(long userId, long recipeId) {
        long isDisLiked = jdbcTemplate.query("SELECT coalesce(count(*), 0) likesCount FROM tbl_like_dislike l WHERE l.user_id = ? AND l.recipe_id = ? AND l.is_like = false", IS_LIKED_MAPPER, userId, recipeId).stream().findFirst().get();
        return isDisLiked != 0;
    }

    private int addRating(long userId, long recipeId, boolean isLike) { //parametrization of makeRecipeLiked and makeRecipeDisliked
        removeRecipeRating(userId, recipeId);
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("recipe_id", recipeId);
        data.put("is_like", isLike);
        return simpleJdbcInsert.execute(data);
    }

    @Override
    public int makeRecipeLiked(long userId, long recipeId) {
        return addRating(userId, recipeId, true);
    }

    @Override
    public int makeRecipeDisliked(long userId, long recipeId) {
        return addRating(userId, recipeId, false);
    }

    @Override
    public int removeRecipeRating(long userId, long recipeId) {
        String sql = "DELETE FROM tbl_like_dislike WHERE user_id = ? AND recipe_id = ?";
        return jdbcTemplate.update(sql, userId, recipeId);
    }

    @Override
    public long getLikes(long userId) {
        String sql = "SELECT COUNT(*) AS likesCount " +
                "FROM tbl_like_dislike l " +
                "JOIN tbl_recipe r ON l.recipe_id = r.recipe_id " +
                "WHERE l.is_like = true AND r.user_id = ? ";

        return jdbcTemplate.query(sql, LIKES_MAPPER, userId).stream().findFirst().orElse(0L);
    }
}
