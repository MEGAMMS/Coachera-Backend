package com.coachera.backend.generator;

import com.coachera.backend.entity.Section;
import com.coachera.backend.entity.Module;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SectionGenerator {

    public static List<Section> fromModules(List<Module> modules) {
        if (modules == null || modules.isEmpty()) {
            throw new IllegalArgumentException("Modules list cannot be null or empty");
        }

        Random random = new Random();
        
        return modules.stream()
            .flatMap(module -> {
                // Ensure module is managed/persisted
                if (module.getId() == null) {
                    throw new IllegalStateException("Module must be persisted first (id cannot be null)");
                }

                AtomicInteger sectionOrderCounter = new AtomicInteger(0);
                int sectionsCount = random.nextInt(5) + 1; // 1-5 sections per module
                List<Section> sections = new ArrayList<>();

                for (int i = 0; i < sectionsCount; i++) {
                    try {
                        Section section = Instancio.of(Section.class)
                            .ignore(Select.field(Section::getId))
                            .supply(Select.field(Section::getModule), () -> module)
                            .supply(Select.field(Section::getTitle), () -> "Section "+sectionOrderCounter)
                            .supply(Select.field(Section::getOrderIndex), sectionOrderCounter::getAndIncrement)
                            .ignore(Select.field(Section::getMaterials))
                            .create();

                        if (section.getModule() == null) {
                            throw new IllegalStateException("Generated section has null module");
                        }

                        sections.add(section);
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to create Section for module " + module.getId(), e);
                    }
                }
                return sections.stream();
            })
            .collect(Collectors.toList());
    }
}