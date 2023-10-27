package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.ResetPasswordToken;
import ar.edu.itba.persistenceInterface.ResetPasswordTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ResetPasswordTokenDaoImpl implements ResetPasswordTokenDao {

    private final static RowMapper<ResetPasswordToken> RESET_PASSWORD_TOKEN_MAPPER = (rs, rowNum) -> new ResetPasswordToken(rs.getString("token"), rs.getLong("id"), rs.getLong("user_id"), rs.getTimestamp("expiration_date").toLocalDateTime());
    private final JdbcTemplate jdbcTemplate; //reduce el boilerplate
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public ResetPasswordTokenDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("tbl_reset_password_token")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public void removeTokenByUserID(long id) {
        final String query = "DELETE FROM tbl_reset_password_token WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public ResetPasswordToken createToken(long userId, String token, LocalDateTime expirationDate) {
        final Map<String, Object> passwordData = new HashMap<>();
        Timestamp date = Timestamp.valueOf(expirationDate);
        passwordData.put("user_id", userId);
        passwordData.put("token", token);
        passwordData.put("expiration_date", date);
        final Number key = jdbcInsert.executeAndReturnKey(passwordData);
        return new ResetPasswordToken(token, key.longValue(), userId, expirationDate);

    }

    @Override
    public Optional<ResetPasswordToken> getToken(String token) {
        final String query = "SELECT * FROM tbl_reset_password_token WHERE token = ?";
        return jdbcTemplate.query(query, RESET_PASSWORD_TOKEN_MAPPER, token).stream().findFirst();
    }

    @Override
    public void removeToken(String token) {
        final String query = "DELETE FROM tbl_reset_password_token WHERE token = ?";
        jdbcTemplate.update(query, token);
    }
}
