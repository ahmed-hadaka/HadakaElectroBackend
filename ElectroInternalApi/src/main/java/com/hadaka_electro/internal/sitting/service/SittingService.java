package com.hadaka_electro.internal.sitting.service;

import com.hadaka_electro.common.entities.sitting.*;
import com.hadaka_electro.common.exception.FileStorageException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.sitting.dto.CountryDTO;
import com.hadaka_electro.internal.sitting.dto.StateDTO;
import com.hadaka_electro.internal.sitting.mapper.CountryMapper;
import com.hadaka_electro.internal.sitting.mapper.StateMapper;
import com.hadaka_electro.internal.sitting.repository.CountryRepository;
import com.hadaka_electro.internal.sitting.repository.CurrencyRepository;
import com.hadaka_electro.internal.sitting.repository.SittingRepository;
import com.hadaka_electro.internal.sitting.repository.StateRepository;
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
public class SittingService {

    private final CurrencyRepository currencyRepository;
    private final SittingRepository sittingRepository;
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    private final StateRepository stateRepository;
    private final StateMapper stateMapper;

    public SittingService(CurrencyRepository currencyRepository, SittingRepository sittingRepository, CountryRepository countryRepository, CountryMapper countryMapper, StateRepository stateRepository, StateMapper stateMapper) {
        this.currencyRepository = currencyRepository;
        this.sittingRepository = sittingRepository;
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.stateRepository = stateRepository;
        this.stateMapper = stateMapper;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getGeneralAndCurrencySittings() {
        List<Sitting> generalSittings = sittingRepository.findByCategoryOrCategory(SittingCategory.GENERAL, SittingCategory.CURRENCY);
        List<Currency> currencyList = currencyRepository.findAll();
        return Map.of("generalSittings", generalSittings, "currencyList", currencyList);
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
    public List<Sitting> getMailServerSittings() {
        return sittingRepository.findByCategory(SittingCategory.MAIL_SERVER);
    }

    @Transactional
    public void updateGeneralSittings(List<Sitting> generalSittings, MultipartFile siteLogoFile) {

        List<String> updatedSittingsKeys = generalSittings.stream().map(sitting -> sitting.getKey()).toList();

        List<Sitting> existedSittingOfKeys = sittingRepository.findAllById(updatedSittingsKeys);

        Map<String, String> updatedSittingsMap = generalSittings.stream()
                .collect(Collectors.toMap(sitting -> sitting.getKey(), sitting -> sitting.getValue()));

        // dirty checking will occur here and updates will be saved to db.
        existedSittingOfKeys.forEach(existedSitting -> {
            existedSitting.setValue(updatedSittingsMap.get(existedSitting.getKey()));
        });


        if (siteLogoFile != null && !siteLogoFile.isEmpty()) {
            String uploadDir = "default_images/siteLogo";
            String fileName = StringUtils.cleanPath(siteLogoFile.getOriginalFilename());
            try {
                FileUtil.cleanDir(uploadDir);
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
}
