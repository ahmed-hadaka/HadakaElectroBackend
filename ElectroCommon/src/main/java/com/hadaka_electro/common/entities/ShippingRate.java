package com.hadaka_electro.common.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "shipping_rates")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ShippingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    @NotBlank(message = "Destination country is required")
    private String country;

    @Column(nullable = false, length = 45)
    @NotBlank(message = "Destination state is required")
    private String state;

    @Min(value = 0, message = "Shipping rate must be greater than or equal to 0")
    private double rate;

    @Min(value = 0, message = "Days to deliver must be greater than or equal to 0")
    private int days;

    @Column(name = "cod_supported")
    private boolean codSupported;
}