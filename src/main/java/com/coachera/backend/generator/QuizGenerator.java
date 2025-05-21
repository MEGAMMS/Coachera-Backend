package com.coachera.backend.generator;

import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Quiz;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.List;
import java.util.stream.Collectors;

public class QuizGenerator {

    public static List<Quiz> fromMaterials(List<Material> materials) {
        if (materials == null || materials.isEmpty()) {
            throw new IllegalArgumentException("Materials list cannot be null or empty");
        }

        return materials.stream()
            .map(material -> {
                // Ensure material is managed/persisted
                if (material.getId() == null) {
                    throw new IllegalStateException("Material must be persisted first (id cannot be null)");
                }

                try {
                    Quiz quiz = Instancio.of(Quiz.class)
                        .ignore(Select.field(Quiz::getId))
                        .supply(Select.field(Quiz::getMaterial), () -> material)
                        .create();

                    if (quiz.getMaterial() == null) {
                        throw new IllegalStateException("Generated quiz has null material");
                    }

                    return quiz;
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to create Quiz for material " + material.getId(), e);
                }
            })
            .collect(Collectors.toList());
    }
}