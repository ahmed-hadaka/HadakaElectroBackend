package com.hadaka_electro.internal.order;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.order.Order;
import com.hadaka_electro.common.entities.order.OrderDetail;
import com.hadaka_electro.common.entities.order.OrderTrack;
import com.hadaka_electro.common.entities.product.Product;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.customer.CustomerRepository;
import com.hadaka_electro.internal.order.dto.OrderDTO;
import com.hadaka_electro.internal.order.dto.OrderDetailDTO;
import com.hadaka_electro.internal.order.dto.OrderListDTO;
import com.hadaka_electro.internal.order.dto.OrderTrackDTO;
import com.hadaka_electro.internal.product.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderMapper(CustomerRepository customerRepository, ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public OrderListDTO toListDTO(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderListDTO(
                order.getId(),
                order.getRecipientName(),
                order.getTotal(),
                order.getOrderTime(),
                formatDestination(order),
                order.getPaymentMethod(),
                order.getStatus()
        );
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
                order.getProductCost(),
                order.getSubtotal(),
                order.getTotal(),
                order.getTax(),
                toDetailDTOs(order.getOrderDetails()),
                toTrackDTOs(order.getOrderTracks(), order.getId())
        );
    }

    public Order toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        Order order = new Order();

        order.setId(dto.id());
        order.setOrderTime(dto.orderTime());

        if (dto.customerId() != null) {
            Customer customer = customerRepository.findById(dto.customerId()).orElseThrow(
                    () -> new ObjectNotFoundException("no customers with this id: " + dto.customerId())
            );
            order.setCustomer(customer);
        }

        order.setRecipientName(dto.recipientName());
        order.setRecipientPhoneNumber(dto.recipientPhoneNumber());
        order.setAddressLine1(dto.addressLine1());
        order.setAddressLine2(dto.addressLine2());
        order.setCity(dto.city());
        order.setState(dto.state());
        order.setPostalCode(dto.postalCode());
        order.setCountry(dto.country());

        order.setPaymentMethod(dto.paymentMethod());

        Optional<OrderTrackDTO> latestTrack = dto.orderTracks().stream()
                .max(Comparator.comparing(OrderTrackDTO::updatedAt, Comparator.nullsFirst(Comparator.naturalOrder())));

        if (latestTrack.isPresent()) {
            order.setStatus(latestTrack.get().orderStatus());
        } else {
            order.setStatus(dto.status());
        }

        if (dto.deliverDays() != null) {
            order.setDeliverDays(dto.deliverDays());
        }

        order.setDeliverDate(dto.expectedDeliverDate());

        order.setShippingCost(dto.shippingCost());
        order.setProductCost(dto.productCost());
        order.setSubtotal(dto.subtotal());
        order.setTotal(dto.total());
        order.setTax(dto.tax());

        if (dto.orderDetails() != null) {
            order.setOrderDetails(toDetailEntities(dto.orderDetails(), order));
        }

        if (dto.orderTracks() != null) {
            order.setOrderTracks(toTrackEntities(dto.orderTracks(), order));
        }

        return order;
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
                detail.getProductCost(),
                detail.getUnitPrice(),
                detail.getShippingCost(),
                detail.getSubtotal()
        );
    }

    private Set<OrderDetail> toDetailEntities(Set<OrderDetailDTO> detailDTOs, Order order) {
        if (detailDTOs == null || detailDTOs.isEmpty()) {
            return new HashSet<>();
        }

        order.getOrderDetails().clear();

        return detailDTOs.stream()
                .map(dto -> toDetailEntity(dto, order))
                .collect(Collectors.toSet());
    }

    private OrderDetail toDetailEntity(OrderDetailDTO dto, Order order) {
        if (dto == null) {
            return null;
        }

        OrderDetail detail = new OrderDetail();
        detail.setOrder(order);

        if (dto.productId() != null) {
            Product product = productRepository.findById(dto.productId()).orElseThrow(
                    () -> new ObjectNotFoundException("no products with this id: " + dto.productId())
            );
            detail.setProduct(product);
        }

        detail.setQuantity(dto.quantity());
        detail.setProductCost(dto.productCost());
        detail.setUnitPrice(dto.unitPrice());
        detail.setShippingCost(dto.shippingCost());
        detail.setSubtotal(dto.subtotal());
        return detail;
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

    private Set<OrderTrack> toTrackEntities(Set<OrderTrackDTO> trackDTOs, Order order) {
        if (trackDTOs == null || trackDTOs.isEmpty()) {
            return new HashSet<>();
        }

        order.getOrderTracks().clear();

        return trackDTOs.stream()
                .map(dto -> toTrackEntity(dto, order))
                .collect(Collectors.toSet());
    }

    private OrderTrack toTrackEntity(OrderTrackDTO dto, Order order) {
        if (dto == null) {
            return null;
        }

        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setStatus(dto.orderStatus());
        track.setUpdatedAt(dto.updatedAt());
        track.setNotes(dto.notes());
        return track;
    }

    //  Helpers
    private String formatDestination(Order order) {
        return String.join(", ",
                List.of(Optional.ofNullable(order.getAddressLine1()).orElse(""),
                        Optional.ofNullable(order.getAddressLine2()).orElse(""),
                        Optional.ofNullable(order.getCity()).orElse(""),
                        Optional.ofNullable(order.getState()).orElse(""),
                        Optional.ofNullable(order.getCountry()).orElse(""),
                        Optional.ofNullable(order.getPostalCode()).orElse(""))
        );
    }
}