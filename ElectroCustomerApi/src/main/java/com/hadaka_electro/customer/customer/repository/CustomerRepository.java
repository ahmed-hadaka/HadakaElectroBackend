package com.hadaka_electro.customer.customer.repository;

import com.hadaka_electro.common.entities.Customer;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);

    Optional<Customer> findCustomerByEmail(String email);

    Optional<Customer> findCustomerByVerificationCode(String verificationCode);

    Optional<Customer> findCustomerByResetPasswordToken(@NotBlank String s);
}
