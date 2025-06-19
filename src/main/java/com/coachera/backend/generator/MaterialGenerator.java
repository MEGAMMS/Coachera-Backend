package com.coachera.backend.generator;

import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Material.MaterialType;
import com.coachera.backend.entity.Section;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MaterialGenerator {

    private static final String[] VIDEO_URLS = {
        "https://www.youtube.com/watch?v=Q7AOvWpIVHU",
        "https://youtu.be/DQdB7wFEygo?si=U93ySrbYrZf66v6h",
        "https://youtu.be/uJimjSDio_Y?si=ZSbD1yyb-SWwNDl3",
        "https://youtu.be/v5SDSWscaKY?si=rWo3uOUX07eu4Df8",
        "https://youtu.be/6CJiM3E2mAA?si=MpPyh6rKGOaTGE5s"
    };

    private static final String[] ARTICLE_CONTENTS = {
        "This is a comprehensive guide on the topic...",
        "In this article, we'll explore the key concepts...",
        "Learn the fundamentals with this detailed explanation...",
        "Advanced techniques and best practices covered here..."
    };

    private static final MaterialType[] MATERIAL_TYPES = MaterialType.values();

    public static List<Material> fromSections(List<Section> sections) {
        if (sections == null || sections.isEmpty()) {
            throw new IllegalArgumentException("Sections list cannot be null or empty");
        }

        Random random = new Random();
        
        return sections.stream()
            .flatMap(section -> {
                if (section.getId() == null) {
                    throw new IllegalStateException("Section must be persisted first (id cannot be null)");
                }

                AtomicInteger materialOrderCounter = new AtomicInteger(0);
                int materialsCount = random.nextInt(8) + 3; // 3-10 materials per section
                List<Material> materials = new ArrayList<>();

                for (int i = 0; i < materialsCount; i++) {
                    try {
                        MaterialType type = getRandomMaterialType(random);
                        
                        Material material = createMaterial(section, materialOrderCounter, random, type);
                        materials.add(material);
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to create Material for section " + section.getId(), e);
                    }
                }
                return materials.stream();
            })
            .collect(Collectors.toList());
    }

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
                    MaterialType type = getRandomMaterialType(random);
                    Material material = createMaterial(section, orderCounter, random, type);
                    materials.add(material);
                }
                return materials.stream();
            })
            .collect(Collectors.toList());
    }

    private static MaterialType getRandomMaterialType(Random random) {
        return MATERIAL_TYPES[random.nextInt(MATERIAL_TYPES.length)];
    }

    private static Material createMaterial(Section section, 
                                        AtomicInteger orderCounter, 
                                        Random random, 
                                        MaterialType type) {
        Material material = Instancio.of(Material.class)
            .ignore(Select.field(Material::getId))
            .supply(Select.field(Material::getSection), () -> section)
            .supply(Select.field(Material::getTitle), () -> 
                "Material " + orderCounter.get() + " - " + type.name())
            .supply(Select.field(Material::getOrderIndex), orderCounter::getAndIncrement)
            .supply(Select.field(Material::getType), () -> type)
            .ignore(Select.field(Material::getQuiz))
            .create();

        // Explicitly set content fields based on type
        // material.setVideoUrl(type == MaterialType.VIDEO ? 
        //     VIDEO_URLS[random.nextInt(VIDEO_URLS.length)] : null);
        material.setArticle(type == MaterialType.ARTICLE ? 
            ARTICLE_CONTENTS[random.nextInt(ARTICLE_CONTENTS.length)] : null);

        if (material.getSection() == null) {
            throw new IllegalStateException("Generated material has null section");
        }

        return material;
    }
}