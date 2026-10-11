package com.hadaka_electro.customer.checkout;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.order.Order;
import com.hadaka_electro.common.entities.order.PaymentMethod;
import com.hadaka_electro.common.entities.setting.Setting;
import com.hadaka_electro.customer.customer.CustomerService;
import com.hadaka_electro.customer.order.OrderService;
import com.hadaka_electro.customer.security.CustomerUserDetails;
import com.hadaka_electro.customer.setting.SettingService;
import com.hadaka_electro.customer.shopping_cart.ShoppingCartService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final OrderService orderService;
    private final ShoppingCartService shoppingCartService;
    private final CustomerService customerService;
    private final SettingService settingService;

    public CheckoutController(CheckoutService checkoutService, OrderService orderService, ShoppingCartService shoppingCartService, CustomerService customerService, SettingService settingService) {
        this.checkoutService = checkoutService;
        this.orderService = orderService;
        this.shoppingCartService = shoppingCartService;
        this.customerService = customerService;
        this.settingService = settingService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<Map<String, Object>> initializeCheckoutPage(@AuthenticationPrincipal CustomerUserDetails customerUserDetails) {
        if (customerUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> orderInfo = checkoutService.getOrderInfo(customerUserDetails.getUsername());

        return ResponseEntity.ok(orderInfo);
    }

    @GetMapping("/place-order")
    public ResponseEntity<Object> placeOrder(@AuthenticationPrincipal CustomerUserDetails customerUserDetails) throws MessagingException {
        if (customerUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String customerEmail = customerUserDetails.getUsername();

        Order order = orderService.createOrder(customerEmail, PaymentMethod.COD);
        shoppingCartService.removeAllByCustomer(customerEmail);

        Customer customer = customerService.getCustomerByEmail(customerEmail);
        Map<String, String> mailSettings = settingService.getMailSettings();
        List<Setting> currencySettings = settingService.getCurrencySettings();
        orderService.sendOrderConfirmationEmail(customer, mailSettings, currencySettings, order);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
