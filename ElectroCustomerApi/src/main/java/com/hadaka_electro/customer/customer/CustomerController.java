package com.hadaka_electro.customer.customer;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.sitting.Country;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/register")
    public ResponseEntity<List<Country>> initializeRegistrationForm() {
        // fetch all countries & states frm service and return them
        List<Country> countries = customerService.getAllCountries();
        return ResponseEntity.ok(countries);
    }

    @PostMapping("/save-customer")
    public ResponseEntity<String> saveCustomer(@RequestBody @Valid CustomerDTO customerDTO) throws MessagingException {
        // call aspect here before proceed to check email uniqueness
        // in service: assign the generated verification code to it and save it with enable status = false.
        Customer savedCustomer = customerService.saveCustomer(customerDTO);
        // in service: send verification email
        customerService.sendVerificationEmail(savedCustomer);

        return ResponseEntity.ok("Customer: " + savedCustomer.getFullName() + " Registered successfully\n" +
                "Please check your email to verify your account");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCustomer(@RequestParam("code") String verificationCode) {
        String message = customerService.verifyCustomer(verificationCode);
        return ResponseEntity.ok(message);

    }

}
