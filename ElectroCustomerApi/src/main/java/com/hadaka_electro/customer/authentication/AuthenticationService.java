package com.hadaka_electro.customer.authentication;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.customer.customer.repository.CustomerRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    public AuthenticationService(PasswordEncoder passwordEncoder, CustomerRepository customerRepository) {
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer fetchOrCreateCustomer(String email, String firstName, String lastName) {

        Optional<Customer> customerOptional = customerRepository.findCustomerByEmail(email);
        if (customerOptional.isPresent())
            return customerOptional.get();

        Customer customer = new Customer();

        customer.setEmail(email);
        customer.setPassword(passwordEncoder.encode(RandomString.make(16)));
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPhoneNumber("N/A");
        customer.setAddressLine1("N/A");
        customer.setPostalCode("N/A");
        customer.setCity("N/A");
        customer.setState("N/A");
        customer.setVerificationCode("");// verified from google
        customer.setEnabled(true);

        return customerRepository.save(customer);
    }
}
