
package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.UserImageDao;
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

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@Sql(scripts = { "classpath:populateUserImageDaoImplTest.sql"})
public class UserImageDaoImplTest extends GlobalTestVariables {

    @Autowired
    private DataSource ds;

    @Autowired
    private UserImageDao userImageDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);

        this.jdbcInsert =
                new SimpleJdbcInsert(ds)
                        .withTableName("tbl_user_photo");
    }

    @Test
    public void testUploadUserPhoto() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_user_photo");
        userImageDao.uploadUserPhoto(USER_ID,  IMG_BYTEA);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_user_photo"));
    }

    @Test
    public void testUploadUserPhotoWhenUserAlreadyHasAPhoto() {
        userImageDao.uploadUserPhoto(USER_ID,  IMG_BYTEA);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_user_photo"));
    }


    @Test
    public void testFindUserPhotoByUserId(){
        Optional<byte[]> photoData = userImageDao.findUserPhotoByUserId(USER_ID);
        byte[] expectedPhotoData = new byte[]{0x00};

        assertTrue(photoData.isPresent());
        assertArrayEquals(expectedPhotoData, photoData.get());
    }

    @Test
    public void testFindUserPhotoByUserIdWhenUserIdDoesNotExist(){
        Optional<byte[]> photoData = userImageDao.findUserPhotoByUserId(0);
        assertFalse(photoData.isPresent());
    }


    @Test
    public void testDeleteUserPhoto(){
        int rowsAffected = userImageDao.deleteUserPhoto(USER_ID);
        assertEquals(1, rowsAffected);
    }

    @Test
    public void testDeleteUserPhotoWhenUserIdDoesNotExist(){
        int rowsAffected = userImageDao.deleteUserPhoto(0);
        assertEquals(0, rowsAffected);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_user_photo"));

    }





}
