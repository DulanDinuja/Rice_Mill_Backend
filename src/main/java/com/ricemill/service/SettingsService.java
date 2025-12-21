package com.ricemill.service;

import com.ricemill.dto.SettingsDto;
import com.ricemill.entity.Settings;
import com.ricemill.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettingsService {
    
    private final SettingsRepository settingsRepository;
    
    public SettingsDto.Response getSettings() {
        return SettingsDto.Response.builder()
                .companyName(getSettingValue("companyName"))
                .address(getSettingValue("address"))
                .contact(getSettingValue("contact"))
                .timezone(getSettingValue("timezone"))
                .lowStockThreshold(getSettingValue("lowStockThreshold"))
                .build();
    }
    
    @Transactional
    public SettingsDto.Response updateSettings(SettingsDto.UpdateRequest request) {
        for (Map.Entry<String, String> entry : request.getSettings().entrySet()) {
            Settings setting = settingsRepository.findBySettingKey(entry.getKey())
                    .orElseGet(() -> {
                        Settings newSetting = new Settings();
                        newSetting.setSettingKey(entry.getKey());
                        return newSetting;
                    });
            
            setting.setSettingValue(entry.getValue());
            setting.setUpdatedAt(LocalDateTime.now());
            settingsRepository.save(setting);
        }
        
        return getSettings();
    }
    
    private String getSettingValue(String key) {
        return settingsRepository.findBySettingKey(key)
                .map(Settings::getSettingValue)
                .orElse("");
    }
}
