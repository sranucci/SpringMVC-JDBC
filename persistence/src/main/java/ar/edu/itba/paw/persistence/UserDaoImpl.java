package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserWPhoto;
import ar.edu.itba.persistenceInterface.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    //creo una lambda static para no tener que generarla cada vez que llamo a findById
    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("user_id"), rs.getString("email"), rs.getString("password"), rs.getString("name"), rs.getString("last_name"), rs.getBoolean("is_admin"), rs.getBoolean("is_verified"));
    private final static RowMapper<UserWPhoto> USER_W_PHOTO_MAPPER = (rs, rowNum) -> new UserWPhoto(rs.getLong("user_id"), rs.getString("email"), rs.getString("password"), rs.getString("name"), rs.getString("last_name"), rs.getBoolean("is_admin"), rs.getBoolean("is_verified"), Optional.of(rs.getBytes("photo_data")));
    private final JdbcTemplate jdbcTemplate; //reduce el boilerplate
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public UserDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("tbl_user")
                .usingGeneratedKeyColumns("user_id");
    }

    @Override
    public Optional<User> create(String email, String password, String name, String lastname, boolean isAdmin, boolean isVerified) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);
        data.put("name", name);
        data.put("last_name", lastname);
        data.put("created_at", Date.from(Instant.now()));
        data.put("is_admin", isAdmin);
        data.put("is_verified", isVerified);
        try {
            final Number key = jdbcInsert.executeAndReturnKey(data); //genero INSERT INTO users (email, password) VALUES (?, ?)
            return Optional.of(new User(key.longValue(), email, password, name, lastname, isAdmin, isVerified));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> verifyUser(long id) {
        String query = "UPDATE tbl_user SET is_verified = true WHERE user_id = ?";
        if (jdbcTemplate.update(query, id) == 1) {
            return findById(id);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(final long id) {
        return jdbcTemplate.query("SELECT * FROM tbl_user WHERE user_id = ?", ROW_MAPPER, id)
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> findByRecipeId(long recipeId) {
        return jdbcTemplate.query("SELECT * FROM tbl_user u JOIN tbl_recipe r ON u.user_id = r.user_id WHERE r.recipe_id = ?", ROW_MAPPER, recipeId)
                .stream()
                .findFirst();
    }

    @Override
    public int updateNameById(long userId, String name, String lastname) {
        String sql = "UPDATE tbl_user SET name = ?, last_name = ? " +
                "WHERE user_id = ?;";
        return jdbcTemplate.update(sql, name, lastname, userId);
    }


    @Override
    public int updatePasswordById(long userId, String password) {
        String sql = "UPDATE tbl_user SET password = ? WHERE user_id = ?;";
        return jdbcTemplate.update(sql, password, userId);
    }

    @Override
    public int deleteById(long userId) {
        String sql = "DELETE FROM tbl_user WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    public Optional<User> findByEmail(final String email) {
        return jdbcTemplate.query("SELECT * FROM tbl_user WHERE email = ?", ROW_MAPPER, email)
                .stream()
                .findFirst();
    }
    @Override
    public Optional<User> updatePasswordByEmail(String email, String password) {
        final String query = "UPDATE tbl_user set password = ? WHERE email = ? ";
        if (jdbcTemplate.update(query, password, email) == 1) {
            return findByEmail(email);
        }
        return Optional.empty();
    }

    @Override
    public int deleteNonVerifiedUsers() {
        return jdbcTemplate.update("DELETE FROM tbl_user WHERE is_verified = FALSE");
    }
}
