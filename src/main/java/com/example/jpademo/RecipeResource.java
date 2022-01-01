package com.example.jpademo;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.springframework.http.ResponseEntity.*;

@RestController
public class RecipeResource {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public RecipeResource(RecipeRepository recipeRepository, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping("/recipes")
    public ResponseEntity createRecipe(@RequestBody @Valid CreateRecipeRequest request) {
        final var recipe = new Recipe();
        recipe.setTitle(request.title);
        recipe.setDescription(request.description);
        recipe.setCategories(request.categories.stream().map(c -> getOrCreateCategory(c, recipe)).collect(toUnmodifiableSet()));
        recipe.setIngredients(request.ingredients.stream().map(i -> {
            final var ingredient = new Ingredient();
            ingredient.setTitle(i.title);
            ingredient.setDescription(i.description);
            ingredient.setRecipe(recipe);
            return ingredient;
        }).collect(toUnmodifiableSet()));
        return status(HttpStatus.CREATED).body(Map.of("id", recipeRepository.save(recipe).getId()));
    }

    @GetMapping("/recipes/{id}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable("id") Long id) {
        return recipeRepository.findById(id)
            .map(recipe -> ok().body(recipeToResponse(recipe))).orElseGet(() -> notFound().build());
    }

    private Category getOrCreateCategory(CreateCategoryRequest c, Recipe recipe) {
        return categoryRepository.findByTitle(c.getTitle())
            .map(category -> {
                category.getRecipes().add(recipe);
                return category;
            })
            .orElseGet(() -> {
                final var category = new Category();
                category.setTitle(c.getTitle());
                category.getRecipes().add(recipe);
                return categoryRepository.save(category);
            });
    }

    private static RecipeResponse recipeToResponse(Recipe recipe) {
        return RecipeResponse.recipeResponse()
            .title(recipe.getTitle())
            .description(recipe.getDescription())
            .ingredients(recipe.getIngredients().stream()
                .map(RecipeResource::ingredientToResponse)
                .sorted(comparing(IngredientResponse::getTitle))
                .collect(toUnmodifiableList()))
            .categories(recipe.getCategories().stream()
                .map(RecipeResource::categoryToResponse)
                .sorted(comparing(CategoryResponse::getTitle))
                .collect(toUnmodifiableList()))
            .build();
    }

    private static IngredientResponse ingredientToResponse(Ingredient ingredient) {
        return IngredientResponse.ingredientResponse().title(ingredient.getTitle()).description(ingredient.getDescription()).build();
    }

    public static CategoryResponse categoryToResponse(Category category) {
        return CategoryResponse.categoryResponse()
            .title(category.getTitle())
            .build();
    }

    @Data
    static class CreateRecipeRequest {
        @NotBlank
        public String title;

        @NotBlank
        public String description;

        @NotEmpty
        public Set<CreateIngredientRequest> ingredients;

        @NotEmpty
        public Set<CreateCategoryRequest> categories;
    }

    @Data
    static class CreateIngredientRequest {
        @NotBlank
        public String title;

        @NotBlank
        public String description;
    }

    @Data
    static class CreateCategoryRequest {
        @NotBlank
        public String title;
    }

    @Data
    @Builder(builderMethodName = "recipeResponse")
    static class RecipeResponse {
        public String title;
        public String description;
        public List<IngredientResponse> ingredients;
        public List<CategoryResponse> categories;
    }

    @Data
    @Builder(builderMethodName = "ingredientResponse")
    static class IngredientResponse {
        public String title;
        public String description;
    }

    @Data
    @Builder(builderMethodName = "categoryResponse")
    static class CategoryResponse {
        public String title;
    }
}
