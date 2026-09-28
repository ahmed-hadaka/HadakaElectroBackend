package com.hadaka_electro.common.entities.setting;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "settings")
@Data
public class Setting {
    @Id
    @Column(name = "`key`", nullable = false, length = 128)
    private String key;

    @Column(nullable = false, length = 1024)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private SettingCategory category;

}
