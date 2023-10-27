package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.enums.AvailableDifficultiesForSort;
import ar.edu.itba.paw.enums.ShowRecipePages;
import ar.edu.itba.paw.enums.SortOptions;
import ar.edu.itba.paw.models.Difficulty;
import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.deletion.DeletionData;
import ar.edu.itba.paw.models.ingredient.RecipeIngredient;
import ar.edu.itba.paw.models.recipe.FullRecipe;
import ar.edu.itba.paw.models.recipe.Recipe;
import ar.edu.itba.persistenceInterface.RecipeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class RecipeDaoImpl implements RecipeDao {
    private final static RowMapper<Long> PHOTO_MAPPER = (rs, rowNum) -> rs.getLong("id");
    private final static RowMapper<DeletionData> RECIPE_DELETION_DATA = (rs, rowNum) -> new DeletionData(rs.getString("email"), rs.getString("title"));
    private final static RowMapper<RecipeIngredient> RECIPE_INGREDIENT_ROW_MAPPER = (rs, rowNum) -> new RecipeIngredient(rs.getString("ingredient_name"), rs.getString("unit_name"), rs.getFloat("quantity"));
    private final static RowMapper<Category> CATEGORY_ROW_MAPPER = (rs, rowNum) -> new Category(rs.getInt("category_id"), rs.getString("category_name"));
    private final static RowMapper<Difficulty> DIFFICULTY_MAPPER = (rs, rowNum) -> new Difficulty(rs.getLong("difficulty_id"), rs.getString("difficulty_name"));
    private final static RowMapper<Recipe> RECIPE_ROW_MAPPER = ((rs, rowNum) -> {
        List<Long> imageIds = new ArrayList<>();
        imageIds.add(rs.getLong("image_id"));
        return new Recipe(rs.getLong("recipe_id"), rs.getString("title"), rs.getString("description"), rs.getLong("user_id"), rs.getBoolean("is_private"), rs.getInt("total_minutes") % 60, rs.getInt("total_minutes") / 60, rs.getInt("difficulty_id"),
                rs.getInt("servings"), imageIds, rs.getInt("likes"), rs.getInt("dislikes"));
    });


    private final static RowMapper<String[]> INSTRUCTIONS_MAPPER =

            (rs,rowNum) -> rs.getString("instructions").split("#");
    private final static RowMapper<Long> COUNT_MAPPER = ((rs, rowNum) -> rs.getLong("recipe_count"));

    private final static RowMapper<FullRecipe> FULL_RECIPE_ROW_MAPPER = ((rs, rowNum) ->
            new FullRecipe(rs.getLong("recipe_id"), rs.getString("title"), rs.getString("description"), rs.getLong("user_id"), rs.getBoolean("is_private"), rs.getInt("total_minutes") % 60, rs.getInt("total_minutes") / 60, rs.getInt("difficulty_id"),
                    rs.getInt("servings"), new ArrayList<>(), rs.getInt("likes"), rs.getInt("dislikes"), rs.getTimestamp(("created_at")).toInstant(), rs.getString("instructions").split("#"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0));

    private final JdbcTemplate jdbcTemplate; //reduce el boilerplate
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public RecipeDaoImpl(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("tbl_recipe")
                .usingGeneratedKeyColumns("recipe_id")
                .usingColumns("title", "description", "user_id", "is_private", "total_minutes", "difficulty_id", "servings", "instructions");
    }

    private void sortBySql(Optional<SortOptions> sort, StringBuilder query) {
        if (sort.isPresent() && !sort.get().equals(SortOptions.NONE)) {
            SortOptions sortOption = sort.get();
            query.append("ORDER BY ").append(sortOption.getSortOption()).append(" ");
        } else { //siempre hay que ordenar por algo para que ande paging
            query.append("ORDER BY recipe_id DESC ");
        }
    }

    private void filterByDifficultySql(Optional<AvailableDifficultiesForSort> difficulty, List<Object> parameters, StringBuilder query) {
        if (difficulty.isPresent() && difficulty.get() != AvailableDifficultiesForSort.ALL) {
            query.append("AND r.difficulty_id = ? ");
            parameters.add(difficulty.get().getDifficultyId());
        }
    }

    private void filterByCategoriesSql(List<Integer> categories, List<Object> parameters, StringBuilder query) {
        if (categories.size() != 0) {
            int[] categoriesId = categories.stream().mapToInt(Integer::intValue).toArray();
            query
                    .append("AND r.recipe_id IN ( ")
                    .append("SELECT recipe_id ")
                    .append("FROM tbl_recipe_category ")
                    .append("WHERE category_id = ANY (?) ")
                    .append("GROUP BY recipe_id ) ");

            parameters.add(categoriesId);
        }
    }

    private void filterBySearchQuerySql(Optional<String> searchQuery, List<Object> parameters, StringBuilder query) {
        if (searchQuery.isPresent()) {
            String[] titleList = Arrays.stream(searchQuery.get().split("\\s+"))
                    .map(keyword -> "%" + InputCleaner.clean(keyword).toUpperCase() + "%")
                    .toArray(String[]::new);

            query.append("AND UPPER(title) LIKE ALL (?) ");
            parameters.add(titleList);
        }
    }

    private void filterByPageTypeSql(ShowRecipePages pageToShow, List<Object> parameters, StringBuilder query, Optional<Long> userId) {
        if (pageToShow.equals(ShowRecipePages.DISCOVER)) {
            query.append("WHERE is_private = false ");
        } else if (pageToShow.equals(ShowRecipePages.SAVED) && userId.isPresent()) {
            query
                    .append("WHERE r.recipe_id IN ( ")
                    .append("SELECT recipe_id FROM tbl_saved_by_user s ")
                    .append("WHERE s.user_id = ? ) ");
            parameters.add(userId.get());
        } else if (pageToShow.equals(ShowRecipePages.MY_RECIPES) && userId.isPresent()) {
            query.append("WHERE r.user_id = ? ");
            parameters.add(userId.get());
        }
    }

    private void filterByIngredientsSql(Optional<String> ingredients, List<Object> parameters, StringBuilder query) {
        if (ingredients.isPresent()) {
            String[] ingredientsList = Arrays.stream(ingredients.get().split("#"))
                    .map(keyword -> "%" + InputCleaner.clean(keyword).toUpperCase() + "%")
                    .toArray(String[]::new);
            query.append("JOIN ( ")
                    .append("SELECT distinct recipe_id ")
                    .append("FROM tbl_recipe_ingredient ri ")
                    .append("LEFT JOIN tbl_ingredient i on ri.ingredient_id = i.ingredient_id ")
                    .append("WHERE UPPER(i.ingredient_name) LIKE ANY (?) ")
                    .append("GROUP BY recipe_id ")
                    .append("HAVING COUNT(DISTINCT i.ingredient_id) >= ? ")
                    .append(") AS filtered_recipes ON r.recipe_id = filtered_recipes.recipe_id ");
            parameters.add(ingredientsList);
            parameters.add(ingredientsList.length);
        }
    }

    private String serializeInstructions(String[] instructions) {
        return Arrays.stream(instructions).reduce((prevConcat, current) -> prevConcat + "#" + current).orElse("");//instructions never emptyor null
    }

    @Override
    public long create(String title, String description, long userId, boolean isPrivate, int totalMinutes, long difficultyId, int servings, String[] instructions) {
        String serializedInstructions = serializeInstructions(instructions);
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("user_id", userId);
        data.put("is_private", isPrivate);
        data.put("total_minutes", totalMinutes);
        data.put("difficulty_id", difficultyId);
        data.put("servings", servings);
        data.put("instructions", serializedInstructions);
        final Number key = jdbcInsert.executeAndReturnKey(data);
        return key.longValue();
    }

    @Override
    public Optional<DeletionData> getRecipeDeletionData(long recipeId) {
        return jdbcTemplate.query("SELECT * FROM  tbl_recipe JOIN tbl_user ON tbl_recipe.user_id = tbl_user.user_id WHERE recipe_id = ?", RECIPE_DELETION_DATA, recipeId).stream().findFirst();
    }

    @Override
    public boolean removeRecipe(long recipeId) {
        long rowsDeleted = jdbcTemplate.update("DELETE FROM tbl_recipe WHERE tbl_recipe.recipe_id = ?", recipeId);
        return rowsDeleted > 0;
    }

    @Override
    public boolean removeRecipe(long recipeId, long userId) {
        //chequeamos que el usuario efectivamente sea el dueño
        long rowsDeleted = jdbcTemplate.update("DELETE FROM tbl_recipe WHERE tbl_recipe.user_id = ? AND tbl_recipe.recipe_id = ?", userId, recipeId);
        return rowsDeleted > 0;
    }
    @Override
    public Optional<Recipe> getRecipe(long recipeId) {
        String query =
                "SELECT r.recipe_id, title, description, r.user_id, is_private, total_minutes, difficulty_id, " +
                        "servings, " +
                        "COUNT(DISTINCT CASE WHEN l.is_like = true THEN l.user_id END) AS likes, COUNT(DISTINCT CASE WHEN l.is_like = false THEN l.user_id END) AS dislikes, rp.id as image_id " +
                        "FROM tbl_recipe r " +
                        "JOIN tbl_recipe_photo rp ON r.recipe_id = rp.recipe_id " +
                        "LEFT JOIN tbl_like_dislike l ON r.recipe_id = l.recipe_id " +
                        "WHERE r.recipe_id = ? " +
                        "GROUP BY r.recipe_id, difficulty_id, rp.id ";
        return jdbcTemplate.query(query, RECIPE_ROW_MAPPER, recipeId).stream().findFirst();
    }

    @Override
    public List<RecipeIngredient> getIngredientsById(long recipeId) {
        return jdbcTemplate.query("SELECT ingredient_name, unit_name, quantity FROM tbl_recipe NATURAL JOIN tbl_recipe_ingredient NATURAL JOIN tbl_ingredient NATURAL JOIN tbl_units WHERE recipe_id = ?", RECIPE_INGREDIENT_ROW_MAPPER, recipeId);
    }

    @Override
    public List<Category> getCategoriesById(long recipeId) {
        return jdbcTemplate.query("SELECT category_name, category_id FROM tbl_recipe NATURAL JOIN tbl_recipe_category NATURAL JOIN tbl_category WHERE recipe_id = ?", CATEGORY_ROW_MAPPER, recipeId);
    }

    @Override
    public List<Long> getPhotoIdsByRecipeId(long recipeId) {
        return jdbcTemplate.query("SELECT * FROM tbl_recipe_photo WHERE recipe_id = ?", PHOTO_MAPPER, recipeId);
    }

    public List<Recipe> getRecipesByFilter(Optional<AvailableDifficultiesForSort> difficulty, Optional<String> ingredients, List<Integer> categories,
                                           Optional<SortOptions> sort, Optional<String> searchQuery, ShowRecipePages pageToShow,
                                           Optional<Long> userId, Optional<Long> page, Optional<Integer> limit) {

        List<Object> parameters = new ArrayList<>();

        StringBuilder query = new StringBuilder();

        query
                .append("SELECT r.recipe_id, title, description, r.user_id, is_private, total_minutes, difficulty_id, ")
                .append("servings, created_at, instructions, rp.id AS image_id, ")
                .append("COUNT(DISTINCT CASE WHEN l.is_like = true THEN l.user_id END) AS likes, COUNT(DISTINCT CASE WHEN l.is_like = false THEN l.user_id END) AS dislikes ")
                .append("FROM tbl_recipe r ")
                .append("JOIN tbl_recipe_photo rp ON r.recipe_id = rp.recipe_id ")
                .append("JOIN tbl_recipe_category rc ON r.recipe_id = rc.recipe_id ")
                .append("LEFT JOIN tbl_like_dislike l ON r.recipe_id = l.recipe_id ");

        filterByIngredientsSql(ingredients, parameters, query);

        filterByPageTypeSql(pageToShow, parameters, query, userId);

        query.append("AND rp.is_primary_photo = TRUE ");

        filterBySearchQuerySql(searchQuery, parameters, query);

        filterByCategoriesSql(categories, parameters, query);

        filterByDifficultySql(difficulty, parameters, query);

        query.append("GROUP BY r.recipe_id, image_id ");

        sortBySql(sort, query);

        long lim = (limit.orElse(10)); // 10 is the default pageSize if none is given
        long offset = (page.orElse(1L) - 1) * lim; // frontend's 1st page is backend's page number 0

        query.append("LIMIT ").append(lim).append(" OFFSET ").append(offset);

        return jdbcTemplate.query(query.toString(), parameters.toArray(), RECIPE_ROW_MAPPER);
    }

    public long getTotalNumberRecipesByFilterForPagination(Optional<AvailableDifficultiesForSort> difficulty, Optional<String> ingredients, List<Integer> categories,
                                                           Optional<SortOptions> sort, Optional<String> searchQuery, ShowRecipePages pageToShow,
                                                           Optional<Long> userId, Optional<Long> page, Optional<Integer> limit){
        List<Object> parameters = new ArrayList<>();

        StringBuilder query = new StringBuilder();

        query
                .append("SELECT COUNT(DISTINCT r.recipe_id) as recipe_count ")
                .append("FROM tbl_recipe r ")
                .append("JOIN tbl_recipe_category rc ON r.recipe_id = rc.recipe_id ")
        ;

        filterByIngredientsSql(ingredients, parameters, query);

        filterByPageTypeSql(pageToShow, parameters, query, userId);

        filterBySearchQuerySql(searchQuery, parameters, query);

        filterByCategoriesSql(categories, parameters, query);

        filterByDifficultySql(difficulty, parameters, query);

        return jdbcTemplate.query(query.toString(), parameters.toArray(), COUNT_MAPPER).stream().findFirst().orElse(0L);
    }

    public Optional<FullRecipe> getFullRecipe(long recipeId) {
        String query =
                "SELECT r.title, r.recipe_id, title, description, r.user_id, is_private, total_minutes, difficulty_id, servings, created_at, instructions, " +
                        "COUNT(DISTINCT CASE WHEN l.is_like = true THEN l.user_id END) AS likes, COUNT(DISTINCT CASE WHEN l.is_like = false THEN l.user_id END) AS dislikes, rp.id as image_id " +
                        "FROM tbl_recipe r " +
                         "JOIN tbl_recipe_photo rp ON r.recipe_id = rp.recipe_id " +
                         "LEFT JOIN tbl_like_dislike l ON r.recipe_id = l.recipe_id " +
                        "WHERE r.recipe_id = ? " +
                        "GROUP BY r.recipe_id, difficulty_id, rp.id ";
        return jdbcTemplate.query(query, FULL_RECIPE_ROW_MAPPER, recipeId).stream().findFirst();
    }

    @Override
    public boolean updateRecipe(String title, String description, boolean isPrivate, int totalMinutes, long difficultyId, int servings, String[] instructions, long userId, long recipeId) {
        int rowsAffected = jdbcTemplate.update("UPDATE tbl_recipe SET title = ?,description = ?,is_private = ?,total_minutes = ?,difficulty_id = ?,servings = ?,instructions = ?" +
                " WHERE recipe_id = ? and user_id = ? ", title, description, isPrivate, totalMinutes, difficultyId, servings, serializeInstructions(instructions), recipeId, userId);
        return rowsAffected > 0;
    }


    @Override
    public Optional<String[]> getRecipeInstructions(long recipeId) {
        return jdbcTemplate.query("SELECT * FROM tbl_recipe WHERE recipe_id = ?", INSTRUCTIONS_MAPPER, recipeId)
                .stream().findFirst();
    }
}