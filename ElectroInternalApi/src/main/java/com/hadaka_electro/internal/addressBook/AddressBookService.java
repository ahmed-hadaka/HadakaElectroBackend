package com.hadaka_electro.internal.addressBook;

import com.hadaka_electro.common.entities.Address;
import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AddressBookService {


    private final AddressBookRepository addressRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public AddressBookService(AddressBookRepository addressRepository,
                              CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    public Address getDefaultAddress(String customerEmail) {
        Customer customer = getCustomer(customerEmail);

        return addressRepository
                .findAddressByDefaultForShippingAndCustomer(true, customer)
                .orElseThrow(() -> new ObjectNotFoundException("No Default Address for the customer: " + customerEmail));
    }

    private Customer getCustomer(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("Customer not found"));
    }
}
