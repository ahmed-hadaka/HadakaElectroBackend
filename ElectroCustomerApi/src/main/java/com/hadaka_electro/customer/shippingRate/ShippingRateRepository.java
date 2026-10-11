package com.hadaka_electro.customer.shippingRate;

import com.hadaka_electro.common.entities.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {

    Optional<ShippingRate> findByCountryAndState(String country, String state);
}
