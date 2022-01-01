package com.example.jpademo.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
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
