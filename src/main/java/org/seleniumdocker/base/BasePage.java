package org.seleniumdocker.base;

import com.assertthat.selenium_shutterbug.core.Shutterbug;
import org.seleniumdocker.configuration.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class BasePage {
    protected WebDriver driver;

    public void initDriverAndElements(final WebDriver webdriver) {
        this.driver = webdriver;

        PageFactory.initElements(driver, this);
    }

    public void initComponents() {}

    public String getUrl() {
        return driver.getCurrentUrl();
    }

    public void captureScreenshot(String fileName) {
        Shutterbug.shootPage(driver).withName(fileName).save(ConfigManager.config().baseScreenshotPath());
    }

    protected void clearAndType(WebElement elem, String text) {
        elem.clear();
        elem.sendKeys(text);
    }

}
