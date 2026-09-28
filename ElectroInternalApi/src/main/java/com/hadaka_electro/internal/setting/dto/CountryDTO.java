package com.hadaka_electro.internal.setting.dto;

import jakarta.validation.constraints.NotBlank;

public record CountryDTO(Long id, @NotBlank String name, @NotBlank String code) {
}
