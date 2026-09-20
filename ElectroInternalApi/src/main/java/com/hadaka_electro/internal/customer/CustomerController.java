package com.hadaka_electro.internal.customer;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<Page<CustomerDTO>> listCustomers(@PageableDefault(sort = "firstName") Pageable pageable,
                                                           @RequestParam(required = false) String keyword) {
        Page<CustomerDTO> customers = customerService.listAllCustomers(pageable, keyword);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> viewCustomer(@PathVariable("id") Long customerId) {
        CustomerDTO customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateCustomer(@RequestBody @Valid CustomerDTO customerDTO) {
        customerService.updateCustomerDetails(customerDTO);
        return ResponseEntity.ok("Customer Id: " + customerDTO.getId() + " updated successfully");
    }

    @PostMapping("/toggle-enable-status/{id}")
    public ResponseEntity<String> enableCustomer(@PathVariable("id") Long customerId) {
        String status = customerService.updateEnableStatus(customerId);
        return ResponseEntity.ok("Customer id: " + customerId + " has been " + status + " successfully.");

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable("id") Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok("Customer id: " + customerId + " has been deleted successfully.");
    }
}
