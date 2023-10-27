package ar.edu.itba.paw.servicesInterface.category;

import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.category.RecipeCategory;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();

    Optional<Category> getCategoryById(long category_id);

    long createRecipeCategory(long recipeId, long categoryId);

    List<Category> getAllCategoriesForRecipeForm(long recipeId);

    boolean deleteCategories(long recipeId);
}
