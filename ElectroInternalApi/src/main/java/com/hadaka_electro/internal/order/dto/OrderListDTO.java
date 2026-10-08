package com.hadaka_electro.internal.order.dto;

import com.hadaka_electro.common.entities.order.OrderStatus;
import com.hadaka_electro.common.entities.order.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderListDTO {
    private Integer id;
    private String customerName;
    private double total;
    private LocalDateTime orderTime;
    private String destination;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
}
