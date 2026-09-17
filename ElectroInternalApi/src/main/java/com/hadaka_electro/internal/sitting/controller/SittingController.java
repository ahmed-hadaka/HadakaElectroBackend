package com.hadaka_electro.internal.sitting.controller;

import com.hadaka_electro.common.entities.sitting.Sitting;
import com.hadaka_electro.internal.sitting.dto.CountryDTO;
import com.hadaka_electro.internal.sitting.dto.StateDTO;
import com.hadaka_electro.internal.sitting.repository.CountryRepository;
import com.hadaka_electro.internal.sitting.service.SittingService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sittings")
public class SittingController {

    private final SittingService sittingService;
    private final CountryRepository countryRepository;

    public SittingController(SittingService sittingService, CountryRepository countryRepository) {
        this.sittingService = sittingService;
        this.countryRepository = countryRepository;
    }

    @GetMapping("/general")
    public ResponseEntity<Map<String, Object>> getGeneralAndCurrencySittings() {
        Map<String, Object> res = sittingService.getGeneralAndCurrencySittings();
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "/update-general", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> updateGeneralSittings(@RequestPart("updated-sittings") List<Sitting> generalSittings,
                                                        @RequestPart(value = "site-logo", required = false) MultipartFile siteLogoFile) {
        sittingService.updateGeneralSittings(generalSittings, siteLogoFile);
        return ResponseEntity.ok("General Sittings Updated Successfully");
    }

    @GetMapping("/countries")
    public ResponseEntity<List<CountryDTO>> getCountries() {
        List<CountryDTO> countryDTOList = sittingService.getCountries();
        return ResponseEntity.ok(countryDTOList);
    }

    @PostMapping("/save-country")
    public ResponseEntity<String> saveCountry(@RequestBody @Valid CountryDTO countryDto) {
        String name = sittingService.saveCountry(countryDto);
        return ResponseEntity.ok("Country : " + name + " Saved Successfully");
    }

    @DeleteMapping("/delete-country/{country_id}")
    public ResponseEntity<String> deleteCountry(@PathVariable("country_id") Long countryId) {
        sittingService.deleteCountryById(countryId);
        return ResponseEntity.ok("Country with id: " + countryId + " Deleted Successfully");

    }

    @GetMapping("/states/{country_id}")
    public ResponseEntity<List<StateDTO>> getStatesOfCountry(@PathVariable("country_id") Long countryId) {
        List<StateDTO> countryStates = sittingService.getCountryStates(countryId);
        return ResponseEntity.ok(countryStates);
    }

    @PostMapping("/save-state")
    public ResponseEntity<String> saveState(@RequestBody @Valid StateDTO stateDto) {
        String stateName = sittingService.saveState(stateDto);
        return ResponseEntity.ok("State: " + stateName + " Saved Successfully");

    }

    @DeleteMapping("/delete-state/{state_id}")
    public ResponseEntity<String> deleteState(@PathVariable("state_id") Long stateId) {
        sittingService.deleteStateById(stateId);
        return ResponseEntity.ok("State with id: " + stateId + " Deleted Successfully");
    }

    @GetMapping("/mail-server")
    public ResponseEntity<List<Sitting>> getMailServerSittings() {
        List<Sitting> mailServerSittings = sittingService.getMailServerSittings();
        return ResponseEntity.ok(mailServerSittings);
    }
}
