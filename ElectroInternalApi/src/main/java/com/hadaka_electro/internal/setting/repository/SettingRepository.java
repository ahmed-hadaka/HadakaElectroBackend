package com.hadaka_electro.internal.setting.repository;

import com.hadaka_electro.common.entities.setting.Setting;
import com.hadaka_electro.common.entities.setting.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {

    List<Setting> findByCategory(SettingCategory settingCategory);

    List<Setting> findByCategoryOrCategory(SettingCategory category1, SettingCategory category2);
}
