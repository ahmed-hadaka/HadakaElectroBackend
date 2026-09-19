package com.hadaka_electro.customer.aspect;

import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.customer.customer.CustomerDTO;
import com.hadaka_electro.customer.customer.CustomerRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class CheckEmailUniqueness {

    private final CustomerRepository customerRepository;

    public CheckEmailUniqueness(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Before("execution(* com.hadaka_electro.customer.customer.CustomerController.saveCustomer(..))")
    public void checkCustomerEmailUniqueness(JoinPoint joinPoint) {
        CustomerDTO customer = (CustomerDTO) joinPoint.getArgs()[0];
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new DuplicatedObjectException("The email " + customer.getEmail() + " is already taken!");
        }
    }
}
