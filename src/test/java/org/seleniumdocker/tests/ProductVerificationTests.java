package org.seleniumdocker.tests;

import org.seleniumdocker.configuration.ConfigManager;
import org.seleniumdocker.properties.Product;
import org.testng.annotations.Test;

public class ProductVerificationTests extends TestBase {

    Product backpack;

    @Test()
    public void productTests() {
        backpack = new Product("Sauce Labs Backpack","desc");
        loginPage
                .open()
                .typeUsername(ConfigManager.config().username())
                .typePassword(ConfigManager.config().password())
                .clickOnLogin()
                .verifyPageTitle("Products")
                .openProduct(backpack);
    }
}
