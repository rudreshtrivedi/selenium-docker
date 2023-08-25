package org.seleniumdocker.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.seleniumdocker.base.BasePage;
import org.seleniumdocker.base.BasePageFactory;
import org.seleniumdocker.components.Header;
import org.seleniumdocker.components.SideNavMenu;
import org.seleniumdocker.properties.Product;
import org.testng.Assert;

public class ProductDetailPage extends BasePage {

    private Header header;
    private SideNavMenu sideNavMenu;

    @FindBy(className = "title")
    private WebElement lblTitle;


    @Override
    public void initComponents() {
        header = new Header(driver);
        sideNavMenu = new SideNavMenu(driver);
    }

    public String getTitle() {
        return lblTitle.getText();
    }

    public ProductDetailPage verifyPageTitle(String title) {
        Assert.assertEquals(getTitle(),title);
        return this;
    }

    public ProductDetailPage openProduct(Product product) {
        driver.findElement(By.xpath("//div[.='"+product.getProdName()+"']")).click();
        return BasePageFactory.createInstance(driver, ProductDetailPage.class);
    }
}
