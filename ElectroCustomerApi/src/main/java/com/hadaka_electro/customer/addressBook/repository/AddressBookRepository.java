package com.hadaka_electro.customer.addressBook.repository;

import com.hadaka_electro.common.entities.Address;
import com.hadaka_electro.common.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressBookRepository extends JpaRepository<Address, Integer> {

    List<Address> findByCustomerId(Long customerId);

    Optional<Address> findByIdAndCustomerId(Integer id, Long customerId);

    @Modifying
    @Query("UPDATE Address a SET a.defaultForShipping = true WHERE a.id = ?1")
    void setDefaultAddress(Integer id);

    @Modifying
    @Query("UPDATE Address a SET a.defaultForShipping = false WHERE a.id != ?1 AND a.customer.id = ?2")
    void setNonDefaultForOthers(Integer defaultAddressId, Long customerId);

    Optional<Address> findAddressByDefaultForShippingAndCustomer(boolean defaultAddress, Customer customer);
}