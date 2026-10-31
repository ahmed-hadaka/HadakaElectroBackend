package com.hadaka_electro.customer.order;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(nativeQuery = true, value = "select * " +
            "from orders o " +
            "inner join order_details od on o.id = od.order_id " +
            "inner join products p on p.id = od.product_id " +
            "where o.customer_id = :customerId and " +
            "(LOWER(p.name) like lower(CONCAT('%',:keyword,'%')) " +
            "or lower(o.status) like lower(concat('%',:keyword,'%'))) ")
    Page<Order> findOrdersByCustomer(Long customerId, String keyword, Pageable pageable);

    Page<Order> findOrdersByCustomer(Customer customer, Pageable pageable);

    Optional<Order> findOrderByIdAndCustomer(Integer id, Customer customer);
}
