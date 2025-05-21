package com.coachera.backend.service;

import com.coachera.backend.dto.CategoryDTO;
import com.coachera.backend.entity.Category;
import com.coachera.backend.exception.ConflictException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;


    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new ConflictException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return new CategoryDTO(savedCategory);
    }

    
    public CategoryDTO getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return new CategoryDTO(category);
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryDTO(category))
                .collect(Collectors.toList());
    }

    public CategoryDTO updateCategory(Integer id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!existingCategory.getName().equals(categoryDTO.getName()) && 
            categoryRepository.existsByName(categoryDTO.getName())) {
            throw new ConflictException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        modelMapper.map(categoryDTO, existingCategory);
        Category updatedCategory = categoryRepository.save(existingCategory);
        return new CategoryDTO(updatedCategory);
    }

    // حذف فئة
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}