package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.UserDao;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@Sql(scripts = { "classpath:populateUserDaoImplTest.sql"})
public class UserDaoImplTest extends GlobalTestVariables {


    @Autowired
    private DataSource ds;

    @Autowired
    private UserDao userDao;
    private JdbcTemplate jdbcTemplate;



    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_user");

        Optional<User> user = userDao.create(EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, IS_ADMIN, true);

        assertTrue(user.isPresent());
        assertEquals(EMAIL, user.get().getEmail());
        assertEquals(PASSWORD, user.get().getPassword());
        assertEquals(FIRST_NAME, user.get().getName());
        assertEquals(LAST_NAME, user.get().getLastname());
        assertEquals(IS_ADMIN, user.get().isAdmin());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_user"));

    }
    @Test
    public void testCreateUserAlreadyExists() {
        Optional<User> user1 = userDao.create(EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, IS_ADMIN, true);
        Optional<User> user2 = userDao.create(EMAIL, PASSWORD, FIRST_NAME, LAST_NAME, IS_ADMIN, true);
        assertFalse(user2.isPresent());
    }


    @Test
    public void testFindByIdWhenIdIsFound() { // Inserting a user with ID 1 into the database

        Optional<User> user = userDao.findById(USER_ID);

        assertTrue(user.isPresent());
        assertEquals(USER_ID, user.get().getId());
        assertEquals(EMAIL, user.get().getEmail());
        assertEquals(PASSWORD, user.get().getPassword());
        assertEquals(FIRST_NAME, user.get().getName());
        assertEquals(LAST_NAME, user.get().getLastname());
        assertEquals(IS_ADMIN, user.get().isAdmin());
    }

    @Test
    public void testFindByIdWhenIdIsNotFound() {
        Optional<User> user = userDao.findById(NON_EXISTENT_USER_ID);

        assertFalse(user.isPresent());
    }

    @Test
    public void testFindByRecipeId() {
        Optional<User> user = userDao.findByRecipeId(1L);
        assertTrue(user.isPresent());
        assertEquals(USER_ID, user.get().getId());
        assertEquals(EMAIL, user.get().getEmail());
        assertEquals(PASSWORD, user.get().getPassword());
        assertEquals(FIRST_NAME, user.get().getName());
        assertEquals(LAST_NAME, user.get().getLastname());
        assertEquals(IS_ADMIN, user.get().isAdmin());
    }


    @Test
    public void testUpdateNameById() {

        int rowsAffected = userDao.updateNameById(USER_ID, FIRST_NAME2, LAST_NAME2);

        //Optional<User> updatedUser = userDao.findById(USER_ID);

        assertEquals(1, rowsAffected);
        //assertEquals(FIRST_NAME2, updatedUser.get().getName());
        //assertEquals(LAST_NAME2, updatedUser.get().getLastname());

    }

    @Test
    public void testUpdatePasswordById() {

        int rowsAffected = userDao.updatePasswordById(USER_ID, PASSWORD2);
        assertEquals(1, rowsAffected);
    }


    @Test
    public void testDeleteById() {
        int rowsAffected = userDao.deleteById(USER_ID);
        assertEquals(1, rowsAffected);
    }



    @Test
    public void testFindByEmail(){

        Optional<User> user = userDao.findByEmail(EMAIL);

        assertTrue(user.isPresent());
        assertEquals(USER_ID, user.get().getId());
        assertEquals(EMAIL, user.get().getEmail());
        assertEquals(PASSWORD, user.get().getPassword());
        assertEquals(FIRST_NAME, user.get().getName());
        assertEquals(LAST_NAME, user.get().getLastname());
        assertEquals(IS_ADMIN, user.get().isAdmin());
    }

    @Test
    public void testFindByEmailWhenUserDoesNotExist(){

        Optional<User> user = userDao.findByEmail("nonExistingEmail");

        assertFalse(user.isPresent());
    }

}
