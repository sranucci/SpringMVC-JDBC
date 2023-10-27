package ar.edu.itba.paw.models.recipe;

import ar.edu.itba.paw.enums.AvailableDifficulties;
import ar.edu.itba.paw.enums.AvailableDifficultiesForSort;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Recipe {
    private final long recipeId;
    private final String title;
    private final String description;
    private final long userId;
    private final boolean isPrivate;
    private final int minutes;
    private final int hours;
    private final long difficulty;
    private final int servings;
    private final long likesCount;
    private final long dislikesCount;
    @Setter
    private List<Long> imageIds;

    //constructor que usa FullRecipe para create (no tiene like ni dislike como param)
    public Recipe(long recipeId, String title, String description, long userId, boolean isPrivate, int minutes, int hours, long difficulty, int servings, List<Long> imageIds) {
        this.recipeId = recipeId;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.isPrivate = isPrivate;
        this.minutes = minutes;
        this.hours = hours;
        this.difficulty = difficulty;
        this.servings = servings;
        this.imageIds = imageIds;
        this.likesCount = 0;
        this.dislikesCount = 0;
    }

    public Recipe(long recipeId, String title, String description, long userId, boolean isPrivate, int minutes, int hours, long difficulty, int servings, List<Long> imageIds, long likesCount, long dislikesCount) {
        this.recipeId = recipeId;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.isPrivate = isPrivate;
        this.minutes = minutes;
        this.hours = hours;
        this.difficulty = difficulty;
        this.servings = servings;
        this.imageIds = imageIds;
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
    }

    // esto es obligatorio porque a lombok le pinta cambiar el nombre isPrivate (confirmado)
    public boolean getIsPrivate() {
        return isPrivate;
    }

    public String getDifficultyString() {
        return AvailableDifficulties.getStringValue(difficulty);
    }
}
