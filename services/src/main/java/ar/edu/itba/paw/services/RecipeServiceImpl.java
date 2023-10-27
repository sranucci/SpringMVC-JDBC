package ar.edu.itba.paw.services;

import ar.edu.itba.paw.dtos.CommentWrapper;
import ar.edu.itba.paw.dtos.RecipesAndSize;
import ar.edu.itba.paw.dtos.UploadedRecipeFormDto;
import ar.edu.itba.paw.enums.AvailableDifficultiesForSort;
import ar.edu.itba.paw.enums.ShowRecipePages;
import ar.edu.itba.paw.enums.SortOptions;
import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.deletion.DeletionData;
import ar.edu.itba.paw.models.ingredient.RecipeIngredientRecover;
import ar.edu.itba.paw.models.recipe.FullRecipe;
import ar.edu.itba.paw.models.recipe.Recipe;
import ar.edu.itba.paw.models.recipe.RecipeImage;
import ar.edu.itba.paw.servicesInterface.ImageService;
import ar.edu.itba.paw.servicesInterface.MailService;
import ar.edu.itba.paw.servicesInterface.RecipeService;
import ar.edu.itba.paw.servicesInterface.category.CategoryService;
import ar.edu.itba.paw.servicesInterface.comments.CommentsService;
import ar.edu.itba.paw.servicesInterface.ingredient.IngredientsService;
import ar.edu.itba.persistenceInterface.RecipeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeDao recipeDao;
    private final IngredientsService ingredientsService;
    private final CategoryService categoryService;
    private final MailService mailService;
    private final ImageService imageService;
    private final CommentsService commentsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RecipeServiceImpl.class);

    @Autowired
    public RecipeServiceImpl(final RecipeDao recipeDao, final IngredientsService ingredientsService, final CommentsService commentsService,
                             final CategoryService categoryService, final MailService mailService, final ImageService imageService) {
        this.recipeDao = recipeDao;
        this.imageService = imageService;
        this.ingredientsService = ingredientsService;
        this.categoryService = categoryService;
        this.commentsService = commentsService;
        this.mailService = mailService;
    }

    @Transactional
    @Override
    public boolean removeRecipeNotifyingUser(long recipeId, String deletionMotive) {
        Optional<DeletionData> data = recipeDao.getRecipeDeletionData(recipeId);
        boolean deleted = recipeDao.removeRecipe(recipeId);
        if (!data.isPresent() || !deleted) {
            return false;
        }
        mailService.sendRecipeDeletionEmail(data.get().getUserMail(), data.get().getRecipeName(), deletionMotive)
                .thenAccept(mailStatus -> {
                    if ( !mailStatus.isSend() )
                        LOGGER.error("Could not send mail notifying recipe deletion for recipe {} with motive {}\nreason\n{}",recipeId,deletionMotive,mailStatus.getError());
                });
        return true;
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<UploadedRecipeFormDto> recoverUserRecipe(long recipeId, long userId) {
        Optional<FullRecipe> maybeRecipe = recipeDao.getFullRecipe(recipeId);
        if (!maybeRecipe.isPresent() || userId != maybeRecipe.get().getUserId())
            return Optional.empty();
        List<Category> categories = categoryService.getAllCategoriesForRecipeForm(recipeId);
        List<RecipeIngredientRecover> ingredients = ingredientsService.getAllRecipeIngredientsRecover(recipeId);
        List<Long> recipeImagesId = imageService.getImagesForRecipe(recipeId);
        return Optional.of(new UploadedRecipeFormDto(maybeRecipe.get(), categories, ingredients, recipeImagesId));

    }
    @Transactional
    @Override
    public boolean removeRecipe(long recipeId, long userId) {
        return recipeDao.removeRecipe(recipeId, userId);
    }


    @Transactional
    @Override
    public long create(UploadedRecipeFormDto rf, long userId) {
        long recipeId = recipeDao.create(rf.getTitle(), rf.getDescription(), userId, rf.isPrivate(), rf.getTotalTime(), rf.getDifficultyId(), rf.getServings(), rf.getInstructions());
        imageService.createImages(recipeId, rf.getRecipeImages());
        rf.getIngredientsIterable().forEach(ingredientData -> ingredientsService.createRecipeIngredient(recipeId, ingredientData.getIngredient(), ingredientData.getQuantity(), ingredientData.getUnitId()));
        rf.getCategoriesIterable().forEach(categoryId -> categoryService.createRecipeCategory(recipeId, categoryId));
        return recipeId;
    }

    @Transactional
    @Override
    public boolean updateRecipe(UploadedRecipeFormDto rf, long userId, long recipeId) {
        Optional<Recipe> r = recipeDao.getRecipe(recipeId);
        if (!r.isPresent() || r.get().getUserId() != userId)
            return false;
        recipeDao.updateRecipe(rf.getTitle(), rf.getDescription(), rf.isPrivate(), rf.getTotalTime(), rf.getDifficultyId(), rf.getServings(),
                rf.getInstructions(), userId, recipeId);
        if (rf.getRecipeImages() != null && !rf.getRecipeImages().isEmpty()) {
            imageService.removeImages(recipeId);
            imageService.createImages(recipeId, rf.getRecipeImages());
        }
        ingredientsService.deleteRecipeIngredients(recipeId);
        rf.getIngredientsIterable().forEach(ingredientData -> ingredientsService.createRecipeIngredient(recipeId, ingredientData.getIngredient(), ingredientData.getQuantity(), ingredientData.getUnitId()));
        categoryService.deleteCategories(recipeId);
        rf.getCategoriesIterable().forEach(categoryId -> categoryService.createRecipeCategory(recipeId, categoryId));
        return true;
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<RecipeImage> getImage(long imageId) {
        return imageService.getImage(imageId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Recipe> getRecipe(long recipeId) {
        return recipeDao.getRecipe(recipeId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<FullRecipe> getFullRecipe(long recipeId, Optional<Long> userId, Optional<Long> commentsToBring, Optional<Integer> pageNumber) {
        Optional<FullRecipe> fullRecipe = recipeDao.getFullRecipe(recipeId);
        if (fullRecipe.isPresent()) {
            fullRecipe.get().setImageIds(recipeDao.getPhotoIdsByRecipeId(recipeId));
            fullRecipe.get().setCategories(recipeDao.getCategoriesById(recipeId));
            fullRecipe.get().setIngredients(recipeDao.getIngredientsById(recipeId));

            CommentWrapper commentWrapper = commentsService.getComments(recipeId, userId, commentsToBring, pageNumber);
            fullRecipe.get().setComments(commentWrapper.getCommentList());
            fullRecipe.get().setTotalComments(commentWrapper.getTotalComments());
        }
        return fullRecipe;
    }

    @Transactional(readOnly = true)
    @Override
    public RecipesAndSize getRecipesByFilter(Optional<AvailableDifficultiesForSort> difficulty, Optional<String> ingredients, List<Integer> categories, Optional<SortOptions> sort, Optional<String> query, ShowRecipePages pageToShow, Optional<Long> userId, Optional<Long> page, Optional<Integer> pageSize) {
        List<Recipe> recipeList = recipeDao.getRecipesByFilter(difficulty, ingredients, categories, sort, query, pageToShow, userId, page, pageSize);
        long totalRecipes = recipeDao.getTotalNumberRecipesByFilterForPagination(difficulty, ingredients, categories, sort, query, pageToShow, userId, page, pageSize);
        return new RecipesAndSize(recipeList, totalRecipes);
    }
}
