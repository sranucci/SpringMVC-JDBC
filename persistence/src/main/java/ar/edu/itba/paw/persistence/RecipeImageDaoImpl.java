package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.recipe.RecipeImage;
import ar.edu.itba.persistenceInterface.RecipeImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RecipeImageDaoImpl implements RecipeImageDao {


    private static final RowMapper<RecipeImage> IMAGEMAPPER = (rs, rno)
            -> new RecipeImage(rs.getLong("id"), rs.getBytes("photo_data"), rs.getBoolean("is_primary_photo"));

    private static final RowMapper<Long> IMAGEIDMAPPER =
            (rs, rno) -> rs.getLong("id");

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcImageInsertor;

    @Autowired
    public RecipeImageDaoImpl(DataSource ds) {

        jdbcTemplate = new JdbcTemplate(ds);
        jdbcImageInsertor = new SimpleJdbcInsert(ds)
                .withTableName("tbl_recipe_photo")
                .usingGeneratedKeyColumns("id");
    }


    //returns imageId on creation
    @Override
    public void createImage(long recipeId,byte[] serializedImage, boolean isMainImage) {
        Map<String, Object> data = new HashMap<>();
        data.put("recipe_id", recipeId);
        data.put("photo_data", serializedImage);
        data.put("is_primary_photo", isMainImage);
        jdbcImageInsertor.execute(data);
    }

    public Optional<RecipeImage> getImage(long imageId) {
        return jdbcTemplate.query("SELECT * FROM tbl_recipe_photo WHERE id = ?", IMAGEMAPPER, imageId).stream().findFirst();
    }

    public List<Long> getImagesForRecipe(long recipeId) {
        return jdbcTemplate.query("SELECT * FROM tbl_recipe_photo WHERE recipe_id = ? AND is_primary_photo = TRUE " +
                "UNION " +
                "SELECT * FROM tbl_recipe_photo WHERE recipe_id = ? AND  is_primary_photo = FALSE", IMAGEIDMAPPER, recipeId, recipeId);
    }

    @Override
    public boolean removeImages(long recipeId) {
        return jdbcTemplate.update("DELETE FROM tbl_recipe_photo WHERE tbl_recipe_photo.recipe_id = ? ", recipeId) > 0;
    }

}
