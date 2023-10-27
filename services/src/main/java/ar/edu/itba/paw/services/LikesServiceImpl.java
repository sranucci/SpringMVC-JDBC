package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.recipe.Recipe;
import ar.edu.itba.paw.servicesInterface.LikesService;
import ar.edu.itba.paw.servicesInterface.MailService;
import ar.edu.itba.paw.servicesInterface.RecipeService;
import ar.edu.itba.paw.servicesInterface.UserService;
import ar.edu.itba.paw.servicesInterface.exceptions.RecipeNotFoundException;
import ar.edu.itba.paw.servicesInterface.exceptions.UserNotFoundException;
import ar.edu.itba.persistenceInterface.LikesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikesServiceImpl implements LikesService {
    private final LikesDao likesDao;
    private final RecipeService recipeService;
    private final MailService mailService;
    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LikesServiceImpl.class);

    @Autowired
    public LikesServiceImpl(final LikesDao likesDao, final RecipeService recipeService, final MailService mailService,final UserService userService) {
        this.likesDao = likesDao;
        this.recipeService = recipeService;
        this.mailService = mailService;
        this.userService = userService;
    }


    @Transactional(readOnly = true)
    @Override
    public Boolean isRecipeLikedById(long userId, long recipeId) {
        return likesDao.isRecipeLikedById(userId, recipeId);
    }

    @Transactional(readOnly = true)
    @Override
    public Boolean isRecipeDislikedById(long userId, long recipeId) {
        return likesDao.isRecipeDislikedById(userId, recipeId);
    }


    @Transactional
    @Override
    public int makeRecipeLiked(long userId, long recipeId) {
        Recipe recipe = recipeService.getRecipe(recipeId).orElseThrow(RecipeNotFoundException::new);
        int likesResponse = likesDao.makeRecipeLiked(userId, recipeId);

        if (recipe.getLikesCount() > 0 && recipe.getLikesCount() % 5 == 0) { //cuando el usuario llega a un numero de likes multiplo de 5 se envia un mail felicitando
            User recipeOwner = userService.findById(recipe.getUserId()).orElseThrow(UserNotFoundException::new);
            mailService.sendNLikesEmail(recipeOwner.getEmail(), recipe.getTitle(), recipeOwner.getName(), recipe.getLikesCount(), recipeId)
                    .thenAccept(mailStatus -> {
                        if ( !mailStatus.isSend()){
                            LOGGER.error("Error sending likes mail to user {} for recipe with id {}, reason:\n{}",userId,recipe,mailStatus.getError());
                        }
                    });
        }
        return likesResponse;
    }

    @Transactional
    @Override
    public int makeRecipeDisliked(long userId, long recipeId) {
        return likesDao.makeRecipeDisliked(userId, recipeId);
    }

    @Transactional
    @Override
    public int removeRecipeRating(long userId, long recipeId) {
        return likesDao.removeRecipeRating(userId, recipeId);
    }
}
