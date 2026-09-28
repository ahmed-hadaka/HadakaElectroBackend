package com.hadaka_electro.internal.setting.repository;

import com.hadaka_electro.common.entities.setting.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
