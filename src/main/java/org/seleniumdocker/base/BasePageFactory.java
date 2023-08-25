package org.seleniumdocker.base;

import org.openqa.selenium.WebDriver;

public class BasePageFactory {
    private BasePageFactory() {}

    public static <T extends BasePage> T createInstance(final WebDriver driver, final Class<T> clazz) {
        try {
            BasePage instance = clazz.getDeclaredConstructor().newInstance();

            instance.initDriverAndElements(driver);
            instance.initComponents();

            return clazz.cast(instance);
        } catch (Exception e) {
            throw new InstantiationError("Page class instantiation failed with exception: \n"+ e);
        }
    }
}
