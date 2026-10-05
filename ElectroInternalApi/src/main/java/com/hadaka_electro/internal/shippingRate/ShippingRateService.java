package com.hadaka_electro.internal.shippingRate;

import com.hadaka_electro.common.entities.ShippingRate;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ShippingRateService {

    private final ShippingRateRepository shippingRateRepository;

    @Autowired
    public ShippingRateService(ShippingRateRepository shippingRateRepository) {
        this.shippingRateRepository = shippingRateRepository;
    }

    public Page<ShippingRate> listAllShippingRates(String keyword, Pageable pageable) {
        Page<ShippingRate> page;
        if (keyword != null && !keyword.isEmpty()) {
            page = shippingRateRepository.findAllByKeyword(keyword, pageable);
        } else {
            page = shippingRateRepository.findAll(pageable);
        }
        return page;
    }

    @Transactional
    public int saveShippingRate(ShippingRate shippingRate) throws DuplicatedObjectException, ObjectNotFoundException {
        // Check if destination already exists to prevent duplicates
        Optional<ShippingRate> existingDest = shippingRateRepository.findByCountryAndState(shippingRate.getCountry(), shippingRate.getState());

        if (existingDest.isPresent() && (shippingRate.getId() == null || !existingDest.get().getId().equals(shippingRate.getId()))) {
            throw new DuplicatedObjectException("There is already a rate for the destination " + shippingRate.getState() + ", " + shippingRate.getCountry());
        }

        if (shippingRate.getId() == 0)
            shippingRate.setId(null);
        ShippingRate savedRate = shippingRateRepository.save(shippingRate);
        return savedRate.getId();
    }

    public ShippingRate findById(int id) throws ObjectNotFoundException {
        ShippingRate rate = shippingRateRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("No shipping rate found with ID: " + id));
        return rate;
    }

    @Transactional
    public void deleteShippingRate(int id) throws ObjectNotFoundException {
        if (shippingRateRepository.countById(id) > 0) {
            shippingRateRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundException("No shipping rate found with ID: " + id);
        }
    }

    @Transactional
    public boolean updateCODSupport(int id) throws ObjectNotFoundException {
        Optional<ShippingRate> rate = shippingRateRepository.findById(id);
        boolean currentStatus;
        if (rate.isPresent()) {
            currentStatus = rate.get().isCodSupported();
            shippingRateRepository.updateCODSupport(id, !currentStatus);
        } else {
            throw new ObjectNotFoundException("Shipping rate not found with id: " + id);
        }
        return !currentStatus;
    }
}