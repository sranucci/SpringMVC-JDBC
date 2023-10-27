package ar.edu.itba.paw.models.ingredient;

import lombok.Getter;

import java.util.Objects;

@Getter
public class RecipeIngredient extends TemplateRecipeIngredient {


    private final String units;

    public RecipeIngredient(String name, String units, float quantity) {
        super(name, quantity);
        this.units = units;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeIngredient that = (RecipeIngredient) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
