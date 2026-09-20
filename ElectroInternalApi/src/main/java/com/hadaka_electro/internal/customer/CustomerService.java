package com.hadaka_electro.internal.customer;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Transactional(readOnly = true)
    public Page<CustomerDTO> listAllCustomers(Pageable pageable, String keyword) {
        Page<Customer> customers;
        if (keyword != null && !keyword.isEmpty()) {
            customers = customerRepository.searchCustomersByKeyword(keyword, pageable);
        } else {
            customers = customerRepository.findAll(pageable);
        }
        return customers.map(customer -> customerMapper.toDTO(customer));
    }

    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long customerId) {
        Customer customer = isCustomerExist(customerId);
        return customerMapper.toDTO(customer);
    }

    @Transactional
    public void updateCustomerDetails(CustomerDTO customerDTO) {
        if (customerRepository.existsById(customerDTO.getId())) {
            Customer updatedCustomer = customerMapper.toEntity(customerDTO);
            customerRepository.save(updatedCustomer);
        } else
            throw new ObjectNotFoundException("No Customers with this id: " + customerDTO.getId());
    }

    @Transactional
    public String updateEnableStatus(Long customerId) {
        Customer customer = isCustomerExist(customerId);

        boolean status = customer.isEnabled();
        customerRepository.updateEnableStatus(customerId, !status);
        status = !status;
        if (status) {
            return "Enabled";
        }
        return "Disabled";
    }

    @Transactional
    public void deleteCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId))
            throw new ObjectNotFoundException("No Customers with this id: " + customerId);

        customerRepository.deleteById(customerId);
    }

    private Customer isCustomerExist(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty())
            throw new ObjectNotFoundException("No Customers with this id: " + customerId);
        return customer.get();
    }
}
