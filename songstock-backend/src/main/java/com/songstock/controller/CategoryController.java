package com.songstock.controller;

import com.songstock.dto.ApiResponse;
import com.songstock.entity.Category;
import com.songstock.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las categorías.
 * Expone endpoints CRUD con validaciones y seguridad.
 */
@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*", maxAge = 3600) // Permitir CORS desde cualquier origen
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Obtener todas las categorías
     * 
     * @return Lista de categorías (activas e inactivas)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categorías obtenidas exitosamente", categories));
    }

    /**
     * Obtener una categoría por su ID
     * 
     * @param id Identificador de la categoría
     * @return Categoría encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Categoría encontrada", category));
    }

    /**
     * Obtener todas las categorías activas
     * 
     * @return Lista de categorías activas
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Category>>> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(ApiResponse.success("Categorías activas obtenidas exitosamente", categories));
    }

    /**
     * Crear una nueva categoría (solo ADMIN)
     * 
     * @param category Datos de la categoría a crear
     * @return Categoría creada
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(ApiResponse.success("Categoría creada exitosamente", createdCategory));
    }

    /**
     * Actualizar una categoría existente (solo ADMIN)
     * 
     * @param id       ID de la categoría a actualizar
     * @param category Nuevos datos de la categoría
     * @return Categoría actualizada
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(ApiResponse.success("Categoría actualizada exitosamente", updatedCategory));
    }

    /**
     * Eliminar una categoría (solo ADMIN)
     * 
     * @param id ID de la categoría a eliminar
     * @return Mensaje de éxito
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Categoría eliminada exitosamente"));
    }
}
