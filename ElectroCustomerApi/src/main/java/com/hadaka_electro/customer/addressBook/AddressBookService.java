package com.hadaka_electro.customer.addressBook;

import com.hadaka_electro.common.entities.Address;
import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.addressBook.repository.AddressBookRepository;
import com.hadaka_electro.customer.addressBook.repository.AddressDTO;
import com.hadaka_electro.customer.addressBook.repository.AddressMapper;
import com.hadaka_electro.customer.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AddressBookService {

    private final AddressBookRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressBookService(AddressBookRepository addressRepository,
                              CustomerRepository customerRepository,
                              AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
        this.addressMapper = addressMapper;
    }

    public List<AddressDTO> listAddressBook(String customerEmail) {
        Customer customer = getCustomer(customerEmail);
        return addressRepository.findByCustomerId(customer.getId())
                .stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    public AddressDTO getAddress(Integer id, String customerEmail) throws ObjectNotFoundException {
        Customer customer = getCustomer(customerEmail);
        Address address = addressRepository.findByIdAndCustomerId(id, customer.getId())
                .orElseThrow(() -> new ObjectNotFoundException("Address not found or you do not have permission to view it."));
        return addressMapper.toDto(address);
    }

    @Transactional
    public Integer save(AddressDTO addressDTO, String customerEmail) throws ObjectNotFoundException {
        Customer customer = getCustomer(customerEmail);

        Address existingAddress = null;
        if (addressDTO.getId() != null && addressDTO.getId() > 0) {
            existingAddress = addressRepository.findByIdAndCustomerId(addressDTO.getId(), customer.getId())
                    .orElseThrow(() -> new ObjectNotFoundException("Address not found or you do not have permission to modify it."));
        }

        Address address = addressMapper.toEntity(addressDTO, existingAddress);
        address.setCustomer(customer);

        Address savedAddress = addressRepository.save(address);
        return savedAddress.getId();
    }

    @Transactional
    public void delete(Integer id, String customerEmail) throws ObjectNotFoundException {
        Customer customer = getCustomer(customerEmail);
        Address address = addressRepository.findByIdAndCustomerId(id, customer.getId())
                .orElseThrow(() -> new ObjectNotFoundException("Address Id: " + id + " not found."));
        addressRepository.delete(address);
    }

    @Transactional
    public void setDefaultAddress(Integer defaultAddressId, String customerEmail) {
        Customer customer = getCustomer(customerEmail);
        addressRepository.setDefaultAddress(defaultAddressId);
        addressRepository.setNonDefaultForOthers(defaultAddressId, customer.getId());
    }

    private Customer getCustomer(String email) {
        return customerRepository.findCustomerByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("Customer not found"));
    }

    @Transactional(readOnly = true)
    public Optional<Address> getDefaultAddressByCustomer(Customer customer) {
        return addressRepository.findAddressByDefaultForShippingAndCustomer(true, customer);
    }
}