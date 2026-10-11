package com.hadaka_electro.customer.checkout;

import com.hadaka_electro.common.entities.Address;
import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.ShippingRate;
import com.hadaka_electro.common.entities.product.Product;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.addressBook.AddressBookService;
import com.hadaka_electro.customer.addressBook.repository.AddressDTO;
import com.hadaka_electro.customer.addressBook.repository.AddressMapper;
import com.hadaka_electro.customer.customer.CustomerService;
import com.hadaka_electro.customer.product.service.ProductService;
import com.hadaka_electro.customer.shippingRate.ShippingRateService;
import com.hadaka_electro.customer.shopping_cart.CartItemDTO;
import com.hadaka_electro.customer.shopping_cart.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutService {

    private final AddressBookService addressBookService;
    private final CustomerService customerService;
    private final ShippingRateService shippingRateService;
    private final ShoppingCartService shoppingCartService;
    private final ProductService productService;
    private final AddressMapper addressMapper;

    public CheckoutService(AddressBookService addressBookService, CustomerService customerService, ShippingRateService shippingRateService, ShoppingCartService shoppingCartService, ProductService productService, AddressMapper addressMapper) {
        this.addressBookService = addressBookService;
        this.customerService = customerService;
        this.shippingRateService = shippingRateService;
        this.shoppingCartService = shoppingCartService;
        this.productService = productService;
        this.addressMapper = addressMapper;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOrderInfo(String email) {
        Customer customer = customerService.getCustomerByEmail(email);

        Address defaultShippingAddress = addressBookService
                .getDefaultAddressByCustomer(customer).orElseThrow(() ->
                        new ObjectNotFoundException("No default shipping address for this customer: " + customer.getEmail()));

        AddressDTO defaultShippingAddressDTO = addressMapper.toDto(defaultShippingAddress);

        ShippingRate defaultAddressShippingRate = shippingRateService
                .getShippingRateByCountryAndState(defaultShippingAddressDTO.getCountryName(),
                        defaultShippingAddressDTO.getState());

        if (defaultAddressShippingRate == null) {
            throw new ObjectNotFoundException("No Shipping rate for this address: " + defaultShippingAddressDTO);
        }

        List<CartItemDTO> cartItems = shoppingCartService.listCartItems(email);

        if (cartItems.isEmpty()) {
            throw new ObjectNotFoundException("No Products in your cart!");
        }

        OrderSummaryInfoDTO orderSummaryInfoDTO = handleCheckoutCartInfo(defaultAddressShippingRate, cartItems);

        return Map.of("defaultShippingAddress", defaultShippingAddressDTO, "orderSummaryInfo", orderSummaryInfoDTO);
    }

    private OrderSummaryInfoDTO handleCheckoutCartInfo(ShippingRate shippingRate, List<CartItemDTO> cartItems) {
        List<CheckoutCartItemDTO> checkoutCartItemDTOS = new ArrayList<>();
        double orderTotalPrice = 0.0, shippingTotalPrice = 0.0, orderTotalCost = 0.0;


        for (CartItemDTO cartItemDTO : cartItems) {
            if (cartItemDTO != null) {
                double productShippingCost = calcProductShippingCost(cartItemDTO.productId(), cartItemDTO.quantity(), shippingRate.getRate());

                orderTotalPrice += (cartItemDTO.unitPrice() * cartItemDTO.quantity());
                orderTotalCost += (cartItemDTO.unitCost() * cartItemDTO.quantity());
                shippingTotalPrice += productShippingCost;

                checkoutCartItemDTOS.add(new CheckoutCartItemDTO(
                        cartItemDTO.productId(),
                        cartItemDTO.productName(),
                        cartItemDTO.unitPrice(),
                        cartItemDTO.unitCost(),
                        cartItemDTO.quantity(),
                        productShippingCost
                ));
            }
        }

        OrderSummaryInfoDTO orderSummaryInfoDTO = new OrderSummaryInfoDTO(
                checkoutCartItemDTOS,
                orderTotalCost,
                orderTotalPrice,
                shippingTotalPrice,
                0.0,
                orderTotalPrice + shippingTotalPrice,
                shippingRate.isCodSupported(),
                shippingRate.getDays(),
                LocalDate.now().plusDays(shippingRate.getDays())

        );

        return orderSummaryInfoDTO;

    }

    // Dimension's unit: M, Weight unit: KG
    private double calcProductShippingCost(Integer productId, int quantity, double rate) {
        Product product = productService.findProductById(productId);
        double shippingCost = 0.0, DIM_weight, billableWeight, DIM_FACTOR = 5000.0;

        DIM_weight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_FACTOR;
        billableWeight = Math.max(product.getWeight(), DIM_weight);
        billableWeight = billableWeight * quantity;

        shippingCost += billableWeight * rate;

        return shippingCost;
    }

}
