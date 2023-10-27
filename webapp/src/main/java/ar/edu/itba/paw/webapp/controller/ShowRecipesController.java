package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.webapp.dtos.FilterAutocompleteDataDto;
import ar.edu.itba.paw.dtos.RecipesAndSize;
import ar.edu.itba.paw.dtos.ShowRecipesDto;
import ar.edu.itba.paw.enums.AvailableDifficultiesForSort;
import ar.edu.itba.paw.enums.ShowRecipePages;
import ar.edu.itba.paw.enums.SortOptions;
import ar.edu.itba.paw.models.recipe.Recipe;
import ar.edu.itba.paw.servicesInterface.RecipeService;
import ar.edu.itba.paw.servicesInterface.category.CategoryService;
import ar.edu.itba.paw.servicesInterface.ingredient.IngredientsService;
import ar.edu.itba.paw.webapp.exceptions.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


//Controller handles recipe request once a particular recipe is clicked
@Controller
public class ShowRecipesController extends BaseUserTemplateController {

    private final int PAGESIZE = 9;
    private RecipeService rs;
    private CategoryService cs;
    private IngredientsService is;

    private ModelAndView showRecipes(
            String pageHeadTitle,
            String pageTitle,
            ShowRecipePages showRecipePage,
            Optional<AvailableDifficultiesForSort> difficulty,
            Optional<SortOptions> sort,
            List<Integer> selectedCategories,
            Optional<String> selectedIngredients,
            Optional<String> searchBarQuery,
            Optional<Long> page
    ) {
        if (page.isPresent() && page.get() < 1)
            throw new InvalidArgumentException();

        Optional<Long> userId = getCurrentUserId();
        RecipesAndSize recipesAndSize = rs.getRecipesByFilter(
                difficulty, selectedIngredients, selectedCategories,
                sort, searchBarQuery, showRecipePage, userId, page, Optional.of(PAGESIZE)
        );
        List<Recipe> recipeList = recipesAndSize.getRecipeList();
        long totalRecipes = recipesAndSize.getTotalRecipes();
        long totalPages = (long) Math.ceil((double) totalRecipes / PAGESIZE);
        ModelAndView showRecipes = new ModelAndView("/showRecipes");

        ShowRecipesDto dto = new ShowRecipesDto(
                pageHeadTitle,
                pageTitle,
                recipeList,
                difficulty.orElse(AvailableDifficultiesForSort.ALL),
                sort.orElse(SortOptions.NONE),
                selectedCategories.stream().map(Object::toString).collect(Collectors.toList()), //el checkbox de materialize necesita strings
                selectedIngredients.orElse(""),
                searchBarQuery.orElse(""),
                page.orElse(1L),
                PAGESIZE,
                totalRecipes,
                totalPages
        );

        showRecipes.addObject("dto", dto);
        showRecipes.addObject("autocompleteDto", getFilterAutocompleteData());
        return showRecipes;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView discover(
            @RequestParam(value = "difficulty") Optional<AvailableDifficultiesForSort> difficulty,
            @RequestParam(value = "sort") Optional<SortOptions> sort,
            @RequestParam(value = "selectedIngredients") Optional<String> selectedIngredients,
            @RequestParam(value = "selectedCategories", required = false, defaultValue = "") List<Integer> selectedCategories,
            @RequestParam(value = "searchBarQuery") Optional<String> searchBarQuery,
            @RequestParam(value = "page") Optional<Long> page
    ) {
        return showRecipes(
                "Discover",
                "Discover",
                ShowRecipePages.DISCOVER,
                difficulty,
                sort,
                selectedCategories,
                selectedIngredients,
                searchBarQuery,
                page
        );
    }

    @RequestMapping(value = "/myRecipes", method = RequestMethod.GET)
    public ModelAndView myRecipes(
            @RequestParam(value = "difficulty") Optional<AvailableDifficultiesForSort> difficulty,
            @RequestParam(value = "sort") Optional<SortOptions> sort,
            @RequestParam(value = "selectedIngredients") Optional<String> selectedIngredients,
            @RequestParam(value = "selectedCategories", required = false, defaultValue = "") List<Integer> selectedCategories,
            @RequestParam(value = "searchBarQuery") Optional<String> searchBarQuery,
            @RequestParam(value = "page") Optional<Long> page

    ) {
        return showRecipes(
                "My Recipes",
                "MyRecipes",
                ShowRecipePages.MY_RECIPES,
                difficulty,
                sort,
                selectedCategories,
                selectedIngredients,
                searchBarQuery,
                page
        );
    }

    @RequestMapping(value = "/saved", method = RequestMethod.GET)
    public ModelAndView saved(
            @RequestParam(value = "difficulty") Optional<AvailableDifficultiesForSort> difficulty,
            @RequestParam(value = "sort") Optional<SortOptions> sort,
            @RequestParam(value = "selectedIngredients") Optional<String> selectedIngredients,
            @RequestParam(value = "selectedCategories", required = false, defaultValue = "") List<Integer> selectedCategories,
            @RequestParam(value = "searchBarQuery") Optional<String> searchBarQuery,
            @RequestParam(value = "page") Optional<Long> page
    ) {
        return showRecipes(
                "Saved",
                "Saved",
                ShowRecipePages.SAVED,
                difficulty,
                sort,
                selectedCategories,
                selectedIngredients,
                searchBarQuery,
                page
        );
    }

    private FilterAutocompleteDataDto getFilterAutocompleteData() {
        Map<String, Object> autoCompleteMap = new HashMap<>();
        Map<Long, String> categoryMap = new HashMap<>();

        is.getAllIngredients().forEach(ingredient -> autoCompleteMap.put(ingredient.getName(), null));
        cs.getAllCategories().forEach(category -> categoryMap.put(category.getCategoryId(), category.getName()));
        return new FilterAutocompleteDataDto(autoCompleteMap, categoryMap);
    }


    @RequestMapping("/upload-success")
    public ModelAndView getUploadSuccessScreen() {
        return new ModelAndView("/successScreen");
    }

    @Autowired
    public void setRecipeService(final RecipeService rs) {
        this.rs = rs;
    }

    @Autowired
    public void setCategoryService(final CategoryService cs) {
        this.cs = cs;
    }

    @Autowired
    public void setIngredientsService(final IngredientsService is) {
        this.is = is;
    }

}
