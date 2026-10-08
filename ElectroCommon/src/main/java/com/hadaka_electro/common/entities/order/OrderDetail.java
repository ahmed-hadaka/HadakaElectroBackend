package com.hadaka_electro.common.entities.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hadaka_electro.common.entities.product.Product;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"order", "product"})
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "product_cost", nullable = false)
    private double productCost;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "shipping_cost", nullable = false)
    private double shippingCost;

    @Column(nullable = false)
    private double subtotal;
}
