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
        @Index(name = "title_idx", columnList = "title", unique = true)
    }
)
@NamedEntityGraph(
    name = "Category",
    attributeNodes = {
        @NamedAttributeNode("recipes")
    }
)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;

    @ManyToMany(mappedBy = "categories")
    @ToString.Exclude
    private Set<Recipe> recipes = new HashSet<>();
}
