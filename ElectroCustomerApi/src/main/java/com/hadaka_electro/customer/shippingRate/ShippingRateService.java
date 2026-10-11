package com.hadaka_electro.customer.shippingRate;

import com.hadaka_electro.common.entities.ShippingRate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ShippingRateService {
    private final ShippingRateRepository shippingRateRepository;

    public ShippingRateService(ShippingRateRepository shippingRateRepository) {
        this.shippingRateRepository = shippingRateRepository;
    }

    @Transactional(readOnly = true)
    public ShippingRate getShippingRateByCountryAndState(String country, String state) {
        Optional<ShippingRate> existingDest = shippingRateRepository.findByCountryAndState(country, state);
        return existingDest.orElse(null);
    }
}
