package org.seleniumdocker.base;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;

import static org.seleniumdocker.configuration.ConfigManager.config;

public enum BrowserEnum {
    CHROME {
        @Override
        public RemoteWebDriver getDriver() {
//            WebDriverManager.chromedriver().setup();
            RemoteWebDriver driver = new ChromeDriver(getOptions());

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config().timeout()));
            driver.manage().window().maximize();

            return driver;
        }

        private ChromeOptions getOptions() {
            ChromeOptions options = new ChromeOptions();

            options.setAcceptInsecureCerts(true);

            if (Boolean.TRUE.equals(config().headless())) {
                options.addArguments("--headless=new");
            }

            return options;
        }
    },

    FIREFOX {
        @Override
        public RemoteWebDriver getDriver() {
            RemoteWebDriver driver = new FirefoxDriver(getOptions());

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config().timeout()));
            driver.manage().window().maximize();

            return driver;
        }

        private FirefoxOptions getOptions() {
            FirefoxOptions options = new FirefoxOptions();

            options.setAcceptInsecureCerts(true);

            if (Boolean.TRUE.equals(config().headless())) {
                options.addArguments("--headless=new");
            }

            return options;
        }
    };

    public abstract RemoteWebDriver getDriver();
}
