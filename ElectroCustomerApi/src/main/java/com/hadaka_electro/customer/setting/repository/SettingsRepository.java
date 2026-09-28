package com.hadaka_electro.customer.setting.repository;

import com.hadaka_electro.common.entities.setting.Setting;
import com.hadaka_electro.common.entities.setting.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingsRepository extends JpaRepository<Setting, String> {

    List<Setting> findByCategoryOrCategory(SettingCategory settingCategory, SettingCategory settingCategory1);
}
