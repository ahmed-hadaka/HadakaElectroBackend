package com.hadaka_electro.internal.sitting.repository;

import com.hadaka_electro.common.entities.sitting.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
