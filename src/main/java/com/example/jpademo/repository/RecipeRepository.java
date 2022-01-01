package com.example.jpademo.repository;

import com.example.jpademo.model.Recipe;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @EntityGraph(value = "Recipe")
    Optional<Recipe> findByTitle(String title);

    @EntityGraph(value = "Recipe")
    Optional<Recipe> findById(Long id);
}
