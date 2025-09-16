package com.songstock.service;

import com.songstock.entity.Category;
import com.songstock.exception.ResourceAlreadyExistsException;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActive(true);
    }

    @Transactional(readOnly = true)
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + name));
    }

    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new ResourceAlreadyExistsException("La categoría ya existe: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category category) {
        Category existingCategory = getCategoryById(id);

        if (!existingCategory.getName().equals(category.getName()) &&
                categoryRepository.existsByName(category.getName())) {
            throw new ResourceAlreadyExistsException("La categoría ya existe: " + category.getName());
        }

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setIsActive(category.getIsActive());

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
