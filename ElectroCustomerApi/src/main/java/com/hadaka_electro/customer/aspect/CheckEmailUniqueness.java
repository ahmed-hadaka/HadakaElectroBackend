package com.hadaka_electro.customer.aspect;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.customer.customer.repository.CustomerDTO;
import com.hadaka_electro.customer.customer.repository.CustomerRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@Aspect
public class CheckEmailUniqueness {

    private final CustomerRepository customerRepository;

    public CheckEmailUniqueness(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Before("execution(* com.hadaka_electro.customer.customer.CustomerController.saveCustomer(..))")
    public void checkCustomerEmailUniqueness(JoinPoint joinPoint) {
        CustomerDTO customerDTO = (CustomerDTO) joinPoint.getArgs()[0];
        Optional<Customer> customer = customerRepository.findCustomerByEmail(customerDTO.getEmail());
        if (customer.isPresent()) {
            if (Objects.equals(customer.get().getId(), customerDTO.getId())) {
                return;
            }
            throw new DuplicatedObjectException("The email " + customerDTO.getEmail() + " is already taken!");
        }
    }
}
