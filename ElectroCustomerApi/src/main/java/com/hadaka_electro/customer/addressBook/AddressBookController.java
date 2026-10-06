package com.hadaka_electro.customer.addressBook;

import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.customer.addressBook.repository.AddressDTO;
import com.hadaka_electro.customer.security.CustomerUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address-book")
public class AddressBookController {

    private final AddressBookService addressService;

    @Autowired
    public AddressBookController(AddressBookService addressService) {
        this.addressService = addressService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<Map<String, Object>> showAddressBook(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<AddressDTO> addresses = addressService.listAddressBook(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("addresses", addresses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddressDetails(@PathVariable Integer id, @AuthenticationPrincipal CustomerUserDetails userDetails) throws ObjectNotFoundException {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AddressDTO address = addressService.getAddress(id, userDetails.getUsername());
        return ResponseEntity.ok(address);
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveAddress(@RequestBody AddressDTO addressDTO, @AuthenticationPrincipal CustomerUserDetails userDetails) throws ObjectNotFoundException {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer id = addressService.save(addressDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body("Address with ID " + id + " has been saved successfully.");
    }

    @PatchMapping("/set-default/{id}")
    public ResponseEntity<String> setDefaultAddress(@PathVariable Integer id, @AuthenticationPrincipal CustomerUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        addressService.setDefaultAddress(id, userDetails.getUsername());
        return ResponseEntity.ok("Address has been set as default successfully.");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable Integer id, @AuthenticationPrincipal CustomerUserDetails userDetails) throws ObjectNotFoundException {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        addressService.delete(id, userDetails.getUsername());
        return ResponseEntity.ok("Address has been deleted.");
    }
}