package com.coachera.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.SearchRequest;
import com.coachera.backend.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Entity;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "Generic search endpoints for all entities")
public class SearchController {

    private final SearchService searchService;
    private final Map<String, Class<?>> entityRegistry = new HashMap<>();

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
        // Register your entities here
        registerEntity("courses", com.coachera.backend.entity.Course.class);
        registerEntity("students", com.coachera.backend.entity.Student.class);
        registerEntity("instructors", com.coachera.backend.entity.Instructor.class);
        registerEntity("categories", com.coachera.backend.entity.Category.class);
        registerEntity("skills", com.coachera.backend.entity.Skill.class);
        registerEntity("learning-paths", com.coachera.backend.entity.LearningPath.class);
        registerEntity("materials", com.coachera.backend.entity.Material.class);
        registerEntity("sections", com.coachera.backend.entity.Section.class);
        registerEntity("quizzes", com.coachera.backend.entity.Quiz.class);
        registerEntity("questions", com.coachera.backend.entity.Question.class);
        registerEntity("reviews", com.coachera.backend.entity.Review.class);
        registerEntity("certificates", com.coachera.backend.entity.Certificate.class);
    }

    private void registerEntity(String name, Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Class " + entityClass.getName() + " is not an entity");
        }
        entityRegistry.put(name, entityClass);
    }

    @PostMapping("/{entityType}")
    @Operation(summary = "Search entities", description = "Search and filter entities of the specified type with pagination support")
    public ApiResponse<?> search(
            @Parameter(description = "Type of entity to search for", example = "courses") @PathVariable String entityType,
            @RequestBody SearchRequest searchRequest) {

        Class<?> entityClass = entityRegistry.get(entityType);
        if (entityClass == null) {
            return ApiResponse.error(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Invalid entity type. Available types: " + entityRegistry.keySet());
        }

        Page<?> results = searchService.search(entityClass, searchRequest);
        return ApiResponse.paginated(results);
    }

    @GetMapping("/entities")
    @Operation(summary = "Get available entities", description = "Returns a list of entity types that can be searched")
    public ApiResponse<?> getAvailableEntities() {
        return ApiResponse.success("Available entity types retrieved", entityRegistry.keySet());
    }
}
