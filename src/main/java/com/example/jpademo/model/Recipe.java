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
@NamedEntityGraph(
    name = "Recipe",
    attributeNodes = {
        @NamedAttributeNode(value = "ingredients"),
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
    private Set<Ingredient> ingredients = new HashSet<>();

    @ManyToMany
    @ToString.Exclude
    private Set<Category> categories = new HashSet<>();

}
