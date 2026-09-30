package com.hadaka_electro.customer.shopping_cart;

import com.hadaka_electro.customer.security.CustomerUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    private final ShoppingCartService cartService;

    public ShoppingCartController(ShoppingCartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<List<CartItemDTO>> viewCart(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<CartItemDTO> cartItems = cartService.listCartItems(userDetails.getUsername());
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/add/{productId}/{quantity}")
    public ResponseEntity<Map<String, String>> addProductToCart(
            @PathVariable Integer productId,
            @PathVariable Integer quantity,
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Map.of("message", "Please Login/Reister first!"));
        }
        cartService.addProduct(productId, quantity, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Product added to cart successfully."));
    }

    @PutMapping("/update/{productId}/{quantity}")
    public ResponseEntity<Map<String, String>> updateQuantity(
            @PathVariable Integer productId,
            @PathVariable Integer quantity,
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Map.of("message", "Please Login/Reister first!"));
        }
        cartService.updateQuantity(productId, quantity, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Cart updated successfully."));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Map<String, String>> removeProductFromCart(
            @PathVariable Integer productId,
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Map.of("message", "Please Login/Reister first!"));
        }
        cartService.removeProduct(productId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Product removed from cart successfully."));
    }
}