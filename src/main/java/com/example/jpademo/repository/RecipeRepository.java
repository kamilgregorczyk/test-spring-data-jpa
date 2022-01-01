package com.example.jpademo.repository;

import com.example.jpademo.model.Recipe;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {

    @Override
    List<Recipe> findAll();

    Optional<Recipe> findByTitle(String title);

    List<Recipe> findAllByTitle(String title);
}