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
import com.hadaka_electro.customer.customer.repository.CustomerRepository;
import com.hadaka_electro.customer.order.dto.OrderDTO;
import com.hadaka_electro.customer.order.dto.OrderListDTO;
import com.hadaka_electro.customer.order.dto.OrderReturnReqDTO;
import com.hadaka_electro.customer.product.repository.ProductRepository;
import com.hadaka_electro.customer.setting.SettingService;
import com.hadaka_electro.customer.setting.repository.CountryRepository;
import com.hadaka_electro.customer.setting.repository.SettingsRepository;
import com.hadaka_electro.customer.shopping_cart.ShoppingCartService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;
    @Value("${app.frontend.baseUrl}")
    private String frontendBaseUrl;

    private final CustomerService customerService;
    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SettingsRepository settingsRepository;
    private final ShoppingCartService shoppingCartService;

    public OrderService(CustomerService customerService, CheckoutService checkoutService, OrderRepository orderRepository, ProductRepository productRepository, SettingsRepository settingsRepository, CountryRepository countryRepository, SettingService settingService, CustomerRepository customerRepository, OrderMapper orderMapper, ShoppingCartService shoppingCartService) {
        this.customerService = customerService;
        this.checkoutService = checkoutService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.settingsRepository = settingsRepository;
        this.countryRepository = countryRepository;
        this.settingService = settingService;
        this.customerRepository = customerRepository;
        this.orderMapper = orderMapper;
        this.shoppingCartService = shoppingCartService;
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
        orderTrack.setUpdatedAt(LocalDateTime.now());
        order.addOrderTrack(orderTrack);


        Order savedOrder = orderRepository.save(order);
        shoppingCartService.removeAllByCustomer(customerEmail);
        return savedOrder;
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
        String orderLink = frontendBaseUrl + "/orders";
        content = content.replace("[[orderLink]]", orderLink);

        mimeMessageHelper.setText(content);

        mailSender.send(mimeMessage);
    }

    @Transactional(readOnly = true)
    public Page<OrderListDTO> getAllCustomerOrders(String email, Pageable pageable, String keyword) {
        Customer customer = customerService.getCustomerByEmail(email);

        Page<Order> ordersPage;
        if (keyword != null && !keyword.isEmpty()) {
            ordersPage = orderRepository.findOrdersByCustomer(customer.getId(), keyword, pageable);
        } else {
            ordersPage = orderRepository.findOrdersByCustomer(customer, pageable);
        }
        return ordersPage.map(order -> orderMapper.toListDTO(order));
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderByIdAndCustomer(int id, String email) {
        Customer customer = customerService.getCustomerByEmail(email);
        Order order = orderRepository.findOrderByIdAndCustomer(id, customer)
                .orElseThrow(() -> new ObjectNotFoundException("No Orders with this id: " + id + " for you!"));

        return orderMapper.toDTO(order);
    }

    @Transactional
    public void requestOrderReturn(OrderReturnReqDTO orderReturnReq, String email) {
        Customer customer = customerService.getCustomerByEmail(email);
        Order order = orderRepository.findOrderByIdAndCustomer(orderReturnReq.orderId(), customer)
                .orElseThrow(() -> new ObjectNotFoundException("No such order with this id: " + orderReturnReq.orderId()));

        if (order.hasStatus(OrderStatus.RETURNED) || order.hasStatus(OrderStatus.RETURNED_REQUESTED))
            return;

        OrderTrack orderTrack = new OrderTrack();
        orderTrack.setOrder(order);
        orderTrack.setStatus(OrderStatus.RETURNED_REQUESTED);
        orderTrack.setUpdatedAt(LocalDateTime.now());
        String note = orderReturnReq.reason();
        if (orderReturnReq.note() != null && !orderReturnReq.note().isEmpty()) {
            note = note + ", " + orderReturnReq.note();
        }
        orderTrack.setNotes(note);
        order.addOrderTrack(orderTrack);
        order.setStatus(OrderStatus.RETURNED_REQUESTED);

        orderRepository.save(order);
    }
}
