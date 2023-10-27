package ar.edu.itba.paw.persistence.comments;

import ar.edu.itba.paw.enums.UserInteraction;
import ar.edu.itba.paw.models.UserComment;
import ar.edu.itba.paw.models.comments.Comment;
import ar.edu.itba.paw.models.deletion.CommentDeletionData;
import ar.edu.itba.persistenceInterface.CommentsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.*;

@Repository
public class CommentsDaoImpl implements CommentsDao {

    private final static RowMapper<UserComment> NO_USER_COMMENT_ROW_MAPPER = (rs, rowNum) -> new UserComment(rs.getLong("comment_id"), rs.getString("name"), rs.getString("last_name"), rs.getString("comment_content"), rs.getTimestamp("created_at").toInstant(), rs.getLong("user_id")
            , rs.getLong("count_dislikes"), rs.getLong("count_likes"), UserInteraction.NO_INTERACTION);

    private final static RowMapper<UserComment> USER_INTERACTION_COMMENT_ROW_MAPPER = (rs, rowNum) -> new UserComment(rs.getLong("comment_id"), rs.getString("name"), rs.getString("last_name"), rs.getString("comment_content"), rs.getTimestamp("created_at").toInstant(), rs.getLong("user_id")
            , rs.getLong("count_dislikes"), rs.getLong("count_likes"), UserInteraction.getInteraction(rs.getString("interaction_data")));

    private final static RowMapper<Comment> COMMENT_MAPPER = (rs, rowNum) -> new Comment(rs.getLong("recipe_id"), rs.getLong("user_id"), rs.getLong("comment_id"), rs.getString("comment_content"));

    private final static RowMapper<Long> COUNT_MAPPER = (rs, rowNum) -> rs.getLong("total_comments");

    private final static RowMapper<CommentDeletionData> COMMENT_DELETION_DATA_ROW_MAPPER = (rs, rowNum) -> new CommentDeletionData(rs.getString("email"), rs.getString("title"), rs.getString("comment_content"));
    private final SimpleJdbcInsert insert;
    private final JdbcTemplate template;


    @Autowired
    public CommentsDaoImpl(final DataSource ds) {
        this.template = new JdbcTemplate(ds);
        this.insert = new SimpleJdbcInsert(ds)
                .withTableName("tbl_comment")
                .usingGeneratedKeyColumns("comment_id");
    }


