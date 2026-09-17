package com.hadaka_electro.customer.sitting;

import com.hadaka_electro.common.entities.sitting.Sitting;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SittingController {

    private final SittingService sittingService;

    public SittingController(SittingService sittingService) {
        this.sittingService = sittingService;
    }

    // well be called inside the APP_INITIALIZATION component in SPA and stored in session storage.
    @GetMapping("/general-sittings")
    public ResponseEntity<List<Sitting>> getGeneralSittings() {
        List<Sitting> generalAndCurrencySittings = sittingService.getGeneralAndCurrencySittings();
        return ResponseEntity.ok(generalAndCurrencySittings);
    }
}
