package com.hadaka_electro.customer.order;

import com.hadaka_electro.common.entities.order.Order;
import com.hadaka_electro.common.entities.order.OrderDetail;
import com.hadaka_electro.common.entities.order.OrderTrack;
import com.hadaka_electro.customer.order.dto.OrderDTO;
import com.hadaka_electro.customer.order.dto.OrderDetailDTO;
import com.hadaka_electro.customer.order.dto.OrderListDTO;
import com.hadaka_electro.customer.order.dto.OrderTrackDTO;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderListDTO toListDTO(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderListDTO(
                order.getId(),
                order.getOrderTime(),
                getProductsNames(order.getOrderDetails()),
                order.getTotal(),
                order.getStatus()
        );
    }

    private Set<String> getProductsNames(Set<OrderDetail> orderDetails) {
        return orderDetails.stream().map(orderDetail -> orderDetail.getProduct().getName()).collect(Collectors.toSet());
    }

    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        assert order.getCustomer() != null;
        return new OrderDTO(
                order.getId(),
                order.getOrderTime(),
                order.getCustomer().getId(),
                order.getCustomer().getFullName(),
                order.getCustomer() != null ? order.getCustomer().getEmail() : null,
                order.getCustomer() != null ? order.getCustomer().getPhoneNumber() : null,
                order.getRecipientName(),
                order.getRecipientPhoneNumber(),
                order.getAddressLine1(),
                order.getAddressLine2(),
                order.getCity(),
                order.getState(),
                order.getPostalCode(),
                order.getCountry(),
                order.getPaymentMethod(),
                order.getStatus(),
                order.getDeliverDays(),
                order.getDeliverDate(),
                order.getShippingCost(),
                order.getSubtotal(),
                order.getTotal(),
                order.getTax(),
                toDetailDTOs(order.getOrderDetails()),
                toTrackDTOs(order.getOrderTracks(), order.getId())
        );
    }


    private Set<OrderDetailDTO> toDetailDTOs(Set<OrderDetail> orderDetails) {
        if (orderDetails == null || orderDetails.isEmpty()) {
            return Set.of();
        }

        return orderDetails.stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toSet());
    }

    private OrderDetailDTO toDetailDTO(OrderDetail detail) {
        return new OrderDetailDTO(
                detail.getProduct() != null ? detail.getProduct().getId() : null,
                detail.getProduct() != null ? detail.getProduct().getName() : null,
                detail.getProduct() != null ? detail.getProduct().getMainImage() : null,
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getSubtotal()
        );
    }

    //  OrderTrack Conversions 

    private Set<OrderTrackDTO> toTrackDTOs(Set<OrderTrack> orderTracks, Integer fallbackOrderId) {
        if (orderTracks == null || orderTracks.isEmpty()) {
            return Set.of();
        }

        return orderTracks.stream()
                .map(track -> toTrackDTO(track, fallbackOrderId))
                .collect(Collectors.toSet());
    }

    private OrderTrackDTO toTrackDTO(OrderTrack track, Integer fallbackOrderId) {
        if (track == null) {
            return null;
        }

        Integer orderId = track.getOrder() != null ? track.getOrder().getId() : fallbackOrderId;
        return new OrderTrackDTO(
                orderId,
                track.getStatus(),
                track.getNotes(),
                track.getUpdatedAt()
        );
    }
}