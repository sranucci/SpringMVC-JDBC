package ar.edu.itba.paw.models.recipe;

import ar.edu.itba.paw.models.UserComment;
import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.ingredient.RecipeIngredient;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Getter
public class
FullRecipe extends Recipe {
    private final Instant createdDate;
    private final String[] instructions;
    @Setter
    private List<Category> categories;
    @Setter
    private List<RecipeIngredient> ingredients;
    @Setter
    private List<UserComment> comments;
    @Setter
    private long totalComments;

    //constructor a usar en recipeDetail (tiene toodo)
    public FullRecipe(long recipeId, String title, String description, long userId, boolean isPrivate, int minutes, int hours, int difficulty, int servings, List<Long> imageIds, long likesCount, long dislikesCount, Instant createdDate, String[] instructions, List<Category> categories, List<RecipeIngredient> ingredients, List<UserComment> comments, long totalComments) {
        super(recipeId, title, description, userId, isPrivate, minutes, hours, difficulty, servings, imageIds, likesCount, dislikesCount);
        this.createdDate = createdDate;
        this.instructions = instructions;
        this.categories = categories;
        this.ingredients = ingredients;
        this.comments = comments;
        this.totalComments = totalComments;
    }

    //jsp only understands Date and not Instant
    public Date getCreatedDate() {
        return Date.from(createdDate);
    }

}
