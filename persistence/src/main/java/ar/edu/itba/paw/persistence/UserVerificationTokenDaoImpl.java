package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.UserVerificationToken;
import ar.edu.itba.persistenceInterface.UserVerificationTokenDao;
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
public class UserVerificationTokenDaoImpl implements UserVerificationTokenDao {

    private final static RowMapper<UserVerificationToken> USER_VERIFICATION_TOKEN_MAPPER = (rs, rowNum) -> new UserVerificationToken(rs.getString("token"), rs.getLong("id"), rs.getLong("user_id"));
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public UserVerificationTokenDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("tbl_user_verification_token")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public UserVerificationToken createToken(long userId, String token) {
        final Map<String, Object> data = new HashMap<>();
        data.put("user_id", userId);
        data.put("token", token);
        final Number key = jdbcInsert.executeAndReturnKey(data);
        return new UserVerificationToken(token, key.longValue(), userId);
    }

    @Override
    public Optional<UserVerificationToken> getToken(String token) {
        final String query = "SELECT * FROM tbl_user_verification_token WHERE token = ?";
        return jdbcTemplate.query(query, USER_VERIFICATION_TOKEN_MAPPER, token).stream().findFirst();
    }
    @Override
    public void removeToken(String token) {
        final String query = "DELETE FROM tbl_user_verification_token WHERE token = ?";
        jdbcTemplate.update(query, token);
    }

    @Override
    public long deleteTokens() {
        return jdbcTemplate.update("DELETE FROM tbl_user_verification_token");
    }
}
