package ar.edu.itba.persistenceInterface;

import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.category.RecipeCategory;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {

    List<RecipeCategory> getAllRecipeCategories(long recipeId);

    List<Category> getAllCategories();

    long createRecipeCategory(long recipeId, long categoryId);

    Optional<Category> getCategoryById(long category_id);

    List<Category> getAllCategoriesForRecipeForm(long recipeId);

    boolean deleteCategories(long recipeId);

}
