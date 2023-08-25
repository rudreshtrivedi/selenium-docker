package org.seleniumdocker.components;

import org.openqa.selenium.WebDriver;

/**
 * @author tahanima
 */
public abstract class BaseComponent {

    protected WebDriver driver;

    public BaseComponent(WebDriver driver) {
        this.driver = driver;
    }
}
