package com.hadaka_electro.customer.customer;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.sitting.Country;
import com.hadaka_electro.customer.sitting.repository.CountryRepository;
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

        if (dto.getCountryId() != null) {
            // the getRef.. return proxy object contains the id instead of heavy select command.
            Country country = countryRepository.getReferenceById(dto.getCountryId());
            customer.setCountry(country);
        }

        return customer;
    }
}