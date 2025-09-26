package com.songstock.service;

import com.songstock.dto.UserRegistrationDTO;
import com.songstock.entity.User;
import com.songstock.entity.UserRole;
import com.songstock.exception.ResourceAlreadyExistsException;
import com.songstock.exception.ResourceNotFoundException;
import com.songstock.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con la entidad
 * {@link User}.
 * 
 * Incluye lógica de negocio para crear, actualizar, eliminar, activar,
 * desactivar y consultar usuarios.
 * También maneja validaciones de unicidad en username y email.
 *
 * Anotaciones principales:
 * - {@link Service}: Indica que es un componente de servicio dentro de Spring.
 * - {@link Transactional}: Garantiza la consistencia transaccional en las
 * operaciones de base de datos.
 */
@Service
@Transactional
public class UserService {

    /** Repositorio para acceder a la base de datos de usuarios. */
    private final UserRepository userRepository;

    /** Codificador de contraseñas para almacenar las claves de forma segura. */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param userRepository  Repositorio de usuarios.
     * @param passwordEncoder Codificador de contraseñas.
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return Lista de usuarios.
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id Identificador único del usuario.
     * @return Usuario encontrado.
     * @throws ResourceNotFoundException si el usuario no existe.
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario.
     * @return Usuario encontrado.
     * @throws ResourceNotFoundException si no existe el usuario.
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email Correo electrónico.
     * @return Usuario encontrado.
     * @throws ResourceNotFoundException si no existe el usuario.
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    /**
     * Busca un usuario por nombre de usuario o email.
     *
     * @param usernameOrEmail Nombre de usuario o correo electrónico.
     * @return Usuario encontrado en un {@link Optional}.
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    /**
     * Obtiene todos los usuarios que pertenecen a un rol específico.
     *
     * @param role Rol de usuario ({@link UserRole}).
     * @return Lista de usuarios con el rol indicado.
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    /**
     * Obtiene todos los usuarios que están activos.
     *
     * @return Lista de usuarios activos.
     */
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findByIsActive(true);
    }

    /**
     * Crea un nuevo usuario validando que no existan conflictos de username o
     * email.
     *
     * @param userDTO Objeto con los datos del nuevo usuario.
     * @return Usuario creado.
     * @throws ResourceAlreadyExistsException si ya existe un usuario con el mismo
     *                                        username o email.
     */
    public User createUser(UserRegistrationDTO userDTO) {
        // Validar que no exista el username
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ResourceAlreadyExistsException("El username ya existe: " + userDTO.getUsername());
        }

        // Validar que no exista el email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("El email ya existe: " + userDTO.getEmail());
        }

        // Crear el nuevo usuario
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Se almacena encriptada
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        user.setRole(userDTO.getRole());

        return userRepository.save(user);
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param id      Identificador del usuario a actualizar.
     * @param userDTO Objeto con los nuevos datos.
     * @return Usuario actualizado.
     * @throws ResourceAlreadyExistsException si el nuevo username o email ya están
     *                                        en uso.
     */
    public User updateUser(Long id, UserRegistrationDTO userDTO) {
        User existingUser = getUserById(id);

        // Validar username único (si cambió)
        if (!existingUser.getUsername().equals(userDTO.getUsername()) &&
                userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ResourceAlreadyExistsException("El username ya existe: " + userDTO.getUsername());
        }

        // Validar email único (si cambió)
        if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("El email ya existe: " + userDTO.getEmail());
        }

        // Actualizar los datos
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());

        // Solo actualizar contraseña si fue enviada
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setRole(userDTO.getRole());

        return userRepository.save(existingUser);
    }

    /**
     * Desactiva un usuario (soft delete).
     *
     * @param id Identificador del usuario.
     * @return Usuario desactivado.
     */
    public User deactivateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(false);
        return userRepository.save(user);
    }

    /**
     * Activa un usuario previamente desactivado.
     *
     * @param id Identificador del usuario.
     * @return Usuario activado.
     */
    public User activateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(true);
        return userRepository.save(user);
    }

    /**
     * Elimina un usuario permanentemente de la base de datos.
     *
     * @param id Identificador del usuario.
     * @throws ResourceNotFoundException si no existe el usuario.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Verifica si existe un usuario con un username dado.
     *
     * @param username Nombre de usuario.
     * @return true si existe, false en caso contrario.
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Verifica si existe un usuario con un email dado.
     *
     * @param email Correo electrónico.
     * @return true si existe, false en caso contrario.
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
