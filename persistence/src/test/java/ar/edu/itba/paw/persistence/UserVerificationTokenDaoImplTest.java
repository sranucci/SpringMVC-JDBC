package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.UserVerificationToken;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.UserImageDao;
import ar.edu.itba.persistenceInterface.UserVerificationTokenDao;

import static org.junit.Assert.*;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@Sql(scripts = { "classpath:populateUserVerificationTokenDaoImplTest.sql"})
public class UserVerificationTokenDaoImplTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private UserVerificationTokenDao userVerificationTokenDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);

    }
    @Test
    public void testCreateToken(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_user_verification_token");
        UserVerificationToken userVerificationToken = userVerificationTokenDao.createToken(1, "7c734a3f-7e90-42f8-88e1-c031cc132961");
        assertNotNull(userVerificationToken);
        assertEquals(1, userVerificationToken.getUserId());
        assertEquals("7c734a3f-7e90-42f8-88e1-c031cc132961", userVerificationToken.getToken());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_user_verification_token"));
    }

    @Test
    public void testGetTokenWhenPresent(){
        Optional<UserVerificationToken> userVerificationTokenOptional = userVerificationTokenDao.getToken("7c734a3f-7e90-42f8-88e1-c031cc132961");
        assertTrue(userVerificationTokenOptional.isPresent());
        assertEquals("7c734a3f-7e90-42f8-88e1-c031cc132961", userVerificationTokenOptional.get().getToken());
        assertEquals(1, userVerificationTokenOptional.get().getUserId());
    }

    @Test
    public void testGetTokenWhenNotPresent(){
        Optional<UserVerificationToken> userVerificationTokenOptional = userVerificationTokenDao.getToken("invalid");
        assertFalse(userVerificationTokenOptional.isPresent());
    }

    @Test
    public void testRemoveTokenWhenExists(){
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_user_verification_token"));
        userVerificationTokenDao.removeToken("7c734a3f-7e90-42f8-88e1-c031cc132961");
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_user_verification_token"));
    }

    @Test
    public void testRemoveTokenWhenNotExists(){
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_user_verification_token"));
        userVerificationTokenDao.removeToken("invalid");
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_user_verification_token"));
    }
}
