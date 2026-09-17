package com.hadaka_electro.internal.sitting.mapper;

import com.hadaka_electro.common.entities.sitting.Country;
import com.hadaka_electro.internal.brand.service.BrandService;
import com.hadaka_electro.internal.sitting.dto.CountryDTO;
import com.hadaka_electro.internal.sitting.repository.CountryRepository;
import org.springframework.stereotype.Component;

@Component
public class CountryMapper {

    private final CountryRepository countryRepository;

    public CountryMapper(CountryRepository countryRepository){
        this.countryRepository = countryRepository;
    }

    public CountryDTO toDTO(Country entity) {
        if (entity == null) {
            return null;
        }
        return new CountryDTO(
                entity.getId(),
                entity.getName(),
                entity.getCode()
        );
    }

    public Country toEntity(CountryDTO dto) {
        if (dto == null) {
            return null;
        }

        Country country = countryRepository.findById(dto.id()).orElse(new Country());
        country.setName(dto.name());
        country.setCode(dto.code());
        return country;
    }
}