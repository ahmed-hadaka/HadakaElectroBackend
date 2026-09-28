package com.hadaka_electro.customer.security;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.customer.repository.CustomerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private CustomerRepository customerRepository;

    public CustomerUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customer = customerRepository.findCustomerByEmail(username);
        if (customer.isPresent()) {
            return new CustomerUserDetails(customer.get());
        }
        throw new ObjectNotFoundException("There is no Customers with this email: " + username);
    }
}
