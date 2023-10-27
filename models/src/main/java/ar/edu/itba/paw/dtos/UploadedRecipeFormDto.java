package ar.edu.itba.paw.dtos;

import ar.edu.itba.paw.models.category.Category;
import ar.edu.itba.paw.models.ingredient.RecipeIngredientRecover;
import ar.edu.itba.paw.models.recipe.FullRecipe;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Iterator;
import java.util.List;

@Getter
@AllArgsConstructor
public class UploadedRecipeFormDto {
    private Integer servings;
    private Long[] categories;
    private String title;
    private List<byte[]> recipeImages;
    private String description;
    private Float[] quantitys;
    private String[] ingredients;
    private Long[] measureIds;
    private String[] instructions;
    private Integer totalTime;
    private Long difficultyId;
    private boolean isPrivate;
    private List<Long> imageIds;


    public UploadedRecipeFormDto(final FullRecipe recipe, final List<Category> categories, final List<RecipeIngredientRecover> recipeIngredients, final List<Long> imageIds) {
        this.servings = recipe.getServings();
        this.categories = categories.stream().map(Category::getCategoryId).toArray(Long[]::new);
        this.title = recipe.getTitle();
        this.imageIds = imageIds;
        this.description = recipe.getDescription();
        int ingredientsSize = recipeIngredients.size();
        quantitys = new Float[ingredientsSize];
        ingredients = new String[ingredientsSize];
        measureIds = new Long[ingredientsSize];
        for (int i = 0; i < ingredientsSize; i++) {
            RecipeIngredientRecover ing = recipeIngredients.get(i);
            quantitys[i] = ing.getQuantity();
            ingredients[i] = ing.getName();
            measureIds[i] = ing.getUnitId();
        }

        this.instructions = recipe.getInstructions();
        this.totalTime = recipe.getMinutes() + recipe.getHours() * 60;
        difficultyId = recipe.getDifficulty();
        this.isPrivate = recipe.getIsPrivate();

    }


    public Iterable<Long> getCategoriesIterable() {
        return new CategoriesIterable();
    }

    public Iterable<TripleIngredientSelectionDto> getIngredientsIterable() {
        return new IngredientsIterable(ingredients, quantitys, measureIds);
    }

    @AllArgsConstructor
    private static class IngredientsIterable implements Iterable<TripleIngredientSelectionDto> {

        private String[] ingredients;
        private Float[] qtys;
        private Long[] unitIds;

        @Override
        public Iterator<TripleIngredientSelectionDto> iterator() {
            return new IngredientsIterator();
        }

        private class IngredientsIterator implements Iterator<TripleIngredientSelectionDto> {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < qtys.length;//all ararys have same length
            }

            @Override
            public TripleIngredientSelectionDto next() {
                TripleIngredientSelectionDto out = new TripleIngredientSelectionDto(ingredients[current], qtys[current], unitIds[current]);
                current++;
                return out;
            }
        }
    }

    private class CategoriesIterable implements Iterable<Long> {

        @Override
        public Iterator<Long> iterator() {
            return new CategoriesIterator();
        }

        private class CategoriesIterator implements Iterator<Long> {

            int currentIdx = 0;

            @Override
            public boolean hasNext() {
                return currentIdx < categories.length;
            }

            @Override
            public Long next() {
                return categories[currentIdx++];
            }
        }
    }
}
