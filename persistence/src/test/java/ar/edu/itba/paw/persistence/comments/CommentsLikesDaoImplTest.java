package ar.edu.itba.paw.persistence.comments;

import ar.edu.itba.paw.models.comments.CommentLikeData;
import ar.edu.itba.paw.persistence.GlobalTestVariables;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.CommentsLikesDao;
import ar.edu.itba.persistenceInterface.LikesDao;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@Sql(scripts = { "classpath:populateCommentsLikesDaoImplTest.sql"})
public class CommentsLikesDaoImplTest extends GlobalTestVariables {
    @Autowired
    private DataSource ds;

    @Autowired
    private CommentsLikesDao clDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testModifyCommentUserWhenCommentsExist(){
        assertFalse(clDao.modifyCommentUser(99, 1, true));
        assertFalse(clDao.modifyCommentUser(1, 99, false));
    }

    @Test
    public void testModifyCommentUserWhenCommentsDontExist(){
        assertFalse(clDao.modifyCommentUser(99, 1, true));
        assertFalse(clDao.modifyCommentUser(1, 99, false));
    }

    @Test
    public void testGetCommentUserWhenCommentIsDisliked(){
        Optional<CommentLikeData> clData;
        //the comment exists and is disliked
        clData = clDao.getCommentUser(1, 1);
        assertTrue(clData.isPresent());
        assertEquals(1, clData.get().getUserId());
        assertEquals(1, clData.get().getCommentId());
        assertTrue(clData.get().isLiked());

    }

    @Test
    public void testGetCommentUserWhenCommentIsLiked() {
        Optional<CommentLikeData> clData;
        //the comment exists and is liked
        clData = clDao.getCommentUser(1, 2);
        assertTrue(clData.isPresent());
        assertEquals(2, clData.get().getUserId());
        assertEquals(1, clData.get().getCommentId());
        assertFalse(clData.get().isLiked());
    }

    @Test
    public void testGetCommentUserWhenCommentNotExists() {
        Optional<CommentLikeData> clData;
        //the comment doesn't exist
        clData = clDao.getCommentUser(99, 1);
        assertFalse(clData.isPresent());
    }

    @Test
    public void testGetCommentUserWhenUserNotExistsOrDidntRate() {
        Optional<CommentLikeData> clData;
        //the user doesn't exist or hasn't rated the comment
        clData = clDao.getCommentUser(1, 99);
        assertFalse(clData.isPresent());
    }

    @Test
    public void testDeleteUserLikeEntryWhenCommentsExist(){
        //existing comment ratings are deleted
        assertEquals(1, clDao.deleteUserLikeEntry(1, 1));
        assertEquals(1, clDao.deleteUserLikeEntry(1, 2));
    }

    @Test
    public void testDeleteUserLikeEntryWhenCommentsDontExist(){
        //not existing comment rating are trying to be deleted
        assertEquals(0, clDao.deleteUserLikeEntry(99, 1));
        assertEquals(0, clDao.deleteUserLikeEntry(1, 99));
    }

    @Test
    public void testCreateLikeStatusWhenCommentAlreadyRated(){
        //trying to rate a comment that is already rated
        assertThrows(DuplicateKeyException.class, ()->clDao.createLikeStatus(1, 1, true));
        assertThrows(DuplicateKeyException.class, ()->clDao.createLikeStatus(1, 1, false));
        assertThrows(DuplicateKeyException.class, ()->clDao.createLikeStatus(1, 2, true));
    }

    @Test
    public void testCreateLikeStatusWhenCommentNotRated(){
        //trying to rate a comment that isn't rated yet
        assertEquals(1, clDao.createLikeStatus(1, 3, true));
    }
}
