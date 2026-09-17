package com.hadaka_electro.internal.sitting.repository;

import com.hadaka_electro.common.entities.sitting.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    boolean existsById(Long countryId);
}
