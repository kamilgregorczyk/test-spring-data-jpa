package com.example.jpademo.repository;

import com.example.jpademo.model.Note;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    @EntityGraph(value = "Note")
    Optional<Note> findByTitle(String title);
}
