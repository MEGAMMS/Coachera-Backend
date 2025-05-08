package com.coachera.backend.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Category;

public class CategoryGenerator {
    public static List<Category> fromNames(List<String> names) {
        return names.stream().map(name ->
            Instancio.of(Category.class)
                .ignore(Select.field(Category::getId))
                .supply(Select.field(Category::getName), () -> name)
                .create()
        ).collect(Collectors.toList());
    }
}
