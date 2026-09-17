package com.hadaka_electro.internal.sitting.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StateDTO(Long id, @NotBlank String name, @NotNull @Min(1) Long countryId) {
}
