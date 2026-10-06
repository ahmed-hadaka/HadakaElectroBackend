package com.hadaka_electro.customer.addressBook.repository;

import com.hadaka_electro.common.entities.Address;
import com.hadaka_electro.common.entities.setting.Country;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.setting.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    private final CountryRepository countryRepository;

    @Autowired
    public AddressMapper(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public AddressDTO toDto(Address address) {
        if (address == null) {
            return null;
        }

        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setFirstName(address.getFirstName());
        dto.setLastName(address.getLastName());
        dto.setPhoneNumber(address.getPhoneNumber());
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPostalCode(address.getPostalCode());
        dto.setDefaultForShipping(address.isDefaultForShipping());

        if (address.getCountry() != null) {
            dto.setCountryId(address.getCountry().getId());
            dto.setCountryName(address.getCountry().getName());
        }

        return dto;
    }

    public Address toEntity(AddressDTO dto, Address existingEntity) throws ObjectNotFoundException {
        Address entity = (existingEntity != null) ? existingEntity : new Address();

        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setAddressLine1(dto.getAddressLine1());
        entity.setAddressLine2(dto.getAddressLine2());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setPostalCode(dto.getPostalCode());
        entity.setDefaultForShipping(dto.isDefaultForShipping());

        if (dto.getCountryId() != null && dto.getCountryId() > 0) {
            Country country = countryRepository.findById(dto.getCountryId())
                    .orElseThrow(() -> new ObjectNotFoundException("Country with ID " + dto.getCountryId() + " not found."));
            entity.setCountry(country);
        } else {
            throw new ObjectNotFoundException("A valid Country ID must be provided.");
        }

        return entity;
    }
}