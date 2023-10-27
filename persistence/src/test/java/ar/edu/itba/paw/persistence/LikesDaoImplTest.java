package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.LikesDao;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@Sql(scripts = { "classpath:populateLikesDaoImplTest.sql"})
public class LikesDaoImplTest  extends GlobalTestVariables {

    @Autowired
    private DataSource ds;

    @Autowired
    private LikesDao likesDao;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }
    @Test
    public void testIsRecipeLikedByIdWhenTrue() {
        assertTrue(likesDao.isRecipeLikedById(1, 1));
    }

    @Test
    public void testIsRecipeLikedByIdWhenFalse() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_like_dislike");
        assertFalse(likesDao.isRecipeLikedById(1, 1));
    }

    @Test
    public void testIsRecipeDislikedByIdWhenTrue() {
        assertTrue(likesDao.isRecipeDislikedById(2, 1));
    }

    @Test
    public void testIsRecipeDislikedByIdWhenFalse() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_like_dislike");
        assertFalse(likesDao.isRecipeDislikedById(2, 1));
    }

    @Test
    public void testIsRecipeLiked(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_like_dislike");
        assertEquals(1, likesDao.makeRecipeLiked(1L, 1L));
        assertEquals(1, likesDao.makeRecipeLiked(1L, 1L));
    }

    @Test
    public void testIsRecipeDisliked(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_like_dislike");
        assertEquals(1, likesDao.makeRecipeDisliked(1L, 1L));
        assertEquals(1, likesDao.makeRecipeDisliked(1L, 1L));
    }

    @Test
    public void testRemoveRecipeRatingWhenExists(){
        assertEquals(1, likesDao.removeRecipeRating(1L, 1L));
        assertEquals(1, likesDao.removeRecipeRating(2L, 1L));
    }

    @Test
    public void testRemoveRecipeRatingWhenDoesNotExist(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_like_dislike");
        assertEquals(0, likesDao.removeRecipeRating(1L, 1L));
        assertEquals(0, likesDao.removeRecipeRating(2L, 1L));
    }

    @Test
    public void testGetLikesWhenUserExists(){
        assertEquals(1, likesDao.getLikes(1L));
    }

    @Test
    public void testGetLikesWhenUserDoesNotExist(){
        assertEquals(0, likesDao.getLikes(99L));
    }
}
