package com.hadaka_electro.internal.user;

import com.hadaka_electro.common.entities.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;
// convert this to class

@Data
public class UserDTO {

    @NotNull(message = "ID cannot be null")
    private Integer id;

    @Email
    @NotBlank(message = "Error: Email shouldn't be blank!")
    private String email;

    @Size(min = 3, message = "Error: At least three characters required in the name")
    @NotBlank
    private String firstName;

    @Size(min = 3, message = "Error: At least three characters required in the name")
    @NotBlank
    private String lastName;

    private boolean enabled;

    private String password;

    private String photo; // file name

    @NotEmpty(message = "Error: Every user must be assigned to at least one role!")
    private Set<Role> roles;

    public UserDTO() {
    }

    public UserDTO(Integer id, String email) {
        this.id = id;
        this.email = email;
    }

    public UserDTO(Integer id, String email, String firstName, String lastName, boolean enabled, String photo, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
        this.photo = photo;
        this.roles = roles;
    }

}

