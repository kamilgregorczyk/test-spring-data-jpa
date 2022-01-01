package com.example.jpademo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter()
@ToString
@NoArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;

    @ManyToMany(mappedBy = "categories")
    @ToString.Exclude
    private Set<Recipe> recipes = new HashSet<>();
}
