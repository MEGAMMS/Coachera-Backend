package com.coachera.backend.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Category;
import com.coachera.backend.entity.Course;

public class CategoryGenerator {
    public static List<Category> fromNames(List<String> names) {
        if (names == null) {
            throw new IllegalArgumentException("Names list cannot be null");
        }

        return names.stream()
                .map(name -> {
                    try {
                        if (name == null) {
                            throw new IllegalArgumentException("Category name cannot be null");
                        }

                        Category category = Instancio.of(Category.class)
                                .ignore(Select.field(Category::getId))
                                .supply(Select.field(Category::getName), () -> name)
                                .ignore(Select.field(Category::getCourses))
                                .create();

                        if (category == null) {
                            throw new IllegalStateException("Instancio returned null Category for name: " + name);
                        }
                        return category;
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to create Category: " + name, e);
                    }
                })
                .collect(Collectors.toList());
    }

    public static List<Category> fromCourses(List<Course> courses, List<String> names) {
        List<Category> categories = fromNames(names);

        // Distribute courses evenly across categories
        for (int i = 0; i < courses.size(); i++) {
            Category category = categories.get(i % categories.size());
            category.addCourse(courses.get(i));
        }

        return categories;
    }
}
