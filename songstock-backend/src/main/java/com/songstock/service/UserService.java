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

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findByIsActive(true);
    }

    public User createUser(UserRegistrationDTO userDTO) {
        // Validar que no exista el username
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ResourceAlreadyExistsException("El username ya existe: " + userDTO.getUsername());
        }

        // Validar que no exista el email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("El email ya existe: " + userDTO.getEmail());
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        user.setRole(userDTO.getRole());

        return userRepository.save(user);
    }

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

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setRole(userDTO.getRole());

        return userRepository.save(existingUser);
    }

    public User deactivateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(false);
        return userRepository.save(user);
    }

    public User activateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(true);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}