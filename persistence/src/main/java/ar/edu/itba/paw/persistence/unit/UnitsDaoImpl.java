package ar.edu.itba.paw.persistence.unit;

import ar.edu.itba.paw.models.unit.Unit;
import ar.edu.itba.persistenceInterface.UnitsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;


@Repository
public class UnitsDaoImpl implements UnitsDao {


    private final static RowMapper<Unit> UNITMAPPER = (rs, row) -> new Unit(rs.getLong("unit_id"), rs.getString("unit_name"));
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UnitsDaoImpl(DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }


    @Override
    public List<Unit> getAllUnits() {
        return jdbcTemplate.query("SELECT * FROM tbl_units", UNITMAPPER);
    }


}
