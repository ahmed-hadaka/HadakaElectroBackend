package com.hadaka_electro.internal.setting.repository;

import com.hadaka_electro.common.entities.setting.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
//    @Query("select s from State s where s.country.id = ?1 order by s.name asc")
//    List<State> findAllByCountryId(int countryId);

    List<State> findByCountryIdOrderByNameAsc(Long countryId);
}
