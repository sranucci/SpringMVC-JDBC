package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.servicesInterface.RecipeService;
import ar.edu.itba.paw.servicesInterface.exceptions.RecipeNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ar.edu.itba.paw.webapp.forms.DeletionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;


@Controller
public class DeleteRecipeController extends BaseUserTemplateController {

    private RecipeService rs;


    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteRecipeController.class);

    //HTML form does not support DELETE method
    @RequestMapping(value = "/deleteRecipeAdmin/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView deleteRecipe(@Valid @ModelAttribute("deleteForm") DeletionForm form, final BindingResult bindingResult, @PathVariable("id") long recipeId) {
        if (bindingResult.hasErrors())
            return new ModelAndView("redirect:/recipeDetail/" + recipeId + "?error");
        final boolean deleted = rs.removeRecipeNotifyingUser(recipeId, form.getDeletionMotive());
        if (deleted)
            LOGGER.info("Admin with id {} deleted recipe with id {}",getCurrentUserId().get(),recipeId);
        else
            LOGGER.warn("Admin with id {} tried to delete unexisting recipe with id {}",getCurrentUserId().get(),recipeId);

        return new ModelAndView("redirect:/");
    }


    @RequestMapping(value = "/deleteUserRecipe/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView deleteRecipe(@PathVariable("id") long recipeId) {
        Optional<Long> currentUserId = getCurrentUserId();
        //endpoint is visible if we have logged in user
        if (currentUserId.isPresent() && rs.removeRecipe(recipeId, currentUserId.get()))
            return new ModelAndView("redirect:/myRecipes");

        //user atempted to bypass browser and delete a recipie which doesn't own
        LOGGER.error("user with id {} tried to delete a not owned recipe",getCurrentUserId());
        throw new RecipeNotFoundException();
    }


    @Autowired
    public void setRecipeService(RecipeService rs) {
        this.rs = rs;
    }
}
