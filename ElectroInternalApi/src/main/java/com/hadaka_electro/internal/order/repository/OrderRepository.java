package com.hadaka_electro.internal.order.repository;

import com.hadaka_electro.common.entities.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("select o from Order o where lower(concat(" +
            "o.customer.firstName, ' ', o.customer.lastName, ' ', " +
            "o.recipientName, ' ', o.addressLine1, ' ', coalesce(o.addressLine2, ''), ' ', " +
            "o.city, ' ', o.state, ' ', o.postalCode, ' ', o.country, ' ', " +
            "cast(o.paymentMethod as string), ' ', cast(o.status as string))) like lower(concat('%', ?1, '%'))")
    Page<Order> searchOrdersByKeyword(String keyword, Pageable pageable);
}
