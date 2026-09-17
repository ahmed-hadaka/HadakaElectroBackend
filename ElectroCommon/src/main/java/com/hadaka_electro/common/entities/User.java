package com.hadaka_electro.common.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"roles"})
@EqualsAndHashCode(exclude = {"roles", "id"})
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    @Email
    @NotBlank(message = "Error: Email shouldn't be blank!")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    @Size(min = 3, message = "Error: At least three characters required in the name")
    @NotBlank
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @Size(min = 3, message = "Error: At least three characters required in the name")
    @NotBlank
    private String lastName;

    private String photo;

    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @NotEmpty(message = "Error: Every user must be assigned to at least one role!")
    private Set<Role> roles = new HashSet<>();

    public User() {

    }

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String email, String password, String firstName, String lastName, boolean isEnabled) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = isEnabled;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public String getPhotoPath() {
        if (photo == null || id == 0)
            return "/assets/images/default-user.png";

        return "./user_photos/" + this.id + "/" + photo;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

}
