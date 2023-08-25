package org.seleniumdocker.pages;

import org.seleniumdocker.base.BasePage;
import org.seleniumdocker.base.BasePageFactory;
import org.seleniumdocker.configuration.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    private WebElement txtUsername;

    @FindBy(id = "password")
    private WebElement txtPassword;

    @FindBy(id = "login-button")
    private WebElement btnLogin;

    @FindBy(className = "error-message-container")
    private WebElement errorMessage;

    @FindBy(tagName = "h3")
    private By h3;

    public LoginPage open() {
        driver.get(ConfigManager.config().baseUrl());
        return this;
    }

    public LoginPage typeUsername(final String username) {
        clearAndType(txtUsername, username);

        return this;
    }

    public LoginPage typePassword(final String password) {
        clearAndType(txtPassword, password);
        return this;
    }

    public ProductsPage clickOnLogin() {
        btnLogin.click();

        return BasePageFactory.createInstance(driver, ProductsPage.class);
    }

    public ProductsPage loginAs(String username, String password) {
        open();
        typeUsername(username);
        typePassword(password);

        return clickOnLogin();
    }
}
