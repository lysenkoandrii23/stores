package com.lysenko.andrii.stores.entity;

import java.math.BigDecimal;

public class Product {

    private String title;
    private BigDecimal price;
    private ProductStatus status;
    private ProductGroup productGroup;

    public Product(String title, ProductGroup productGroup, BigDecimal price, ProductStatus status) {
        this.title = title;
        this.productGroup = productGroup;
        this.price = price;
        this.status = status;
    }

    public Product(String title, ProductGroup productGroup, BigDecimal price) {
        this(title, productGroup, price, ProductStatus.AVAILABLE);
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public ProductGroup getProductGroup() {
        return productGroup;
    }
}
