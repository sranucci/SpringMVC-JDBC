package ar.edu.itba.paw.services.ingredient;

import ar.edu.itba.paw.models.ingredient.Ingredient;
import ar.edu.itba.paw.models.ingredient.RecipeIngredient;
import ar.edu.itba.paw.models.ingredient.RecipeIngredientRecover;
import ar.edu.itba.paw.servicesInterface.exceptions.IngredientNotFoundException;
import ar.edu.itba.paw.servicesInterface.ingredient.IngredientsService;
import ar.edu.itba.persistenceInterface.IngredientsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class IngredientsServiceImpl implements IngredientsService {

    private final IngredientsDao ingredientsDao;

    @Autowired
    public IngredientsServiceImpl(final IngredientsDao ingredientsDao) {
        this.ingredientsDao = ingredientsDao;
    }


    @Transactional(readOnly = true)
    @Override
    public List<Ingredient> getAllIngredients() {
        return ingredientsDao.getAllIngredients();
    }


    @Transactional(readOnly = true)
    @Override
    public List<RecipeIngredientRecover> getAllRecipeIngredientsRecover(long recipeId) {
        return ingredientsDao.getAllRecipeIngredientsRecover(recipeId);
    }


    @Transactional
    @Override
    public int createRecipeIngredient(long recipeId, String ingredient, float qty, long unitId) {
        long ingredientKey = ingredientsDao.getIngredientId(ingredient).orElseThrow(IngredientNotFoundException::new);
        return ingredientsDao.createRecipeIngredient(recipeId, ingredientKey, qty, unitId);
    }

    @Transactional
    @Override
    public boolean deleteRecipeIngredients(long recipeId) {
        return ingredientsDao.deleteRecipeIngredients(recipeId);
    }

}
