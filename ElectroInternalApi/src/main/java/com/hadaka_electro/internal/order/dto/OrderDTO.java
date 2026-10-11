package com.hadaka_electro.internal.order.dto;

import com.hadaka_electro.common.entities.order.OrderStatus;
import com.hadaka_electro.common.entities.order.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {
    private Integer id;
    private LocalDateTime orderTime;

    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNumber;

    private String recipientName;
    private String recipientPhoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private PaymentMethod paymentMethod;
    private OrderStatus status;


    private Integer deliverDays;
    private LocalDate expectedDeliverDate;
    private double shippingCost;
    private double productCost;
    private double subtotal;
    private double total;

    private List<OrderDetailDTO> orderDetails;
}
