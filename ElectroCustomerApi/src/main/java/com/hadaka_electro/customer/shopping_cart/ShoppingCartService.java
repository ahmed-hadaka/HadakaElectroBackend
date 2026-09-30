package com.hadaka_electro.customer.shopping_cart;

import com.hadaka_electro.common.entities.CartItem;
import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.common.entities.product.Product;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.customer.repository.CustomerRepository;
import com.hadaka_electro.customer.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ShoppingCartService {

    private final CartItemRepository cartRepo;
    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;

    public ShoppingCartService(CartItemRepository cartRepo, ProductRepository productRepo, CustomerRepository customerRepo) {
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
    }

    @Transactional(readOnly = true)
    public List<CartItemDTO> listCartItems(String customerEmail) {
        Customer customer = getCustomerByEmail(customerEmail);
        List<CartItem> cartItems = cartRepo.findByCustomer(customer);
        if (cartItems.isEmpty())
            return List.of();
        return cartItems.stream().map(item -> {
            return new CartItemDTO(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getProduct().getMainImage(),
                    item.getProduct().getPriceAfterDiscount(),
                    item.getQuantity()
            );

        }).collect(Collectors.toList());
    }

    @Transactional
    public void addProduct(Integer productId, Integer quantity, String customerEmail) {

        if (quantity < 1 || quantity > 5) {
            throw new IllegalArgumentException("Quantity must be between 1 and 5.");
        }

        Customer customer = getCustomerByEmail(customerEmail);
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

        Customer customer = getCustomerByEmail(customerEmail);
        // if no cart items with these id's, nothing happen!
        cartRepo.updateQuantity(quantity, customer.getId(), productId);

    }

    @Transactional
    public void removeProduct(Integer productId, String customerEmail) {
        Customer customer = getCustomerByEmail(customerEmail);
        cartRepo.deleteByCustomerAndProduct(customer.getId(), productId);
    }

    private Customer getCustomerByEmail(String email) {
        return customerRepo.findCustomerByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("Customer not found for email: " + email));
    }

    private Product getProductById(Integer productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new ObjectNotFoundException("Product not found with ID: " + productId));
    }
}