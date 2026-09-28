package com.hadaka_electro.internal.setting.service;

import com.hadaka_electro.common.entities.setting.*;
import com.hadaka_electro.common.exception.FileStorageException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.setting.dto.CountryDTO;
import com.hadaka_electro.internal.setting.dto.StateDTO;
import com.hadaka_electro.internal.setting.mapper.CountryMapper;
import com.hadaka_electro.internal.setting.mapper.StateMapper;
import com.hadaka_electro.internal.setting.repository.CountryRepository;
import com.hadaka_electro.internal.setting.repository.CurrencyRepository;
import com.hadaka_electro.internal.setting.repository.SettingRepository;
import com.hadaka_electro.internal.setting.repository.StateRepository;
import com.hadaka_electro.internal.utils.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SettingService {

    private final CurrencyRepository currencyRepository;
    private final SettingRepository settingRepository;
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final StateRepository stateRepository;
    private final StateMapper stateMapper;

    public SettingService(CurrencyRepository currencyRepository, SettingRepository settingRepository, CountryRepository countryRepository, CountryMapper countryMapper, StateRepository stateRepository, StateMapper stateMapper) {
        this.currencyRepository = currencyRepository;
        this.settingRepository = settingRepository;
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.stateRepository = stateRepository;
        this.stateMapper = stateMapper;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getGeneralAndCurrencySettings() {
        List<Setting> generalSettings = settingRepository.findByCategoryOrCategory(SettingCategory.GENERAL, SettingCategory.CURRENCY);
        List<Currency> currencyList = currencyRepository.findAll();
        return Map.of("generalSettings", generalSettings, "currencyList", currencyList);
    }

    @Transactional(readOnly = true)
    public List<CountryDTO> getCountries() {
        return countryRepository.findAll()
                .stream()
                .map(country -> countryMapper.toDTO(country))
                .toList();
    }

    @Transactional
    public String saveCountry(CountryDTO countryDto) {
        return countryRepository.save(countryMapper.toEntity(countryDto)).getName();
    }

    @Transactional(readOnly = true)
    public List<StateDTO> getCountryStates(Long countryId) {
        return stateRepository.findByCountryIdOrderByNameAsc(countryId).stream()
                .map(state -> stateMapper.toDTO(state)).toList();
    }

    @Transactional
    public String saveState(StateDTO stateDto) {
        State state = stateMapper.toEntity(stateDto);
        Country country = countryRepository.findById(state.getCountry().getId()).orElseThrow(() ->
                new ObjectNotFoundException("No countries found with this id: " + stateDto.countryId())
        );
        country.addState(state); // dirty check will save the updates cuz inside a Transaction block
        return state.getName();
    }

    @Transactional(readOnly = true)
    public List<Setting> getMailServerSettings() {
        return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
    }

    @Transactional
    public void updateGeneralSettings(List<Setting> generalSettings, MultipartFile siteLogoFile) {

        List<String> updatedSettingsKeys = generalSettings.stream().map(setting -> setting.getKey()).toList();

        List<Setting> existedSettingOfKeys = settingRepository.findAllById(updatedSettingsKeys);

        Map<String, String> updatedSettingsMap = generalSettings.stream()
                .collect(Collectors.toMap(setting -> setting.getKey(), setting -> setting.getValue()));

        // dirty checking will occur here and updates will be saved to db.
        existedSettingOfKeys.forEach(existedSetting -> {
            existedSetting.setValue(updatedSettingsMap.get(existedSetting.getKey()));
        });


        if (siteLogoFile != null && !siteLogoFile.isEmpty()) {
            String uploadDir = "default_images/siteLogos";
            String fileName = StringUtils.cleanPath(siteLogoFile.getOriginalFilename());
            try {
                FileUtil.saveFile(uploadDir, fileName, siteLogoFile);
            } catch (IOException e) {
                throw new FileStorageException("Could not store site logo " + fileName + ". Please try again! " + e);
            }
        }
    }

    @Transactional
    public void deleteCountryById(Long countryId) {
        if (countryRepository.existsById(countryId)) {
            countryRepository.deleteById(countryId);
        } else {
            throw new ObjectNotFoundException("No Countries with this id: " + countryId);
        }
    }

    @Transactional
    public void deleteStateById(Long stateId) {
        State state = stateRepository.findById(stateId).orElseThrow(() ->
                new ObjectNotFoundException("No states with this id: " + stateId));

        Long countryId = state.getCountry().getId();

        Country country = countryRepository.findById(countryId).orElseThrow(() ->
                new ObjectNotFoundException("No Countries with this id: " + countryId));

        country.removeState(state); // orphan removal will occur here.
    }

    @Transactional(readOnly = true)
    public List<Setting> getMailTemplatesSettings() {
        return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
    }

}
