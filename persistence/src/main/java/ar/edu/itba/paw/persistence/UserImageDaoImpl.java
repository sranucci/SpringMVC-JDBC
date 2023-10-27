package ar.edu.itba.paw.persistence;

import ar.edu.itba.persistenceInterface.UserImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class UserImageDaoImpl implements UserImageDao {
    private final JdbcTemplate jdbcTemplate; //reduce el boilerplate

    private static final RowMapper<byte[]> IMAGE_MAPPER = (rs, rowNum) -> rs.getBytes("photo_data");

    @Autowired
    public UserImageDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    public long uploadUserPhoto(long userId, byte[] photoData){
        deleteUserPhoto(userId);
        String insertSql = "INSERT INTO tbl_user_photo (user_id, photo_data) VALUES (?, ?)";
            return jdbcTemplate.update(insertSql, userId, photoData);
    }

    @Override
    public Optional<byte[]> findUserPhotoByUserId(long userId) {
        String sql = "SELECT photo_data FROM tbl_user_photo WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, IMAGE_MAPPER)
                .stream().findFirst();
    }

    @Override
    public int deleteUserPhoto(long userId) {
        return jdbcTemplate.update("DELETE FROM tbl_user_photo WHERE user_id = ?", userId);
    }
}
