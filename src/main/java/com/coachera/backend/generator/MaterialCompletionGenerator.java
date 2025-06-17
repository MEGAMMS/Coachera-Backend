package com.coachera.backend.generator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Enrollment;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.MaterialCompletion;
import com.coachera.backend.entity.enums.CompletionState;
import com.coachera.backend.entity.enums.CompletionTriggerType;

public class MaterialCompletionGenerator {
    private static final Random random = new Random();

    public static List<MaterialCompletion> forEnrollmentsAndMaterials(
            List<Enrollment> enrollments, 
            List<Material> materials) {
        
        return enrollments.stream()
            .flatMap(enrollment -> {
                // Filter materials that belong to the enrollment's course
                List<Material> courseMaterials = materials.stream()
                    .filter(material -> material.getSection().getModule().getCourse().equals(enrollment.getCourse()))
                    .collect(Collectors.toList());
                
                if (courseMaterials.isEmpty()) {
                    return java.util.stream.Stream.empty();
                }
                
                // Determine random number of materials for this enrollment (0 to all)
                int materialsToComplete = random.nextInt(courseMaterials.size() + 1);
                
                // Select random subset of materials
                List<Material> randomMaterials = random.ints(0, courseMaterials.size())
                    .distinct()
                    .limit(materialsToComplete)
                    .mapToObj(courseMaterials::get)
                    .collect(Collectors.toList());
                
                return randomMaterials.stream().map(material ->
                    Instancio.of(MaterialCompletion.class)
                        .ignore(Select.field(MaterialCompletion::getId))
                        .supply(Select.field(MaterialCompletion::getEnrollment), () -> enrollment)
                        .supply(Select.field(MaterialCompletion::getMaterial), () -> material)
                        .supply(Select.field(MaterialCompletion::isCompleted), () -> true) // All generated are completed
                        .supply(Select.field(MaterialCompletion::getCompletionDate), 
                            () -> LocalDateTime.now().minusDays(random.nextInt(30)))
                        .supply(Select.field(MaterialCompletion::getCompletionState), 
                            () -> CompletionState.values()[random.nextInt(CompletionState.values().length)])
                        .supply(Select.field(MaterialCompletion::getTriggerType), 
                            () -> CompletionTriggerType.values()[random.nextInt(CompletionTriggerType.values().length)])
                        .create()
                );
            })
            .collect(Collectors.toList());
    }

    // Alternative method for specific completion state
    public static List<MaterialCompletion> forEnrollmentsAndMaterialsWithState(
            List<Enrollment> enrollments, 
            List<Material> materials,
            CompletionState state) {
        
        return forEnrollmentsAndMaterials(enrollments, materials).stream()
            .map(mc -> {
                mc.setCompletionState(state);
                return mc;
            })
            .collect(Collectors.toList());
    }
}