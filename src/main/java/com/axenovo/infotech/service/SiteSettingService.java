package com.axenovo.infotech.service;

import com.axenovo.infotech.entity.SiteSetting;
import com.axenovo.infotech.repository.SiteSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SiteSettingService {

    public static final String MAINTENANCE_ENABLED_KEY = "site.maintenance.enabled";
    public static final String DEFAULT_MAINTENANCE_MESSAGE =
        "Our website is currently under maintenance. Please check again after some time.";

    private final SiteSettingRepository siteSettingRepository;

    public SiteSettingService(SiteSettingRepository siteSettingRepository) {
        this.siteSettingRepository = siteSettingRepository;
    }

    @Transactional(readOnly = true)
    public String get(String key, String fallback) {
        return siteSettingRepository.findBySettingKey(key)
            .map(SiteSetting::getSettingValue)
            .filter(value -> !value.isBlank())
            .orElse(fallback);
    }

    @Transactional(readOnly = true)
    public Map<String, String> getMany(Map<String, String> defaults) {
        Map<String, String> values = new LinkedHashMap<>();
        defaults.forEach((key, fallback) -> values.put(key, get(key, fallback)));
        return values;
    }

    @Transactional
    public void save(String key, String value) {
        SiteSetting setting = siteSettingRepository.findBySettingKey(key).orElseGet(SiteSetting::new);
        setting.setSettingKey(key);
        setting.setSettingValue(value == null ? "" : value.trim());
        siteSettingRepository.save(setting);
    }

    @Transactional
    public void saveAll(Map<String, String> values) {
        values.forEach(this::save);
    }

    @Transactional(readOnly = true)
    public boolean isMaintenanceModeEnabled() {
        return parseBoolean(get(MAINTENANCE_ENABLED_KEY, "false"));
    }

    @Transactional
    public void saveMaintenanceMode(boolean enabled) {
        save(MAINTENANCE_ENABLED_KEY, String.valueOf(enabled));
    }

    @Transactional(readOnly = true)
    public String getMaintenanceMessage() {
        return DEFAULT_MAINTENANCE_MESSAGE;
    }

    private boolean parseBoolean(String value) {
        return "true".equalsIgnoreCase(value)
            || "1".equals(value)
            || "yes".equalsIgnoreCase(value)
            || "on".equalsIgnoreCase(value);
    }
}
