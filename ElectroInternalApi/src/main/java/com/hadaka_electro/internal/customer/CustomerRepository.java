package com.hadaka_electro.internal.customer;

import com.hadaka_electro.common.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("UPDATE Customer c SET c.enabled =?2 WHERE c.id = ?1")
    @Modifying
    void updateEnableStatus(Long id, boolean status);

    /*
     * In Spring Data JPA, special parameters like Pageable or Sort are ignored when assigning positional numbers.
     *  Therefore, String keyword is parameter ?1, not ?2
     * */
    @Query("select c from Customer  c where concat(c.firstName,' ',c.lastName,' ',c.email,' '," +
            "c.addressLine1,' ',c.addressLine2,' ',c.city,' ',c.country.name,' ',c.state,' ',c.postalCode) like %?1%")
    Page<Customer> searchCustomersByKeyword(String keyword, Pageable pageable);

    Optional<Customer> findByEmail(String email);
}
