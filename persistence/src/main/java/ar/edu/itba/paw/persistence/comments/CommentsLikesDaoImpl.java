package ar.edu.itba.paw.persistence.comments;


import ar.edu.itba.paw.models.comments.CommentLikeData;
import ar.edu.itba.persistenceInterface.CommentsLikesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class CommentsLikesDaoImpl implements CommentsLikesDao {


    private final static RowMapper<CommentLikeData> COMMENT_LIKE_DATA_ROW_MAPPER =
            (rs, rno) -> new CommentLikeData(rs.getLong("comment_id"), rs.getLong("user_id"), rs.getBoolean("is_like"));


    private final JdbcTemplate template;
    private final SimpleJdbcInsert insert;

    @Autowired
    public CommentsLikesDaoImpl(DataSource ds) {
        this.template = new JdbcTemplate(ds);
        this.insert = new SimpleJdbcInsert(ds)
                .withTableName("tbl_comment_like_dislike");
    }


    @Override
    public boolean modifyCommentUser(long commentId, long userId, boolean liked) {
        return template.update("UPDATE tbl_comment_like_dislike SET is_like = ? WHERE comment_id = ? AND user_id = ?", liked, commentId, userId) > 0;
    }


    public Optional<CommentLikeData> getCommentUser(long commentId, long userId) {
        return template.query("SELECT * FROM tbl_comment_like_dislike WHERE user_id = ? AND comment_id = ?", COMMENT_LIKE_DATA_ROW_MAPPER, userId, commentId)
                .stream().findFirst();
    }

    public long deleteUserLikeEntry(long commentId, long userId) {
        return template.update("DELETE FROM tbl_comment_like_dislike WHERE comment_id = ? AND user_id = ?", commentId, userId);
    }


    public int createLikeStatus(long commentId, long userId, boolean status) {
        Map<String, Object> m = new HashMap<>();
        m.put("is_like", status);
        m.put("comment_id", commentId);
        m.put("user_id", userId);
        return insert.execute(m);
    }


}
