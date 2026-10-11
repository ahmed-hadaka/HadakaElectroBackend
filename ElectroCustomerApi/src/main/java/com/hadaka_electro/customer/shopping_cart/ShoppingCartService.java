package com.hadaka_electro.customer.shopping_cart;

import com.hadaka_electro.common.entities.Address;
import com.hadaka_electro.common.entities.CartItem;
import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.ShippingRate;
import com.hadaka_electro.common.entities.product.Product;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.addressBook.AddressBookService;
import com.hadaka_electro.customer.customer.CustomerService;
import com.hadaka_electro.customer.product.repository.ProductRepository;
import com.hadaka_electro.customer.shippingRate.ShippingRateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ShoppingCartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;
    private final AddressBookService addressBookService;
    private final ShippingRateService shippingRateService;
    private final CustomerService customerService;
//    private ShippingRateService shippingRateService;

    public ShoppingCartService(CartItemRepository cartRepo, ProductRepository productRepo, AddressBookService addressBookService, ShippingRateService shippingRateService, CustomerService customerService) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.addressBookService = addressBookService;
        this.shippingRateService = shippingRateService;
        this.customerService = customerService;
    }

    @Transactional(readOnly = true)
    public List<CartItemDTO> listCartItems(String customerEmail) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        List<CartItem> cartItems = cartRepo.findByCustomer(customer);
        if (cartItems.isEmpty())
            return List.of();
        return cartItems.stream().map(item -> {
            if (item.getProduct() == null)
                return null;
            return new CartItemDTO(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getProduct().getMainImage(),
                    item.getProduct().getPriceAfterDiscount(),
                    item.getProduct().getCost(),
                    item.getQuantity()
            );

        }).collect(Collectors.toList());
    }

    @Transactional
    public void addProduct(Integer productId, Integer quantity, String customerEmail) {

        if (quantity < 1 || quantity > 5) {
            throw new IllegalArgumentException("Quantity must be between 1 and 5.");
        }

        Customer customer = customerService.getCustomerByEmail(customerEmail);
        Product product = getProductById(productId);

        Optional<CartItem> existingItem = cartRepo.findByCustomerAndProduct(customer, product);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            if (newQuantity > 5) {
                throw new IllegalArgumentException("Maximum quantity is 5. You already have " + cartItem.getQuantity() + " in your cart.");
            }
            cartItem.setQuantity(newQuantity);
            cartRepo.save(cartItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCustomer(customer);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartRepo.save(newItem);
        }
    }

    @Transactional
    public void updateQuantity(Integer productId, int quantity, String customerEmail) {
        if (quantity < 1 || quantity > 5) {
            throw new IllegalArgumentException("Quantity must be between 1 and 5.");
        }

        Customer customer = customerService.getCustomerByEmail(customerEmail);
        // if no cart items with these id's, nothing happen!
        cartRepo.updateQuantity(quantity, customer.getId(), productId);

    }

    @Transactional
    public void removeProduct(Integer productId, String customerEmail) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        cartRepo.deleteByCustomerAndProduct(customer.getId(), productId);
    }


    private Product getProductById(Integer productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new ObjectNotFoundException("Product not found with ID: " + productId));
    }

    @Transactional(readOnly = true)
    public boolean checkShippingAvailability(String email) {
        Customer customer = customerService.getCustomerByEmail(email);
        Optional<Address> defaultAddress = addressBookService.getDefaultAddressByCustomer(customer);
        if (defaultAddress.isEmpty())
            return false;
        ShippingRate shippingRate = shippingRateService.getShippingRateByCountryAndState(defaultAddress.get().getCountry().getName(), defaultAddress.get().getState());
        return shippingRate != null;
    }

    @Transactional
    public void removeAllByCustomer(String customerEmail) {
        Customer customer = customerService.getCustomerByEmail(customerEmail);
        cartRepo.deleteCartItemsByCustomer(customer);
    }
}