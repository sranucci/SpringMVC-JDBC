package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.UploadedRecipeFormDto;
import ar.edu.itba.paw.enums.AvailableDifficulties;
import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.ingredient.Ingredient;
import ar.edu.itba.paw.models.unit.Unit;
import ar.edu.itba.paw.servicesInterface.category.CategoryService;
import ar.edu.itba.paw.servicesInterface.exceptions.RecipeNotFoundException;
import ar.edu.itba.paw.servicesInterface.ingredient.IngredientsService;
import ar.edu.itba.paw.servicesInterface.unit.UnitsService;
import ar.edu.itba.paw.webapp.dtos.RecipeFormDto;
import ar.edu.itba.paw.servicesInterface.RecipeService;
import ar.edu.itba.paw.servicesInterface.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.forms.RecipeForm;
import ar.edu.itba.paw.webapp.forms.RecipeFormEdition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@Controller
public class UploadRecipeController extends BaseUserTemplateController {
    private RecipeService rs;
    private IngredientsService is;
    private CategoryService cs;
    private UnitsService us;

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadRecipeController.class);

    @RequestMapping(value = "/upload-recipe", method = {RequestMethod.POST})
    public ModelAndView recieveRecipe(@Valid @ModelAttribute("recipeForm") final RecipeForm recipeForm, final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return recipeForm(recipeForm);
        }
        final long recipeId = rs.create(recipeForm.asUploadedFormDto(), getCurrentUserId().orElseThrow(UserNotFoundException::new));
        LOGGER.info("user with id {} created recipe with id {}", getCurrentUserId().get(),recipeId);
        return new ModelAndView("redirect:/myRecipes");
    }

    @RequestMapping(value = "/upload-recipe", method = RequestMethod.GET)
    public ModelAndView recipeForm(@ModelAttribute("recipeForm") final RecipeForm recipeForm) {
        ModelAndView mav = new ModelAndView("/recipeForm");
        mav.addObject(getFormData());
        return mav;
    }



    @RequestMapping(value = "editRecipe/{recipe_id:\\d+}", method = RequestMethod.GET)
    public ModelAndView editRecipe(@ModelAttribute("recipeForm")final RecipeFormEdition editRecipe, @PathVariable("recipe_id") final long recipeId){
        Optional<UploadedRecipeFormDto> maybeRecipeDto = rs.recoverUserRecipe(recipeId, getCurrentUserId().get());
        if ( !maybeRecipeDto.isPresent()){
            LOGGER.warn("User with id {} tried to get an unexisting recipe",getCurrentUserId().get());
            throw new RecipeNotFoundException();
        }
        editRecipe.setFromRecipeFormDto(maybeRecipeDto.get());
        return obtainEditionView(recipeId);
    }


    private ModelAndView obtainEditionView(long recipeId) {
        ModelAndView mav = new ModelAndView("/recipeForm");
        mav.addObject(getFormData());
        mav.addObject("recipeEdit", true);
        mav.addObject("recipeId", recipeId);
        return mav;
    }


    @RequestMapping(value = "editRecipe/{recipeId:\\d+}", method = RequestMethod.POST)
    public ModelAndView editRecipe(@Valid @ModelAttribute("recipeForm") final RecipeFormEdition editRecipe, final BindingResult bindingResult, @PathVariable("recipeId") final long recipeId) {
        if (bindingResult.hasErrors()) {
            return obtainEditionView(recipeId);
        }
        final boolean succesUpdate = rs.updateRecipe(editRecipe.asUploadedFormDto(), getCurrentUserId().get(), recipeId);
        if ( !succesUpdate )
            LOGGER.warn("user with id {} tried to comment a not owned or inexisting recipe with id {}",getCurrentUserId().get(),recipeId);
        else
            LOGGER.info("user with id {} edited recipe with id {}",getCurrentUserId().get(), recipeId);
        return new ModelAndView("redirect:/myRecipes");
    }


    private RecipeFormDto getFormData() {
        List<Ingredient> ingredients = is.getAllIngredients();
        List<Category> categories = cs.getAllCategories();
        List<Unit> units = us.getAllUnits();
        AvailableDifficulties[] difficulties = AvailableDifficulties.values();
        return new RecipeFormDto(ingredients, units, categories, difficulties);
    }


    @Autowired
    public void setIngredientService(final IngredientsService is){
        this.is = is;
    }

    @Autowired
    public void setCategoryService(final CategoryService cs){
        this.cs = cs;
    }

    @Autowired
    public void setUnitsService(final UnitsService us){
        this.us = us;
    }




    @Autowired
    public void setRecipeService(final RecipeService rs) {
        this.rs = rs;
    }

}
