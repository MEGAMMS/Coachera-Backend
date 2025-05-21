package com.coachera.backend.generator;

import com.coachera.backend.entity.Section;
import com.coachera.backend.entity.Week;
import org.instancio.Instancio;
import org.instancio.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SectionGenerator {

    public static List<Section> fromWeeks(List<Week> weeks) {
        if (weeks == null || weeks.isEmpty()) {
            throw new IllegalArgumentException("Weeks list cannot be null or empty");
        }

        Random random = new Random();
        
        return weeks.stream()
            .flatMap(week -> {
                // Ensure week is managed/persisted
                if (week.getId() == null) {
                    throw new IllegalStateException("Week must be persisted first (id cannot be null)");
                }

                AtomicInteger sectionOrderCounter = new AtomicInteger(0);
                int sectionsCount = random.nextInt(5) + 1; // 1-5 sections per week
                List<Section> sections = new ArrayList<>();

                for (int i = 0; i < sectionsCount; i++) {
                    try {
                        Section section = Instancio.of(Section.class)
                            .ignore(Select.field(Section::getId))
                            .supply(Select.field(Section::getWeek), () -> week)
                            .supply(Select.field(Section::getTitle), () -> "Section "+sectionOrderCounter)
                            .supply(Select.field(Section::getOrderIndex), sectionOrderCounter::getAndIncrement)
                            .create();

                        if (section.getWeek() == null) {
                            throw new IllegalStateException("Generated section has null week");
                        }

                        sections.add(section);
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to create Section for week " + week.getId(), e);
                    }
                }
                return sections.stream();
            })
            .collect(Collectors.toList());
    }
}