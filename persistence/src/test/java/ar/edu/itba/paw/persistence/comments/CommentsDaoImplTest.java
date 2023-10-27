package ar.edu.itba.paw.persistence.comments;

import ar.edu.itba.paw.models.UserComment;
import ar.edu.itba.paw.models.comments.Comment;
import ar.edu.itba.paw.models.deletion.CommentDeletionData;
import ar.edu.itba.paw.persistence.GlobalTestVariables;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.CommentsDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
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
import static org.mockito.ArgumentMatchers.eq;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@Sql(scripts = { "classpath:populateCommentsDaoImplTest.sql"})
public class CommentsDaoImplTest extends GlobalTestVariables {

    @Autowired
    private DataSource ds;

    @Autowired
    private CommentsDao commentsDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateCommentWithInvalidRecipeId() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_comment");
        long invalidRecipeId = -1;
        long userId = 1;
        String commentContent = "This is a test comment.";

        Optional<Comment> comment =
                commentsDao.createComment(invalidRecipeId, userId, commentContent);

        assertFalse(comment.isPresent());
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_comment"));
    }

    @Test
    public void testCreateCommentWithInvalidUserId() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_comment");
        long recipeId = 1L;
        long userId = 0L;
        String commentContent = "This is a comment.";

        Optional<Comment> comment = commentsDao.createComment(recipeId, userId, commentContent);

        assertFalse(comment.isPresent());
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_comment"));
    }

    @Test
    public void testCreateCommentWithValidData() {
        long recipeId = 1L;
        long userId = 2L;
        String commentContent = "This is a test comment";
        Comment comment = new Comment(recipeId, userId, 0L, commentContent);

        Optional<Comment> createdComment =
                commentsDao.createComment(recipeId, userId, commentContent);

        assertTrue(createdComment.isPresent());
        assertEquals(commentContent, createdComment.get().getCommentContent());
        assertEquals(recipeId, createdComment.get().getRecipeId());
        assertEquals(userId, createdComment.get().getUserId());
    }

    @Test
    public void testDeleteCommentWhenExists(){
        assertTrue(commentsDao.deleteComment(1));
    }

    @Test
    public void testDeleteCommentWhenNotExists(){
        assertFalse(commentsDao.deleteComment(99));
    }

    @Test
    public void testDeleteCommentWhenCommentNotExists(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_comment");
        long inexistentCommentId = 99L;
        assertFalse(commentsDao.deleteComment(inexistentCommentId));
    }

    @Test
    public void testGetCommentsById(){
        List<UserComment> comments = commentsDao.getCommentsById(1, Optional.empty(), Optional.empty());
        assertEquals(3, comments.size());
    }

    @Test
    public void testGetCommentsByIdWithCommentsToBring(){
        List<UserComment> comments2 = commentsDao.getCommentsById(1, Optional.of(1L), Optional.of(0));
        assertEquals(1, comments2.size());
    }

    @Test
    public void testGetCommentsByIdWithCommentsToBringAndPageNumber(){
        List<UserComment> comments3 = commentsDao.getCommentsById(99, Optional.of(1L), Optional.of(1));
        assertEquals(0, comments3.size());
    }

    @Test
    public void testGetRecipeCommentWhenExists(){
        Optional<Comment> comment = commentsDao.getRecipeComment(1);
        assertTrue(comment.isPresent());
    }

    @Test
    public void testGetRecipeCommentWhenNotExists(){
        Optional<Comment> comment2 = commentsDao.getRecipeComment(99);
        assertFalse(comment2.isPresent());
    }

    @Test
    public void testGetTotalCommentsWhenExists(){
        long countComments = commentsDao.getTotalComments(1);
        assertTrue(countComments > 0);
    }

    @Test
    public void testGetTotalCommentsWhenNotExists(){
        long countComments = commentsDao.getTotalComments(99);
        assertFalse(countComments > 0);
    }

    @Test
    public void testGetCountComments(){
        long userCommentCount = commentsDao.getCountComments(1L);
        assertEquals(3, userCommentCount);
    }

    @Test
    public void testGetCommentDeletionDataWhenCommentExists(){
        Optional<CommentDeletionData> maybeCommentDeletionData;

        maybeCommentDeletionData = commentsDao.getCommentDeletionData(1L);
        assertTrue(maybeCommentDeletionData.isPresent());
        assertEquals("mail@maill.com", maybeCommentDeletionData.get().getUserMail());
        assertEquals("title", maybeCommentDeletionData.get().getRecipeName());
        assertEquals("Loved the recipe", maybeCommentDeletionData.get().getCommentData());

    }

    @Test
    public void testGetCommentDeletionDataWhenInexistentCommentId(){
        Optional<CommentDeletionData> maybeCommentDeletionData;
        maybeCommentDeletionData = commentsDao.getCommentDeletionData(99L);
        assertFalse(maybeCommentDeletionData.isPresent());
    }

}