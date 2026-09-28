package com.hadaka_electro.customer.setting;

import com.hadaka_electro.common.entities.setting.Setting;
import com.hadaka_electro.common.entities.setting.SettingCategory;
import com.hadaka_electro.customer.setting.repository.SettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SettingService {


    private final SettingsRepository settingsRepository;

    public SettingService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Transactional(readOnly = true)
    public List<Setting> getGeneralAndCurrencySettings() {
        return settingsRepository.findByCategoryOrCategory(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }
}
