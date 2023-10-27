package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.comments.Comment;
import ar.edu.itba.paw.models.recipe.FullRecipe;
import ar.edu.itba.paw.servicesInterface.LikesService;
import ar.edu.itba.paw.servicesInterface.RecipeService;
import ar.edu.itba.paw.servicesInterface.SavedRecipesService;
import ar.edu.itba.paw.servicesInterface.UserService;
import ar.edu.itba.paw.servicesInterface.comments.CommentsLikesService;
import ar.edu.itba.paw.servicesInterface.comments.CommentsService;
import ar.edu.itba.paw.servicesInterface.exceptions.RecipeNotFoundException;
import ar.edu.itba.paw.servicesInterface.exceptions.UserNotFoundException;
import ar.edu.itba.paw.servicesInterface.exceptions.UserNotLoggedInException;
import ar.edu.itba.paw.webapp.exceptions.CommentNotFoundException;
import ar.edu.itba.paw.webapp.exceptions.InvalidArgumentException;
import ar.edu.itba.paw.webapp.forms.CommentForm;
import ar.edu.itba.paw.webapp.forms.DeleteCommentForm;
import ar.edu.itba.paw.webapp.forms.DeletionForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class RecipeDetailController extends BaseUserTemplateController {
    private RecipeService rs;
    private LikesService ls;
    private SavedRecipesService srs;
    private UserService us;
    private CommentsService cs;
    private CommentsLikesService cls;


    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeDetailController.class);

    @RequestMapping("/recipeDetail/{recipeId:\\d+}")
    public ModelAndView recipeDetail(
            @PathVariable("recipeId") final long recipeId,
            @RequestParam("commentsToBring") Optional<Long> commentsToBring,
            @ModelAttribute("commentForm") CommentForm commentForm,
            @ModelAttribute("deleteForm") DeletionForm recipeDeletionForm,
            @ModelAttribute("deleteCommentForm") DeleteCommentForm deletionCommentForm
    ) {
        if (commentsToBring.isPresent() && commentsToBring.get() < 0)
            throw new InvalidArgumentException();
        final ModelAndView mav = new ModelAndView("/recipeDetail");
        FullRecipe fullRecipe = rs.getFullRecipe(recipeId, getCurrentUserId(), commentsToBring, Optional.empty()).orElseThrow(RecipeNotFoundException::new);
        mav.addObject("recipe", fullRecipe);
        if (getCurrentUserId().isPresent()) {
            long userId = getCurrentUserId().get();
            mav.addObject("isSaved", srs.isRecipeSavedByUser(userId, recipeId));
            mav.addObject("isLiked", ls.isRecipeLikedById(userId, recipeId));
            mav.addObject("isDisliked", ls.isRecipeDislikedById(userId, recipeId));
        } else {
            mav.addObject("isSaved", false);
            mav.addObject("isLiked", false);
            mav.addObject("isDisliked", false);
        }
        mav.addObject("recipeUser", us.findById(fullRecipe.getUserId()).orElseThrow(UserNotFoundException::new));
        return mav;
    }


    @RequestMapping(value = "/recipeDetail/{recipeId:\\d+}", method = RequestMethod.POST)
    public ModelAndView comment(@Valid @ModelAttribute("commentForm") final CommentForm commentForm, final BindingResult errors,
                                @PathVariable("recipeId") final long recipeId,
                                @ModelAttribute("deleteForm") DeletionForm recipeDeletionForm,
                                @ModelAttribute("deleteCommentForm") DeleteCommentForm deleteCommentForm) {
        if (errors.hasErrors()) {
            return recipeDetail(recipeId, Optional.empty(), commentForm, recipeDeletionForm, deleteCommentForm);
        }
        Optional<Comment> c = cs.createComment(recipeId, getCurrentUserId().orElseThrow(UserNotLoggedInException::new), commentForm.getComment());
        if (c.isPresent()) {
            LOGGER.info("user with id {} created a comment with id {} on recipe with id {}",getCurrentUserId().get(),c.get().getCommentId(),recipeId);
            return new ModelAndView("redirect:/ recipeDetail" + "/" + recipeId);
        }
        LOGGER.warn("User with id {} tried to comment an unexisting recipe",getCurrentUserId().get());
        throw new RecipeNotFoundException();
    }

    @RequestMapping(value = "/recipeDetail/likeComment/{commentId:\\d+}", method = RequestMethod.POST)
    public ModelAndView likeComment(@PathVariable("commentId") final long commentId) {
        //get is secure, user is logged in
        return likeStatusHelper(commentId, getCurrentUserId().orElseThrow(UserNotLoggedInException::new), true);
    }


    private ModelAndView likeStatusHelper(final long commentId, final long userId, final boolean likeStatus) {
        cls.setCommentLikeStatus(commentId, userId, likeStatus);
        Optional<Comment> c = cs.getRecipeComment(commentId);
        return new ModelAndView("redirect:/recipeDetail/" + c.orElseThrow(() -> new CommentNotFoundException("Comment not found")).getRecipeId());
    }


    @RequestMapping(value = "/recipeDetail/dislikeComment/{commentId:\\d+}", method = RequestMethod.POST)
    public ModelAndView dislikeComment(@PathVariable("commentId") final long commentId) {
        return likeStatusHelper(commentId, getCurrentUserId().orElseThrow(UserNotLoggedInException::new), false);
    }

    @RequestMapping(value = "/deleteRecipeComment/{recipeId:\\d+}/{commentId:\\d+}", method = RequestMethod.POST)
    public ModelAndView deleteRecipeComment(@Valid @ModelAttribute("deleteCommentForm") DeleteCommentForm form, final BindingResult bindingResult, @PathVariable("recipeId") long recipeId, @PathVariable("commentId") long commentId) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("redirect:/recipeDetail/" + recipeId + "?commentError");
        }
        cs.removeCommentNotifyingUser(recipeId, commentId, form.getDeletionMotive());
        return new ModelAndView("redirect:/recipeDetail/" + recipeId);
    }

    @Autowired
    public void setCommentLikeService(CommentsLikesService cls) {
        this.cls = cls;
    }

    @Autowired
    public void setCommentsService(CommentsService cs) {
        this.cs = cs;
    }

    @Autowired
    public void setRecipeService(RecipeService rs) {
        this.rs = rs;
    }

    @Autowired
    public void setLikesService(LikesService ls) {
        this.ls = ls;
    }

    @Autowired
    public void setSavedRecipesService(SavedRecipesService srs) {
        this.srs = srs;
    }

    @Autowired
    public void setUserService(UserService us) {
        this.us = us;
    }
}
