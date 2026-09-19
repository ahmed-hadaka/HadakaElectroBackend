package com.hadaka_electro.customer.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO {

    @NotNull
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Size(max = 45, message = "Email cannot exceed 45 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 45, message = "First name must be between 2 and 45 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 45, message = "Last name must be between 2 and 45 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Size(max = 15, message = "Phone number cannot exceed 15 characters")
    private String phoneNumber;

    @NotBlank(message = "Address Line 1 is required")
    @Size(max = 64, message = "Address Line 1 cannot exceed 64 characters")
    private String addressLine1;

    @Size(max = 64, message = "Address Line 2 cannot exceed 64 characters")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(max = 45, message = "City cannot exceed 45 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 45, message = "State cannot exceed 45 characters")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Size(max = 10, message = "Postal code cannot exceed 10 characters")
    private String postalCode;

    @NotNull(message = "Country is required")
    private Long countryId;

}
