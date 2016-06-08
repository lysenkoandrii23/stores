package com.lysenko.andrii.stores.app;

import com.lysenko.andrii.stores.entity.Product;
import com.lysenko.andrii.stores.entity.ProductGroup;
import com.lysenko.andrii.stores.entity.ProductStatus;
import com.lysenko.andrii.stores.entity.Store;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Main {
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/b2test2";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "password";

    static Connection conn;

    private static class RunnableStore implements Runnable {
        private String name;

        public RunnableStore(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            Store store = Store.getStore(name);

            List<ProductGroup> productGroupsToAdd = new ArrayList<>();
            productGroupsToAdd.add(new ProductGroup("Vegetables", store));
            productGroupsToAdd.add(new ProductGroup("Fruits", store));
            productGroupsToAdd.add(new ProductGroup("Meat", store));
            List<Product> productsToAdd = new ArrayList<>();
            productsToAdd.add(new Product("Potato", productGroupsToAdd.get(0), new BigDecimal(9.80)));
            productsToAdd.add(new Product("Carrot", productGroupsToAdd.get(0), new BigDecimal(4.99)));
            productsToAdd.add(new Product("Tomato", productGroupsToAdd.get(0), new BigDecimal(35.00)));
            productsToAdd.add(new Product("Cucumber", productGroupsToAdd.get(0), new BigDecimal(21.99)));
            productsToAdd.add(new Product("Apple", productGroupsToAdd.get(1), new BigDecimal(25.99)));
            productsToAdd.add(new Product("Orange", productGroupsToAdd.get(1), new BigDecimal(31.00)));
            productsToAdd.add(new Product("Cherry", productGroupsToAdd.get(1), new BigDecimal(38.50)));
            productsToAdd.add(new Product("Pork", productGroupsToAdd.get(2), new BigDecimal(93.00)));
            productsToAdd.add(new Product("Veal", productGroupsToAdd.get(2), new BigDecimal(80.99)));
            productsToAdd.add(new Product("Mutton", productGroupsToAdd.get(2), new BigDecimal(160.05)));

            try {
                try {
                    conn = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                    store.setConnection(conn);
//                    Service.initDB(conn);

                    /*writing test data to db tables*/
                    store.addStore();
                    for(ProductGroup productGroup : productGroupsToAdd) {
                        store.addProductGroup(productGroup);
                    }
                    for(Product product : productsToAdd) {
                        store.addProduct(product);
                    }

                    Random random = new Random();
                    List<ProductGroup> productGroups = new ArrayList<>();
                    store.getProductGroups(productGroups); /*get all product groups of store from db*/

                    ProductGroup absentProductGroup = productGroups.get(random.nextInt(productGroups.size()));
                    List<Product> absentProducts = new ArrayList<>();
                    store.getProducts(absentProductGroup, absentProducts);/*get all products from random product group*/
                    for(Product product : absentProducts) {
                        store.updateProduct(product, ProductStatus.ABSENT);
                    }

                    productGroups.removeAll(Arrays.asList(absentProductGroup));
                    List<Product> otherProducts = new ArrayList<>();
                    for(ProductGroup productGroup : productGroups) {
                        store.getProducts(productGroup, otherProducts);/*get all products from another product groups*/
                    }
                    int n = otherProducts.size();
                    BigDecimal koef = new BigDecimal(1.2);
                    for(int i = 0; i < n; i++) {
                        if (i < n/2) { /*update status for half of products from another product groups*/
                            store.updateProduct(otherProducts.get(i), ProductStatus.EXPECTED);
                        } else {
                            if (otherProducts.get(i).getStatus() == ProductStatus.AVAILABLE) { /*update price for half of products*/
                                store.updateProduct(otherProducts.get(i), otherProducts.get(i).getPrice().multiply(koef));
                            }
                        }
                    }
                } finally {
                    if (conn != null) conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return;
            }
        }
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(new RunnableStore("groceryStore1"));
        Thread thread2 = new Thread(new RunnableStore("groceryStore2"));
        thread1.start();
        System.out.println("first store started " + new Date());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread2.start();
        System.out.println("second store started " + new Date());
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("both stores finished " + new Date());
    }
}