package com.hadaka_electro.customer.sitting;

import com.hadaka_electro.common.entities.sitting.Sitting;
import com.hadaka_electro.common.entities.sitting.SittingCategory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SittingService {


    private final SittingsRepository sittingsRepository;

    public SittingService(SittingsRepository sittingsRepository) {
        this.sittingsRepository = sittingsRepository;
    }

    @Transactional(readOnly = true)
    public List<Sitting> getGeneralAndCurrencySittings() {
        return sittingsRepository.findByCategoryOrCategory(SittingCategory.GENERAL, SittingCategory.CURRENCY);
    }
}
