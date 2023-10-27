package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.servicesInterface.LikesService;
import ar.edu.itba.paw.servicesInterface.exceptions.UserNotLoggedInException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LikesController extends BaseUserTemplateController {
    private LikesService ls;

    @RequestMapping(value = "/rate/like/{recipeId:\\d+}", method = RequestMethod.POST)
    public ModelAndView likeRecipe(@PathVariable final long recipeId) {
        long userId = getCurrentUserId().orElseThrow(UserNotLoggedInException::new);
        ls.makeRecipeLiked(userId, recipeId);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/recipeDetail/" + recipeId);

        return mav;
    }

    @RequestMapping(value = "/rate/dislike/{recipeId:\\d+}", method = RequestMethod.POST)
    public ModelAndView dislikeRecipe(@PathVariable final long recipeId) {
        long userId = getCurrentUserId().orElseThrow(UserNotLoggedInException::new);
        ls.makeRecipeDisliked(userId, recipeId);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/recipeDetail/" + recipeId);
        return mav;
    }

    @RequestMapping(value = "/rate/remove/{recipeId:\\d+}", method = RequestMethod.POST)
    public ModelAndView unrateRecipe(@PathVariable final long recipeId) {
        long userId = getCurrentUserId().orElseThrow(UserNotLoggedInException::new);
        ls.removeRecipeRating(userId, recipeId);
        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/recipeDetail/" + recipeId);
        return mav;
    }

    @Autowired
    public void setLikesService(LikesService ls) {
        this.ls = ls;
    }

}
