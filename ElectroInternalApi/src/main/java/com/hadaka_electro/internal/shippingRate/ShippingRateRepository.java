package com.hadaka_electro.internal.shippingRate;

import com.hadaka_electro.common.entities.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country LIKE %:keyword% OR sr.state LIKE %:keyword%")
    Page<ShippingRate> findAllByKeyword(String keyword, Pageable pageable);

    Optional<ShippingRate> findByCountryAndState(String country, String state);

    long countById(Integer id);

    //todo
    @Modifying
    @Query("UPDATE ShippingRate sr SET sr.codSupported = ?2 WHERE sr.id = ?1")
    void updateCODSupport(Integer id, boolean codSupported);
}