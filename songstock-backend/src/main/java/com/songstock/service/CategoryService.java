package com.songstock.service;

import com.songstock.entity.Category;
import com.songstock.exception.ResourceAlreadyExistsException;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio encargado de la lógica de negocio relacionada con las categorías.
 * Se apoya en el {@link CategoryRepository} para realizar operaciones en la
 * base de datos.
 * 
 * Anotaciones:
 * - {@link Service}: indica que esta clase es un componente de servicio en
 * Spring.
 * - {@link Transactional}: asegura que las operaciones se ejecuten en un
 * contexto transaccional.
 */
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param categoryRepository Repositorio para acceder a las entidades
     *                           {@link Category}.
     */
    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Obtiene todas las categorías registradas en la base de datos.
     * 
     * @return Lista de todas las categorías.
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Busca una categoría por su ID.
     *
     * @param id Identificador de la categoría.
     * @return La categoría encontrada.
     * @throws ResourceNotFoundException Si no existe una categoría con el ID
     *                                   especificado.
     */
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    /**
     * Obtiene todas las categorías activas.
     *
     * @return Lista de categorías que están activas (isActive = true).
     */
    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActive(true);
    }

    /**
     * Busca una categoría por su nombre.
     *
     * @param name Nombre de la categoría.
     * @return La categoría encontrada.
     * @throws ResourceNotFoundException Si no existe una categoría con el nombre
     *                                   indicado.
     */
    @Transactional(readOnly = true)
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + name));
    }

    /**
     * Crea una nueva categoría en la base de datos.
     *
     * @param category Objeto {@link Category} con la información de la nueva
     *                 categoría.
     * @return La categoría guardada.
     * @throws ResourceAlreadyExistsException Si ya existe una categoría con el
     *                                        mismo nombre.
     */
    public Category createCategory(Category category) {
        // Validación: no se permiten categorías duplicadas por nombre
        if (categoryRepository.existsByName(category.getName())) {
            throw new ResourceAlreadyExistsException("La categoría ya existe: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    /**
     * Actualiza los datos de una categoría existente.
     *
     * @param id       Identificador de la categoría a actualizar.
     * @param category Datos actualizados de la categoría.
     * @return La categoría actualizada.
     * @throws ResourceAlreadyExistsException Si el nuevo nombre ya existe en otra
     *                                        categoría.
     * @throws ResourceNotFoundException      Si no se encuentra la categoría a
     *                                        actualizar.
     */
    public Category updateCategory(Long id, Category category) {
        // Busca la categoría existente o lanza excepción si no existe
        Category existingCategory = getCategoryById(id);

        // Validación: si cambia el nombre, verificar que no exista duplicado
        if (!existingCategory.getName().equals(category.getName()) &&
                categoryRepository.existsByName(category.getName())) {
            throw new ResourceAlreadyExistsException("La categoría ya existe: " + category.getName());
        }

        // Se actualizan los campos editables
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setIsActive(category.getIsActive());

        return categoryRepository.save(existingCategory);
    }

    /**
     * Elimina una categoría de la base de datos por su ID.
     *
     * @param id Identificador de la categoría a eliminar.
     * @throws ResourceNotFoundException Si no existe la categoría con el ID
     *                                   proporcionado.
     */
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
