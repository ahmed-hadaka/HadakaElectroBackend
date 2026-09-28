package com.hadaka_electro.internal.setting.mapper;

import com.hadaka_electro.common.entities.setting.Country;
import com.hadaka_electro.common.entities.setting.State;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.setting.dto.StateDTO;
import com.hadaka_electro.internal.setting.repository.CountryRepository;
import com.hadaka_electro.internal.setting.repository.StateRepository;
import org.springframework.stereotype.Component;

@Component
public class StateMapper {


    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;

    public StateMapper(StateRepository stateRepository, CountryRepository countryRepository) {
        this.stateRepository = stateRepository;
        this.countryRepository = countryRepository;
    }

    public StateDTO toDTO(State state) {
        if (state == null) {
            return null;
        }
        Long countryId = state.getCountry().getId();

        return new StateDTO(
                state.getId(),
                state.getName(),
                countryId
        );
    }

    public State toEntity(StateDTO dto) {
        if (dto == null) {
            return null;
        }
        State state = stateRepository.findById(dto.id()).orElse(new State());

        state.setName(dto.name());

        // guarantee that dto.countryId will be >= 1
        Country country = countryRepository.findById(dto.countryId())
                .orElseThrow(() -> new ObjectNotFoundException("No Countries with this id: " + dto.countryId()));
        state.setCountry(country);


        return state;
    }
}