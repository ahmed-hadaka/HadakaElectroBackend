package com.hadaka_electro.customer.sitting.repository;

import com.hadaka_electro.common.entities.sitting.Sitting;
import com.hadaka_electro.common.entities.sitting.SittingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SittingsRepository extends JpaRepository<Sitting, String> {

    List<Sitting> findByCategoryOrCategory(SittingCategory sittingCategory, SittingCategory sittingCategory1);
}
