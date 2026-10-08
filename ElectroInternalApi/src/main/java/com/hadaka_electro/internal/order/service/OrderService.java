package com.hadaka_electro.internal.order.service;

import com.hadaka_electro.common.entities.order.Order;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.addressBook.AddressBookService;
import com.hadaka_electro.internal.order.OrderMapper;
import com.hadaka_electro.internal.order.dto.OrderDTO;
import com.hadaka_electro.internal.order.dto.OrderListDTO;
import com.hadaka_electro.internal.order.repository.OrderRepository;
import com.hadaka_electro.internal.shippingRate.ShippingRateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final AddressBookService addressBookService;
    private final ShippingRateService shippingRateService;


    public OrderService(OrderRepository orderRepository, AddressBookService addressBookService, OrderMapper orderMapper, ShippingRateService shippingRateService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.addressBookService = addressBookService;
        this.shippingRateService = shippingRateService;
    }

    public Page<OrderListDTO> listAllOrders(String keyword, Pageable pageable) {
        Page<Order> orders;
        if (keyword != null && !keyword.isEmpty()) {
            orders = orderRepository.searchOrdersByKeyword(keyword, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }
        return orders.map(orderMapper::toListDTO);
    }

    public OrderDTO findById(int id) throws ObjectNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("No order found with ID: " + id));

//        Set<OrderDetail> orderDetails = order.getOrderDetails();
//        double shippingCost = 0.0, productCost = 0.0, subTotal = 0.0,
//                total = 0.0, DIM_weight = 0.0, billableWeight = 0.0, DIM_FACTOR = 5000.0;
//
//        Address defaultAddress = addressBookService.getDefaultAddress(order.getCustomer().getEmail());
//        ShippingRate distenationShippingRate = shippingRateService.getShippingRateByCountryAndState(defaultAddress.getCountry().getName(),defaultAddress.getState());
//
//        for (OrderDetail orderDetail : orderDetails) {
//            Product product = orderDetail.getProduct();
//            DIM_weight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_FACTOR;
//            billableWeight = Math.max(product.getWeight(), DIM_weight);
//            billableWeight = billableWeight * orderDetail.getQuantity();
//
//            shippingCost += billableWeight* distenationShippingRate.getRate();
//        }

        return orderMapper.toDTO(order);
    }

    @Transactional
    public void deleteOrder(int id) throws ObjectNotFoundException {
        if (!orderRepository.existsById(id)) {
            throw new ObjectNotFoundException("No order found with ID: " + id);
        }
        orderRepository.deleteById(id);
    }
}
