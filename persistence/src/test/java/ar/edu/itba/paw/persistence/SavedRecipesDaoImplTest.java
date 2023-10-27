
package ar.edu.itba.paw.persistence;
import ar.edu.itba.paw.persistence.config.TestConfig;
import ar.edu.itba.persistenceInterface.SavedRecipesDao;
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
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
@Sql(scripts = { "classpath:savedRecipesDaoImplTest.sql"})
public class SavedRecipesDaoImplTest extends GlobalTestVariables {

    @Autowired
    private DataSource ds;

    @Autowired
    private SavedRecipesDao savedRecipesDao;
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(ds);

        this.jdbcInsert =
                new SimpleJdbcInsert(ds)
                        .withTableName("tbl_saved_by_user");
    }
    @Test
    public void testIsRecipeSavedByUser() {
        boolean isSaved = savedRecipesDao.isRecipeSavedByUser(USER_ID, RECIPE_ID);
        assertTrue(isSaved);
    }

    @Test
    public void testSaveRecipe() {
        int rowsAffected = savedRecipesDao.saveRecipe(USER_ID, RECIPE_ID);
        assertEquals(1, rowsAffected);
    }
    @Test
    public void testDeleteSavedRecipe() {
        int rowsAffected = savedRecipesDao.deleteSavedRecipe(USER_ID, RECIPE_ID);
        assertEquals(1, rowsAffected);
    }




}
