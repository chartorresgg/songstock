// ================= ARCHIVO: UserRegistrationDTO.java (ACTUALIZACIÓN) =================
package com.songstock.dto;

import com.songstock.entity.UserRole;
import jakarta.validation.constraints.*;

public class UserRegistrationDTO {

    @NotBlank(message = "Username es requerido")
    @Size(min = 3, max = 50, message = "Username debe tener entre 3 y 50 caracteres")
    private String username;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;

    @NotBlank(message = "Password es requerido")
    @Size(min = 6, message = "Password debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "Nombre es requerido")
    private String firstName;

    @NotBlank(message = "Apellido es requerido")
    private String lastName;

    private String phone;

    // Cambiar a String para manejar tanto "CUSTOMER" como UserRole.CUSTOMER
    private UserRole role;

    // Constructors
    public UserRegistrationDTO() {
    }

    public UserRegistrationDTO(String username, String email, String password, String firstName, String lastName,
            UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    // Método de conveniencia para setear role desde String
    public void setRoleFromString(String roleString) {
        if (roleString != null) {
            try {
                this.role = UserRole.valueOf(roleString.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Si el string no es válido, asignar CUSTOMER por defecto
                this.role = UserRole.CUSTOMER;
            }
        }
    }
}