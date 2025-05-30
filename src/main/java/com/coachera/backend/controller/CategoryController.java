package com.coachera.backend.controller;

import com.coachera.backend.dto.ApiResponse;
import com.coachera.backend.dto.CategoryDTO;
import com.coachera.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ApiResponse<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {

        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return ApiResponse.created("Category was created", createdCategory);

    }

    @GetMapping("/{id}")
    public ApiResponse<?> getCategoryById(@PathVariable Integer id) {

        CategoryDTO category = categoryService.getCategoryById(id);
        return ApiResponse.success(category);

    }

    @GetMapping
    public ApiResponse<?> getAllCategories() {

        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ApiResponse.success(categories);

    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ApiResponse.success("category was updated", updatedCategory);

    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ApiResponse.noContentResponse();

    }
}
