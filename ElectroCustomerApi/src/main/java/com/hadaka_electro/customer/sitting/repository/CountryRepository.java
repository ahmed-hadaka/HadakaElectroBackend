package com.hadaka_electro.customer.sitting.repository;

import com.hadaka_electro.common.entities.sitting.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    @Query("select c from Country c left join fetch c.states order by c.name asc")
    List<Country> findAllByNameAsc();
}
