package com.hadaka_electro.internal.shippingRate;

import com.hadaka_electro.common.entities.ShippingRate;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/shipping-rates")
public class ShippingRateController {

    private final ShippingRateService shippingRateService;

    @Autowired
    public ShippingRateController(ShippingRateService shippingRateService) {
        this.shippingRateService = shippingRateService;
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<Map<String, Object>> listAllShippingRates(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "country", page = 0, direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ShippingRate> shippingRates = shippingRateService.listAllShippingRates(keyword, pageable);

        return ResponseEntity.ok(Map.of(
                "shippingRates", shippingRates
        ));
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveShippingRate(@RequestBody @Valid ShippingRate shippingRate)
            throws DuplicatedObjectException, ObjectNotFoundException {

        int id = shippingRateService.saveShippingRate(shippingRate);
        return ResponseEntity.status(HttpStatus.CREATED).body("Shipping rate with ID " + id + " saved successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShippingRate> viewShippingRateDetails(@PathVariable int id) throws ObjectNotFoundException {
        ShippingRate shippingRate = shippingRateService.findById(id);
        return ResponseEntity.ok(shippingRate);
    }

    @GetMapping("/get-by-country-and-state")
    public ResponseEntity<ShippingRate> getShippingRateByCountryAndState(@RequestParam String country, @RequestParam String state) throws ObjectNotFoundException {
        ShippingRate shippingRate = shippingRateService.getShippingRateByCountryAndState(country, state);
        return ResponseEntity.ok(shippingRate);
    }

    @PatchMapping("/update-cod-status/{id}")
    public ResponseEntity<String> updateCODStatus(@PathVariable int id) throws ObjectNotFoundException {
        boolean status = shippingRateService.updateCODSupport(id);
        if (status) {
            return ResponseEntity.status(HttpStatus.OK).body("COD support for shipping rate ID " + id + " has been enabled");
        }
        return ResponseEntity.status(HttpStatus.OK).body("COD support for shipping rate ID " + id + " has been disabled");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteShippingRate(@PathVariable int id) throws ObjectNotFoundException {
        shippingRateService.deleteShippingRate(id);
        return ResponseEntity.ok("Shipping rate with ID " + id + " has been deleted.");
    }
}