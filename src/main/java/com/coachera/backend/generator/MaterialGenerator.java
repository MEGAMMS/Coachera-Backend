package com.coachera.backend.generator;

import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Section;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MaterialGenerator {

    public static List<Material> fromSections(List<Section> sections) {
        if (sections == null || sections.isEmpty()) {
            throw new IllegalArgumentException("Sections list cannot be null or empty");
        }

        Random random = new Random();
        
        return sections.stream()
            .flatMap(section -> {
                // Ensure section is managed/persisted
                if (section.getId() == null) {
                    throw new IllegalStateException("Section must be persisted first (id cannot be null)");
                }

                AtomicInteger materialOrderCounter = new AtomicInteger(0);
                int materialsCount = random.nextInt(8) + 3; // 3-10 materials per section
                List<Material> materials = new ArrayList<>();

                for (int i = 0; i < materialsCount; i++) {
                    try {
                        Material material = Instancio.of(Material.class)
                            .ignore(Select.field(Material::getId))
                            .supply(Select.field(Material::getSection), () -> section)
                            .supply(Select.field(Material::getTitle), () -> 
                                "Material " + materialOrderCounter + " - " + generateRandomMaterialType())
                            .supply(Select.field(Material::getOrderIndex), materialOrderCounter::getAndIncrement)
                            .create();

                        if (material.getSection() == null) {
                            throw new IllegalStateException("Generated material has null section");
                        }

                        materials.add(material);
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to create Material for section " + section.getId(), e);
                    }
                }
                return materials.stream();
            })
            .collect(Collectors.toList());
    }

    private static String generateRandomMaterialType() {
        String[] types = {"Video", "PDF", "Article", "Assignment", "Slides"};
        return types[new Random().nextInt(types.length)];
    }

    // Alternative version with customizable material count range
    public static List<Material> fromSections(List<Section> sections, int minMaterials, int maxMaterials) {
        if (minMaterials < 1 || maxMaterials < minMaterials) {
            throw new IllegalArgumentException("Invalid material count range");
        }

        Random random = new Random();
        
        return sections.stream()
            .flatMap(section -> {
                AtomicInteger orderCounter = new AtomicInteger(0);
                int count = random.nextInt(maxMaterials - minMaterials + 1) + minMaterials;
                List<Material> materials = new ArrayList<>(count);
                
                for (int i = 0; i < count; i++) {
                    Material material = Instancio.of(Material.class)
                        .ignore(Select.field(Material::getId))
                        .supply(Select.field(Material::getSection), () -> section)
                        .supply(Select.field(Material::getTitle), () -> "Material " + orderCounter)
                        .supply(Select.field(Material::getOrderIndex), orderCounter::getAndIncrement)
                        .create();
                    materials.add(material);
                }
                return materials.stream();
            })
            .collect(Collectors.toList());
    }
}