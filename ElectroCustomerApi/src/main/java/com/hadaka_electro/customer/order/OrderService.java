package com.hadaka_electro.customer.order;

import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.order.*;
import com.hadaka_electro.common.entities.product.Product;
import com.hadaka_electro.common.entities.setting.Country;
import com.hadaka_electro.common.entities.setting.Setting;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.MailUtility;
import com.hadaka_electro.customer.addressBook.repository.AddressDTO;
import com.hadaka_electro.customer.checkout.CheckoutCartItemDTO;
import com.hadaka_electro.customer.checkout.CheckoutService;
import com.hadaka_electro.customer.checkout.OrderSummaryInfoDTO;
import com.hadaka_electro.customer.customer.CustomerService;
import com.hadaka_electro.customer.product.repository.ProductRepository;
import com.hadaka_electro.customer.setting.SettingService;
import com.hadaka_electro.customer.setting.repository.CountryRepository;
import com.hadaka_electro.customer.setting.repository.SettingsRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrderService {

    private final CountryRepository countryRepository;
    private final SettingService settingService;
    @Value("${app.frontend.baseUrl}")
    private String frontendBaseUrl;

    private final CustomerService customerService;
    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SettingsRepository settingsRepository;

    public OrderService(CustomerService customerService, CheckoutService checkoutService, OrderRepository orderRepository, ProductRepository productRepository, SettingsRepository settingsRepository, CountryRepository countryRepository, SettingService settingService) {
        this.customerService = customerService;
        this.checkoutService = checkoutService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.settingsRepository = settingsRepository;
        this.countryRepository = countryRepository;
        this.settingService = settingService;
    }

    @Transactional
    public Order createOrder(String customerEmail, PaymentMethod paymentMethod) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        Map<String, Object> orderInfo = checkoutService.getOrderInfo(customerEmail);
        AddressDTO defaultShippingAddress = (AddressDTO) orderInfo.get("defaultShippingAddress");
        OrderSummaryInfoDTO orderSummaryInfoDTO = (OrderSummaryInfoDTO) orderInfo.get("orderSummaryInfo");

        // fill basic order info
        Order order = new Order();

        order.setOrderTime(LocalDateTime.now());
        order.setRecipientName(defaultShippingAddress.getFirstName() + " " + defaultShippingAddress.getLastName());
        order.setRecipientPhoneNumber(defaultShippingAddress.getPhoneNumber());
        order.setAddressLine1(defaultShippingAddress.getAddressLine1());
        order.setAddressLine2(defaultShippingAddress.getAddressLine2());
        order.setCity(defaultShippingAddress.getCity());
        order.setState(defaultShippingAddress.getState());
        order.setPostalCode(defaultShippingAddress.getPostalCode());
        Country country = countryRepository.findCountryById(defaultShippingAddress.getCountryId())
                .orElseThrow(() -> new ObjectNotFoundException("No Countries found for shipping address"));
        order.setCountry(country.getName());
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.NEW);
        order.setShippingCost(orderSummaryInfoDTO.shippingTotalPrice());
        order.setProductCost(orderSummaryInfoDTO.orderTotalCost());
        order.setSubtotal(orderSummaryInfoDTO.orderTotalPrice());
        order.setTax(orderSummaryInfoDTO.tax());
        order.setTotal(orderSummaryInfoDTO.paymentTotal());
        order.setDeliverDays(orderSummaryInfoDTO.deliverDays());
        order.setDeliverDate(orderSummaryInfoDTO.expectedDeliverDate());
        order.setCustomer(customer);

        // fill order details
        Set<OrderDetail> orderDetails = new HashSet<>();

        for (CheckoutCartItemDTO cartItemDTO : orderSummaryInfoDTO.checkoutCartItemDTOS()) {
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setOrder(order);
            Product product = productRepository.findProductById(cartItemDTO.productId())
                    .orElseThrow(() -> new ObjectNotFoundException("No product with this name: " + cartItemDTO.productName()));
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItemDTO.quantity());
            orderDetail.setProductCost(cartItemDTO.unitCost());
            orderDetail.setUnitPrice(cartItemDTO.unitPrice());
            orderDetail.setShippingCost(cartItemDTO.shippingCost());
            orderDetail.setSubtotal(cartItemDTO.unitPrice() * cartItemDTO.quantity());

            orderDetails.add(orderDetail);
        }
        order.setOrderDetails(orderDetails);

        // fill order track
        OrderTrack orderTrack = new OrderTrack();

        orderTrack.setOrder(order);
        orderTrack.setStatus(OrderStatus.NEW);
        orderTrack.setNotes("Order was placed by customer");
        order.addOrderTrack(orderTrack);


        return orderRepository.save(order);
    }


    @Async
    public void sendOrderConfirmationEmail(Customer customer, Map<String, String> mailSettings, List<Setting> currencySettings, Order order) throws MessagingException {

        // set mailServer(host), port, username, password, turn on Auth and TLS.
        JavaMailSenderImpl mailSender = MailUtility.prepareMailSender(mailSettings);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);


        mimeMessageHelper.setFrom(mailSettings.get("MAIL_FROM"));
        mimeMessageHelper.setTo(customer.getEmail());

        String subject = mailSettings.get("ORDER_CONFIRM_SUBJECT");
        subject = subject.replace("[[orderId]]", order.getId().toString());
        mimeMessageHelper.setSubject(subject);

        String content = mailSettings.get("ORDER_CONFIRM_CONTENT");
        content = content.replace("[[name]]", customer.getFullName());
        content = content.replace("[[orderId]]", order.getId().toString());
        content = content.replace("[[orderTime]]", order.getOrderTime().toString());
        content = content.replace("[[shippingAddress]]", order.getCountry() + " " + order.getState() + " " + order.getAddressLine1());

        String currencySymbolPosition = currencySettings.stream().filter(setting -> setting.getKey().equals("CURRENCY_SYMBOL_POSITION")).findFirst().get().getValue();
        String currencySymbol = currencySettings.stream().filter(setting -> setting.getKey().equals("CURRENCY_SYMBOL")).findFirst().get().getValue();

        if (currencySymbolPosition.equals("BEFORE_PRICE")) {
            content = content.replace("[[total]]", currencySymbol + " " + String.valueOf(order.getTotal()));
        } else {
            content = content.replace("[[total]]", String.valueOf(order.getTotal()) + " " + currencySymbol);
        }

        content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());
        String orderLink = frontendBaseUrl + "/track-order";
        content = content.replace("[[orderLink]]", orderLink);

        mimeMessageHelper.setText(content);

        mailSender.send(mimeMessage);
    }
}
