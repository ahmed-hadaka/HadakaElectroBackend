package com.hadaka_electro.internal.addressBook;

import com.hadaka_electro.common.entities.Address;
import com.hadaka_electro.common.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressBookRepository extends JpaRepository<Address, Integer> {

    Optional<Address> findAddressByDefaultForShippingAndCustomer(boolean defaultForShipping, Customer customer);

}
