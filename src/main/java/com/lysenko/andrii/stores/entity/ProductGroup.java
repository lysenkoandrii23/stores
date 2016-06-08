package com.lysenko.andrii.stores.entity;

public class ProductGroup {

    private String name;
    private Store store;

    public ProductGroup(String name, Store store) {
        this.name = name;
        this.store = store;
    }

    public String getName() {
        return name;
    }

    public Store getStore() {
        return store;
    }
}
