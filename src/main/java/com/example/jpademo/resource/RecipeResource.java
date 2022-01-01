package com.example.jpademo.resource;

import com.example.jpademo.model.Category;
import com.example.jpademo.model.Note;
import com.example.jpademo.model.Recipe;
import com.example.jpademo.repository.CategoryRepository;
import com.example.jpademo.repository.RecipeRepository;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.*;

@RestController
public class RecipeResource {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public RecipeResource(RecipeRepository recipeRepository,
                          CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping("/recipes")
    @Transactional
    public ResponseEntity createRecipe(@RequestBody @Valid CreateRecipeRequest request) {
        final var recipe = new Recipe();
        recipe.setTitle(request.title);
        recipe.setDescription(request.description);
        recipe.setCategories(request.categories.stream().map(c -> getOrCreateCategory(c, recipe)).collect(toUnmodifiableSet()));
        recipe.setNotes(request.notes.stream().map(i -> createNote(i, recipe)).collect(toUnmodifiableSet()));
        return status(CREATED).body(Map.of("id", recipeRepository.save(recipe).getId()));
    }

    @GetMapping("/recipes/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable("id") Long id) {
        return recipeRepository.findById(id)
            .map(recipe -> ok().body(recipeToResponse(recipe)))
            .orElseGet(() -> notFound().build());
    }

    private Category getOrCreateCategory(CreateCategoryRequest categoryRequest, Recipe recipe) {
        return categoryRepository.findByTitle(categoryRequest.getTitle())
            .map(category -> {
                category.getRecipes().add(recipe);
                return category;
            })
            .orElseGet(() -> {
                final var category = new Category();
                category.setTitle(categoryRequest.getTitle());
                category.getRecipes().add(recipe);
                return categoryRepository.save(category);
            });
    }

    private Note createNote(CreateNoteRequest noteRequest, Recipe recipe) {
        final var note = new Note();
        note.setTitle(noteRequest.title);
        note.setDescription(noteRequest.description);
        note.setRecipe(recipe);
        return note;

    }

    private static RecipeResponse recipeToResponse(Recipe recipe) {
        return RecipeResponse.recipeResponse()
            .title(recipe.getTitle())
            .description(recipe.getDescription())
            .notes(recipe.getNotes().stream()
                .map(RecipeResource::noteToResponse)
                .sorted(comparing(NoteResponse::getTitle))
                .collect(toUnmodifiableList()))
            .categories(recipe.getCategories().stream()
                .map(RecipeResource::categoryToResponse)
                .sorted(comparing(CategoryResponse::getTitle))
                .collect(toUnmodifiableList()))
            .build();
    }

    private static NoteResponse noteToResponse(Note note) {
        return NoteResponse.noteResponse().title(note.getTitle()).description(note.getDescription()).build();
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
        public Set<CreateNoteRequest> notes;

        @NotEmpty
        public Set<CreateCategoryRequest> categories;
    }

    @Data
    static class CreateNoteRequest {
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
        public List<NoteResponse> notes;
        public List<CategoryResponse> categories;
    }

    @Data
    @Builder(builderMethodName = "noteResponse")
    static class NoteResponse {
        public String title;
        public String description;
    }

    @Data
    @Builder(builderMethodName = "categoryResponse")
    static class CategoryResponse {
        public String title;
    }
}