    @Override
    public Optional<Comment> createComment(long recipeId, long userId, String commentContent) {
        Map<String, Object> map = new HashMap<>();
        map.put("recipe_id", recipeId);
        map.put("user_id", userId);
        map.put("comment_content", commentContent);
        map.put("created_at", Date.from(Instant.now()));
        try {
            final Number key = insert.execute(map);
            return Optional.of(new Comment(recipeId, userId, key.longValue(), commentContent));
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    @Override
    public List<UserComment> getCommentsById(long recipeId, Optional<Long> commentsToBring, Optional<Integer> pageNumber) {
        return template.query("SELECT tbl_user.name, last_name, comment_content, tbl_comment.user_id, tbl_comment.created_at, tbl_comment.comment_id," +
                        "SUM(CASE WHEN is_like = TRUE THEN 1 ELSE 0 END ) AS count_likes," +
                        "SUM(CASE WHEN is_like = FALSE THEN 1 ELSE 0 END ) AS count_dislikes " +
                        "FROM tbl_comment_like_dislike RIGHT JOIN tbl_comment ON tbl_comment_like_dislike.comment_id = tbl_comment.comment_id  INNER JOIN tbl_user ON tbl_user.user_id = tbl_comment.user_id WHERE recipe_id = ? " +
                        "GROUP BY tbl_comment.comment_id, name, last_name, comment_content, tbl_comment.created_at " +
                        "ORDER BY tbl_comment.created_at DESC " +
                        "LIMIT ? OFFSET ?"
                , NO_USER_COMMENT_ROW_MAPPER, recipeId, commentsToBring.orElse(10L), pageNumber.orElse(0) * commentsToBring.orElse(10L));
    }


    public List<UserComment> getCommentsWithUserInteractionStatus(long recipeId, long userId, Optional<Long> commentsToBring, Optional<Integer> pageNumber) {
        return template.query("SELECT tbl_user.name, last_name, comment_content, tbl_comment.user_id, tbl_comment.created_at, tbl_comment.comment_id, " +
                        "SUM(CASE WHEN is_like = TRUE THEN 1 ELSE 0 END ) AS count_likes, " +
                        "SUM(CASE WHEN is_like = FALSE THEN 1 ELSE 0 END ) AS count_dislikes, " +
                        "CASE WHEN ? IN (SELECT tbl_comment_like_dislike.user_id FROM tbl_comment_like_dislike WHERE tbl_comment.comment_id = tbl_comment_like_dislike.comment_id AND tbl_comment_like_dislike.is_like = TRUE) THEN 'LIKE' " +
                        "WHEN ? IN ( SELECT tbl_comment_like_dislike.user_id FROM tbl_comment_like_dislike WHERE tbl_comment.comment_id = tbl_comment_like_dislike.comment_id AND  tbl_comment_like_dislike.is_like = FALSE) THEN 'DISLIKE' " +
                        "ELSE 'NO_INTERACTION' END as interaction_data " +
                        "FROM tbl_comment_like_dislike RIGHT JOIN tbl_comment ON tbl_comment_like_dislike.comment_id = tbl_comment.comment_id  INNER JOIN tbl_user ON tbl_user.user_id = tbl_comment.user_id WHERE recipe_id = ? " +
                        "GROUP BY tbl_comment.comment_id, name, last_name, comment_content, tbl_comment.created_at " +
                        "ORDER BY tbl_comment.created_at DESC " +
                        "LIMIT ? OFFSET ?"
                , USER_INTERACTION_COMMENT_ROW_MAPPER, userId, userId, recipeId, commentsToBring.orElse(10L), pageNumber.orElse(0) * commentsToBring.orElse(10L));
    }

    public long getTotalComments(long recipeId) {
        return template.query(
                "SELECT COALESCE(COUNT(*), 0) AS total_comments " +
                "FROM tbl_comment WHERE tbl_comment.recipe_id = ?",
                COUNT_MAPPER,
                recipeId
        ).stream().findFirst().orElse(0L);
    }

    @Override
    public Optional<CommentDeletionData> getCommentDeletionData(long commentId) {
        return template.query("SELECT * FROM (SELECT * FROM tbl_comment JOIN tbl_user ON tbl_user.user_id = tbl_comment.user_id WHERE comment_id = ?) as t1 " +
                "JOIN tbl_recipe ON tbl_recipe.recipe_id = t1.recipe_id ", COMMENT_DELETION_DATA_ROW_MAPPER, commentId).stream().findFirst();
    }

    @Override
    public boolean deleteComment(long commentId) {
        int rowsAffected = template.update("DELETE FROM tbl_comment WHERE comment_id = ?", commentId);
        return rowsAffected > 0;
    }


    @Override
    public Optional<Comment> getRecipeComment(long commentId) {
        return template.query("SELECT * FROM tbl_comment WHERE comment_id = ?", COMMENT_MAPPER, commentId).stream().findFirst();
    }

    @Override
    public long getCountComments(long userId) {
        Long commentCount = template.queryForObject(
                "SELECT SUM(comments) " +
                        "FROM (SELECT COUNT(tc.comment_id) as comments, tu.user_id " +
                        "FROM tbl_recipe tr LEFT JOIN tbl_comment tc on tr.recipe_id = tc.recipe_id RIGHT JOIN tbl_user tu on tr.user_id = tu.user_id " +
                        "GROUP BY tr.recipe_id, tu.user_id) as filter_comment " +
                        "WHERE user_id = ? " +
                        "GROUP BY user_id", new Object[]{userId}, Long.class);
        return commentCount;
    }

}
