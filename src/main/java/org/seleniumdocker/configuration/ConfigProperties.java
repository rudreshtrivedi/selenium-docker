package org.seleniumdocker.configuration;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.*;

@LoadPolicy(LoadType.MERGE)
@Sources({"system:properties", "classpath:config.properties"})
public interface ConfigProperties extends Config {
    @Key("browser")
    String browser();

    @Key("headless")
    boolean headless();

    @Key("containerized")
    boolean containerized();

    @Key("timeout")
    int timeout();

    @Key("base.url")
    String baseUrl();

    @Key("username")
    String username();

    @Key("password")
    String password();

    @Key("shouldRecord")
    String shouldRecordTests();

    @Key("base.screenshot.path")
    String baseScreenshotPath();
}
