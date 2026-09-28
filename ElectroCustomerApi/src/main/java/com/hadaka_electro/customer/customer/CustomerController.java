package com.hadaka_electro.customer.customer;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.setting.Country;
import com.hadaka_electro.customer.customer.repository.CustomerDTO;
import com.hadaka_electro.customer.customer.repository.ResetPasswordRequest;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/{email}")
    public ResponseEntity<CustomerDTO> getCustomerDetails(@PathVariable("email") String customerEmail) {
        return ResponseEntity.ok(customerService.getCustomerDetails(customerEmail));
    }

    @PostMapping("/update-customer")
    public ResponseEntity<Map<String, String>> updateCustomer(@RequestBody @Valid CustomerDTO customerDTO) throws MessagingException {
        customerService.updateCustomer(customerDTO);

        return ResponseEntity.ok(Map.of("message", "Updated successfully"));
    }


    @PostMapping("/save-customer")
    public ResponseEntity<Map<String, String>> saveCustomer(@RequestBody @Valid CustomerDTO customerDTO) throws MessagingException {
        // aspect here before proceed to check email uniqueness

        Customer savedCustomer = customerService.saveCustomer(customerDTO);
        Map<String, String> mailSettings = customerService.getMailSettings();
        customerService.sendVerificationEmail(savedCustomer, mailSettings);

        return ResponseEntity.ok(Map.of("message", "Customer: " + savedCustomer.getFullName() + " Registered successfully\n" +
                "Please check your email to verify your account"));
    }


    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyCustomer(@RequestParam("code") String verificationCode) {
        String message = customerService.verifyCustomer(verificationCode);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@RequestParam("customer-email") String customerEmail) throws MessagingException {
        Map<String, String> emailAndTokenMap = customerService.setPasswordToken(customerEmail);
        customerService.sendResetPasswordEmail(emailAndTokenMap);
        return ResponseEntity.ok(Map.of("message", "We have sent a reset password link to your email, Please check."));

    }

    @PostMapping("reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        customerService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(Map.of("message", "You have successfully change your password, Please login."));
    }

}
