package com.hadaka_electro.internal.setting.repository;

import com.hadaka_electro.common.entities.setting.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    boolean existsById(Long countryId);
}
