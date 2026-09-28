package com.hadaka_electro.customer.customer.repository;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.setting.Country;
import com.hadaka_electro.customer.setting.repository.CountryRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    private final CountryRepository countryRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerMapper(CountryRepository countryRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.countryRepository = countryRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer toEntity(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }

        Customer customer = customerRepository.findById(dto.getId()).orElse(new Customer());

        customer.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty())// in edit mode
            customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setAddressLine1(dto.getAddressLine1());
        customer.setAddressLine2(dto.getAddressLine2());
        customer.setCity(dto.getCity());
        customer.setState(dto.getState());
        customer.setPostalCode(dto.getPostalCode());

        if (dto.getCountryId() != null && countryRepository.existsById(dto.getCountryId())) {
            // the getRef.. return proxy object contains the id instead of heavy select command.
            Country country = countryRepository.getReferenceById(dto.getCountryId());
            customer.setCountry(country);
        }

        return customer;
    }

    public CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setEmail(customer.getEmail());

        // We do not send the encrypted password back to the frontend
        dto.setPassword(null);

        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setAddressLine1(customer.getAddressLine1());
        dto.setAddressLine2(customer.getAddressLine2());
        dto.setCity(customer.getCity());
        dto.setState(customer.getState());
        dto.setPostalCode(customer.getPostalCode());

        if (customer.getCountry() != null) {
            dto.setCountryId(customer.getCountry().getId());
        }

        return dto;
    }
}