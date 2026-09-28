package com.hadaka_electro.internal.setting.controller;

import com.hadaka_electro.common.entities.setting.Setting;
import com.hadaka_electro.internal.setting.dto.CountryDTO;
import com.hadaka_electro.internal.setting.dto.StateDTO;
import com.hadaka_electro.internal.setting.service.SettingService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/settings")
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> getGeneralAndCurrencySettings() {
        Map<String, Object> res = settingService.getGeneralAndCurrencySettings();
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "/update-general", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updateGeneralSettings(@RequestPart("updated-settings") List<Setting> generalSettings,
                                                        @RequestPart(value = "site-logo", required = false) MultipartFile siteLogoFile) {
        settingService.updateGeneralSettings(generalSettings, siteLogoFile);
        return ResponseEntity.ok("General Settings Updated Successfully");
    }

    @GetMapping("/countries")
    public ResponseEntity<List<CountryDTO>> getCountries() {
        List<CountryDTO> countryDTOList = settingService.getCountries();
        return ResponseEntity.ok(countryDTOList);
    }

    @PostMapping("/save-country")
    public ResponseEntity<String> saveCountry(@RequestBody @Valid CountryDTO countryDto) {
        String name = settingService.saveCountry(countryDto);
        return ResponseEntity.ok("Country : " + name + " Saved Successfully");
    }

    @DeleteMapping("/delete-country/{country_id}")
    public ResponseEntity<String> deleteCountry(@PathVariable("country_id") Long countryId) {
        settingService.deleteCountryById(countryId);
        return ResponseEntity.ok("Country with id: " + countryId + " Deleted Successfully");

    }

    @GetMapping("/states/{country_id}")
    public ResponseEntity<List<StateDTO>> getStatesOfCountry(@PathVariable("country_id") Long countryId) {
        List<StateDTO> countryStates = settingService.getCountryStates(countryId);
        return ResponseEntity.ok(countryStates);
    }

    @PostMapping("/save-state")
    public ResponseEntity<String> saveState(@RequestBody @Valid StateDTO stateDto) {
        String stateName = settingService.saveState(stateDto);
        return ResponseEntity.ok("State: " + stateName + " Saved Successfully");

    }

    @DeleteMapping("/delete-state/{state_id}")
    public ResponseEntity<String> deleteState(@PathVariable("state_id") Long stateId) {
        settingService.deleteStateById(stateId);
        return ResponseEntity.ok("State with id: " + stateId + " Deleted Successfully");
    }

    @GetMapping("/mail-server")
    public ResponseEntity<List<Setting>> getMailServerSettings() {
        List<Setting> mailServerSettings = settingService.getMailServerSettings();
        return ResponseEntity.ok(mailServerSettings);
    }

    @GetMapping("/mail-templates")
    public ResponseEntity<List<Setting>> getMailTemplatesSettings() {
        List<Setting> mailTemplatesSettings = settingService.getMailTemplatesSettings();
        return ResponseEntity.ok(mailTemplatesSettings);
    }
}
