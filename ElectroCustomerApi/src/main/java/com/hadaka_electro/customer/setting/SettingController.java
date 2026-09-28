package com.hadaka_electro.customer.setting;

import com.hadaka_electro.common.entities.setting.Setting;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    // well be called inside the APP_INITIALIZATION component in SPA and stored in session storage.
    @GetMapping("/general-settings")
    public ResponseEntity<List<Setting>> getGeneralSettings() {
        List<Setting> generalAndCurrencySettings = settingService.getGeneralAndCurrencySettings();
        return ResponseEntity.ok(generalAndCurrencySettings);
    }
}
