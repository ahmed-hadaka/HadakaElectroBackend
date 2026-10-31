package com.hadaka_electro.customer.order;

import com.hadaka_electro.customer.order.dto.OrderDTO;
import com.hadaka_electro.customer.order.dto.OrderListDTO;
import com.hadaka_electro.customer.order.dto.OrderReturnReqDTO;
import com.hadaka_electro.customer.security.CustomerUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<Page<OrderListDTO>> listAllOrders(@AuthenticationPrincipal CustomerUserDetails customerUserDetails,
                                                            @PageableDefault(sort = "orderTime", direction = Sort.Direction.DESC) Pageable pageable,
                                                            @RequestParam(required = false) String keyword) {
        if (customerUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Page<OrderListDTO> orders = orderService.getAllCustomerOrders(customerUserDetails.getUsername(), pageable, keyword);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable int id,
                                                 @AuthenticationPrincipal CustomerUserDetails customerUserDetails) {
        if (customerUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(orderService.getOrderByIdAndCustomer(id, customerUserDetails.getUsername()));
    }

    @PostMapping("/return")
    public ResponseEntity<Object> requestOrderReturn(@RequestBody OrderReturnReqDTO orderReturnReq,
                                                     @AuthenticationPrincipal CustomerUserDetails customerUserDetails) {

        if (customerUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        orderService.requestOrderReturn(orderReturnReq, customerUserDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
