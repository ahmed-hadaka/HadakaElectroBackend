package com.hadaka_electro.common.entities.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hadaka_electro.common.entities.Customer;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"customer", "orderDetails"})
@EqualsAndHashCode(exclude = {"customer", "orderDetails"})
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    @Column(name = "recipient_name", nullable = false, length = 45)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false, length = 15)
    private String recipientPhoneNumber;

    @Column(name = "address_line_1", nullable = false, length = 64)
    private String addressLine1;

    @Column(name = "address_line_2", length = 64)
    private String addressLine2;

    @Column(nullable = false, length = 45)
    private String city;

    @Column(nullable = false, length = 45)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(nullable = false, length = 45)
    private String country;

    @Column(name = "payment_method", nullable = false, length = 45)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 45)
    private OrderStatus status;

    @Column(name = "shipping_cost", nullable = false)
    private double shippingCost;

    @Column(name = "product_cost", nullable = false)
    private double productCost;

    @Column(nullable = false)
    private double subtotal;

    @Column(nullable = false)
    private double tax;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false, name = "deliver_days")
    private int deliverDays;

    @Column(name = "deliver_date")
    private LocalDate deliverDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderTrack> orderTracks = new HashSet<>();

    public void addOrderTrack(OrderTrack orderTrack) {
        this.orderTracks.add(orderTrack);
    }
}
