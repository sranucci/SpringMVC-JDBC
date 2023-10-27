package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.ResetPasswordToken;
import ar.edu.itba.paw.models.UserVerificationToken;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.ResetPasswordTokenDao;
import ar.edu.itba.persistenceInterface.UserVerificationTokenDao;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@Sql(scripts = { "classpath:populateResetPasswordTokenDaoImplTest.sql"})
public class ResetPasswordTokenDaoImplTest {

    @Autowired
    private DataSource ds;

    @Autowired
    private ResetPasswordTokenDao resetPasswordTokenDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreateToken(){
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "tbl_reset_password_token");
        ResetPasswordToken resetPasswordToken = resetPasswordTokenDao.createToken(1, "7c734a3f-7e90-42f8-88e1-c031cc132961", LocalDateTime.now());
        assertNotNull(resetPasswordToken);
        assertEquals(1, resetPasswordToken.getUserId().longValue());
        assertEquals("7c734a3f-7e90-42f8-88e1-c031cc132961", resetPasswordToken.getToken());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_reset_password_token"));
    }

    @Test
    public void testGetTokenWhenPresent(){
        Optional<ResetPasswordToken> resetPasswordToken = resetPasswordTokenDao.getToken("7c734a3f-7e90-42f8-88e1-c031cc132961");
        assertTrue(resetPasswordToken.isPresent());
        assertEquals("7c734a3f-7e90-42f8-88e1-c031cc132961", resetPasswordToken.get().getToken());
        assertEquals(1, resetPasswordToken.get().getUserId().longValue());
    }

    @Test
    public void testGetTokenWhenNotPresent(){
        Optional<ResetPasswordToken> resetPasswordToken = resetPasswordTokenDao.getToken("invalid");
        assertFalse(resetPasswordToken.isPresent());
    }

    @Test
    public void testRemoveTokenWhenExists(){
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_reset_password_token"));
        resetPasswordTokenDao.removeToken("7c734a3f-7e90-42f8-88e1-c031cc132961");
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_reset_password_token"));
    }

    @Test
    public void testRemoveTokenWhenNotExists(){
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_reset_password_token"));
        resetPasswordTokenDao.removeToken("invalid");
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "tbl_reset_password_token"));
    }
}
