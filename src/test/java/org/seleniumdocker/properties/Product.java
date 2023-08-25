package org.seleniumdocker.properties;

public class Product {
    protected String prodName;
    protected String prodDescription;

    public Product(String prodName, String prodDescription) {
        this.prodName = prodName;
        this.prodDescription = prodDescription;
    }

    public String getProdName() {
        return prodName;
    }

    public String getProdDescription() {
        return prodDescription;
    }
}
