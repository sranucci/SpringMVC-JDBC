package ar.edu.itba.paw.services.category;

import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.category.RecipeCategory;
import ar.edu.itba.paw.servicesInterface.category.CategoryService;
import ar.edu.itba.persistenceInterface.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryServiceImpl(final CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }


    @Transactional(readOnly = true)
    public List<Category> getAllCategoriesForRecipeForm(long recipeId){
        return  categoryDao.getAllCategoriesForRecipeForm(recipeId);
    }

    @Transactional
    @Override
    public boolean deleteCategories(long recipeId) {
        return categoryDao.deleteCategories(recipeId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Category> getAllCategories() {
        return categoryDao.getAllCategories();
    }


    @Transactional
    @Override
    public long createRecipeCategory(long recipeId, long categoryId) {
        return categoryDao.createRecipeCategory(recipeId, categoryId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Category> getCategoryById(long category_id) {
        return categoryDao.getCategoryById(category_id);
    }
}
