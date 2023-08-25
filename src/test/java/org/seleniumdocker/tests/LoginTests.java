package org.seleniumdocker.tests;

import org.seleniumdocker.configuration.ConfigManager;
import org.testng.annotations.Test;


public class LoginTests extends TestBase {

    @Test()
    public void testCorrectUserNameAndCorrectPassword() {
        loginPage
                .open()
                .typeUsername(ConfigManager.config().username())
                .typePassword(ConfigManager.config().password())
                .clickOnLogin()
                .verifyPageTitle("Products");
    }

    @Test()
    public void testCorrectUserNameAndCorrectPassword2() {
        loginPage
                .open()
                .typeUsername(ConfigManager.config().username())
                .typePassword(ConfigManager.config().password())
                .clickOnLogin()
                .verifyPageTitle("Products");
    }
}
