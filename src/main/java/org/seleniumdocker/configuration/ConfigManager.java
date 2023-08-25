package org.seleniumdocker.configuration;

import org.aeonbits.owner.ConfigCache;

public class ConfigManager {
    private ConfigManager() {}

    public static ConfigProperties config() {
        return ConfigCache.getOrCreate(ConfigProperties.class);
    }
}
