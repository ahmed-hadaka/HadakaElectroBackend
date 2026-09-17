package com.hadaka_electro.internal.sitting.repository;

import com.hadaka_electro.common.entities.sitting.Sitting;
import com.hadaka_electro.common.entities.sitting.SittingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SittingRepository extends JpaRepository<Sitting, String> {

    List<Sitting> findByCategory(SittingCategory sittingCategory);

    List<Sitting> findByCategoryOrCategory(SittingCategory category1, SittingCategory category2);
}
