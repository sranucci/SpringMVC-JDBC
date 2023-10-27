package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.servicesInterface.SavedRecipesService;
import ar.edu.itba.paw.servicesInterface.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SavedController extends BaseUserTemplateController {
    private SavedRecipesService srs;
    private static final Logger LOGGER = LoggerFactory.getLogger(SavedController.class);



    @RequestMapping(value = "/save/{recipeId:\\d+}", method = RequestMethod.POST)
    public ModelAndView saveRecipe(@PathVariable final long recipeId) {
        srs.saveRecipe(getCurrentUserId().orElseThrow(UserNotFoundException::new), recipeId);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/recipeDetail/" + recipeId);
        LOGGER.info("user with id {} saved recipe {} ",getCurrentUserId().get(),recipeId);
        return mav;
    }

    @RequestMapping(value = "/unsave/{recipeId:\\d+}", method = RequestMethod.POST)
    public ModelAndView unSaveRecipe(@PathVariable final long recipeId) {
        srs.deleteSavedRecipe((getCurrentUserId().orElseThrow(UserNotFoundException::new)), recipeId);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/recipeDetail/" + recipeId);
        LOGGER.info("user with id {} unsaved the recipe {}",getCurrentUserId().get(), recipeId);
        return mav;
    }

    @Autowired
    public void setSrs(SavedRecipesService srs) {
        this.srs = srs;
    }
}
