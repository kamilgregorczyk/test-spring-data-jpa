package com.example.jpademo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter()
@ToString
@NoArgsConstructor
@Entity
@Table(
    indexes = {
        @Index(name = "title_idx", columnList = "title")
    }
)
@NamedEntityGraph(
    name = "Recipe",
    attributeNodes = {
        @NamedAttributeNode(value = "notes"),
        @NamedAttributeNode("categories")
    }
)
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String description;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipe")
    @ToString.Exclude
    private Set<Note> notes = new HashSet<>();


    @ManyToMany
    @JoinTable(name = "recipe_to_category",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id"))
    @ToString.Exclude
    private Set<Category> categories = new HashSet<>();

}
