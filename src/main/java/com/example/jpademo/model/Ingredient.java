package com.example.jpademo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(
    indexes = {
        @Index(name = "title_idx", columnList = "title", unique = true),
        @Index(name = "recipe_id_idx", columnList = "recipe_id")
    }
)
@NamedEntityGraph(
    name = "Ingredient",
    attributeNodes = {
        @NamedAttributeNode("recipe")
    }
)
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    private String description;

    @ManyToOne
    private Recipe recipe;

}
