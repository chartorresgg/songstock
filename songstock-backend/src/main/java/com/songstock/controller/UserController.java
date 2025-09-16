package com.songstock.controller;

import com.songstock.dto.ApiResponse;
import com.songstock.dto.UserRegistrationDTO;
import com.songstock.entity.User;
import com.songstock.entity.UserRole;
import com.songstock.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Usuarios obtenidos exitosamente", users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario encontrado", user));
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<ApiResponse<User>> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("Usuario encontrado", user));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(@PathVariable UserRole role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success("Usuarios obtenidos exitosamente", users));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getActiveUsers() {
        List<User> users = userService.getActiveUsers();
        return ResponseEntity.ok(ApiResponse.success("Usuarios activos obtenidos exitosamente", users));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody UserRegistrationDTO userDTO) {
        User createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(ApiResponse.success("Usuario creado exitosamente", createdUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegistrationDTO userDTO) {
        User updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(ApiResponse.success("Usuario actualizado exitosamente", updatedUser));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> deactivateUser(@PathVariable Long id) {
        User deactivatedUser = userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario desactivado exitosamente", deactivatedUser));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> activateUser(@PathVariable Long id) {
        User activatedUser = userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario activado exitosamente", activatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Usuario eliminado exitosamente"));
    }
}