package com.hadaka_electro.internal.order;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.order.Order;
import com.hadaka_electro.common.entities.order.OrderDetail;
import com.hadaka_electro.internal.order.dto.OrderDTO;
import com.hadaka_electro.internal.order.dto.OrderDetailDTO;
import com.hadaka_electro.internal.order.dto.OrderListDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class OrderMapper {


    public OrderMapper() {
    }

    public OrderListDTO toListDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderListDTO dto = new OrderListDTO();
        dto.setId(order.getId());
        dto.setCustomerName(resolveCustomerName(order.getCustomer()));
        dto.setTotal(order.getTotal());
        dto.setOrderTime(order.getOrderTime());
        dto.setDestination(formatDestination(order));
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setStatus(order.getStatus());
        return dto;
    }

    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderTime(order.getOrderTime());
        dto.setCustomerId(order.getCustomer() != null ? order.getCustomer().getId() : null);
        dto.setCustomerName(resolveCustomerName(order.getCustomer()));
        dto.setCustomerEmail(order.getCustomer() != null ? order.getCustomer().getEmail() : null);
        dto.setCustomerPhoneNumber(order.getCustomer() != null ? order.getCustomer().getPhoneNumber() : null);

        dto.setRecipientName(order.getRecipientName());
        dto.setRecipientPhoneNumber(order.getRecipientPhoneNumber());
        dto.setAddressLine1(order.getAddressLine1());
        dto.setAddressLine2(order.getAddressLine2());
        dto.setCity(order.getCity());
        dto.setState(order.getState());
        dto.setPostalCode(order.getPostalCode());
        dto.setCountry(order.getCountry());

        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setStatus(order.getStatus());
        dto.setDeliverDays(order.getDeliverDays());
        dto.setExpectedDeliverDate(order.getDeliverDate());
        dto.setShippingCost(order.getShippingCost());// sum of all product's Shipping cost
        dto.setProductCost(order.getProductCost());// sum of all product's cost
        dto.setSubtotal(order.getSubtotal());// sum of (unit price * quantity)
        dto.setTotal(order.getTotal());// subtotal + tax + shipping cost
        dto.setOrderDetails(toDetailDTOs(order.getOrderDetails()));
        return dto;
    }

    private List<OrderDetailDTO> toDetailDTOs(java.util.Set<OrderDetail> orderDetails) {
        if (orderDetails == null || orderDetails.isEmpty()) {
            return List.of();
        }

        return orderDetails.stream()
                .sorted(Comparator.comparing(OrderDetail::getId, Comparator.nullsLast(Integer::compareTo)))
                .map(this::toDetailDTO)
                .toList();
    }

    private OrderDetailDTO toDetailDTO(OrderDetail detail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setProductId(detail.getProduct() != null ? detail.getProduct().getId() : null);
        dto.setProductName(detail.getProduct() != null ? detail.getProduct().getName() : null);
        dto.setMainImage(detail.getProduct() != null ? detail.getProduct().getMainImage() : null);
        dto.setQuantity(detail.getQuantity());
        dto.setProductCost(detail.getProductCost());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setShippingCost(detail.getShippingCost());
        dto.setSubtotal(detail.getSubtotal());
        return dto;
    }

    private String formatDestination(Order order) {
        return String.join(", ",
                List.of(Optional.ofNullable(order.getCountry()).orElse(""),
                        Optional.ofNullable(order.getState()).orElse(""),
                        Optional.ofNullable(order.getCity()).orElse(""))
        );
    }

    private String resolveCustomerName(Customer customer) {
        if (customer == null) {
            return null;
        }
        return customer.getFirstName() + " " + customer.getLastName();
    }
}
