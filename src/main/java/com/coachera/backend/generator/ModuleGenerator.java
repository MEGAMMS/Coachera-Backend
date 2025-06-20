package com.coachera.backend.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.instancio.Instancio;
import org.instancio.Select;

import com.coachera.backend.entity.Course;
import com.coachera.backend.entity.Module;

public class ModuleGenerator {

    public static List<Module> fromCourses(List<Course> courses) {
        Random random = new Random();

        return courses.stream()
                .flatMap(course -> {
                    AtomicInteger orderCounter = new AtomicInteger(0);
                    int modulesCount = random.nextInt(10) + 1; // Random number between 1-10
                    List<Module> modules = new ArrayList<>();

                    for (int i = 0; i < modulesCount; i++) {
                        try {
                            Module module = Instancio.of(Module.class)
                                    .ignore(Select.field(Module::getId))
                                    .supply(Select.field(Module::getCourse), () -> course)
                                    .supply(Select.field(Module::getTitle), () -> "Module " + orderCounter)
                                    .supply(Select.field(Module::getOrderIndex), orderCounter::getAndIncrement)
                                    .ignore(Select.field(Module::getSections))
                                    .create();

                            if (module == null) {
                                throw new IllegalStateException("Instancio returned null Module");
                            }
                            modules.add(module);
                        } catch (Exception e) {
                            throw new IllegalStateException("Failed to create Module for course " + course.getId(), e);
                        }
                    }
                    return modules.stream();
                })
                .collect(Collectors.toList());
    }

    public static List<Module> fromCourses(List<Course> courses, int startingOrderIndex) {
        AtomicInteger orderCounter = new AtomicInteger(startingOrderIndex);

        return courses.stream()
                .map(course -> {
                    try {
                        Module module = Instancio.of(Module.class)
                                .ignore(Select.field(Module::getId))
                                .supply(Select.field(Module::getCourse), () -> course)
                                .supply(Select.field(Module::getOrderIndex), orderCounter::getAndIncrement)
                                .create();

                        if (module == null) {
                            throw new IllegalStateException("Instancio returned null Module");
                        }
                        return module;
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to create Module for course " + course.getId(), e);
                    }
                })
                .collect(Collectors.toList());
    }
}