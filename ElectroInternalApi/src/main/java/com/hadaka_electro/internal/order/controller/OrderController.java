package com.hadaka_electro.internal.order.controller;

import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.order.dto.OrderDTO;
import com.hadaka_electro.internal.order.dto.OrderListDTO;
import com.hadaka_electro.internal.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<Page<OrderListDTO>> listAllOrders(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "orderTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderListDTO> orders = orderService.listAllOrders(keyword, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> viewOrderDetails(@PathVariable int id) throws ObjectNotFoundException {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable int id) throws ObjectNotFoundException {
        orderService.deleteOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body("Order with ID " + id + " has been deleted.");
    }
}
