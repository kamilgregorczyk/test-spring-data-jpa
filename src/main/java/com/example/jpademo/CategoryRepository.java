package com.example.jpademo;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    @Override
    List<Category> findAll();

    Optional<Category> findByTitle(String title);

    List<Category> findAllByTitle(String title);
}
