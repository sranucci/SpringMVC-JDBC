package ar.edu.itba.paw.dtos;

import ar.edu.itba.paw.enums.AvailableDifficultiesForSort;
import ar.edu.itba.paw.enums.SortOptions;
import ar.edu.itba.paw.models.recipe.Recipe;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class ShowRecipesDto {
    // mandatory fields
    private String headTitle;
    private String pageTitle;
    private List<Recipe> recipeList;

    // sort parameters
    private AvailableDifficultiesForSort difficulty;
    private SortOptions sort;
    private List<String> selectedCategories;
    private String selectedIngredients;

    // searchbar parameters
    private String searchBarQuery;

    //paging
    private long pageNumber;
    private int pageSize;
    private long totalRecipes; // the total number of recipes brought if there was no paging
    private long totalPages;

    public boolean moreThanOnePage() {
        return getLastPage() > 1;
    }

    public Optional<Long> getPrevPage() {
        if (pageNumber > 1)
            return Optional.of(pageNumber - 1);
        return Optional.empty();
    }

    public Optional<Long> getNextPage() {
        if (pageNumber < getLastPage())
            return Optional.of(pageNumber + 1);
        return Optional.empty();
    }

    private long getLastPage() {
        return (long) Math.ceil((double) totalRecipes / pageSize);
    }
}
