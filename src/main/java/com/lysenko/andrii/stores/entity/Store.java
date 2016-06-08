package com.lysenko.andrii.stores.entity;

import com.lysenko.andrii.stores.service.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Store {
    private static Map<String,Store> storeRepository = new HashMap<>();
    private String name;
    private Connection conn;

    private Store(String name) {
        this.name = name;
    }

    public static Store getStore(String name) {
        synchronized (storeRepository) {
            if (storeRepository.containsKey(name)) {
                return storeRepository.get(name);
            } else {
                storeRepository.put(name, new Store(name));
                return storeRepository.get(name);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public boolean addStore() throws SQLException {
        if (Service.isStoreAdded(this, conn)) {
            return false;
        }

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Stores (store) VALUES(?)");
        try {
            ps.setString(1, getName());
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
        return true;
    }

    public boolean addProductGroup(ProductGroup productGroup) throws SQLException {
        if (!Service.isStoreAdded(productGroup.getStore(), conn)) {
            return false;
        }

        if (Service.isProductGroupAdded(productGroup, conn)) {
            return false;
        }

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Product_groups (st_id, product_group) "
                + "VALUES((SELECT id FROM Stores WHERE (store = ?)), ?)");
        try {
            ps.setString(1, productGroup.getStore().getName());
            ps.setString(2, productGroup.getName());
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
        return true;
    }

    public boolean addProduct(Product product) throws SQLException {
        if (!Service.isStoreAdded(product.getProductGroup().getStore(), conn)) {
            return false;
        }

        if (!Service.isProductGroupAdded(product.getProductGroup(), conn)) {
            return false;
        }

        if (Service.isProductAdded(product, conn)) {
            return false;
        }

        PreparedStatement ps = conn.prepareStatement("INSERT INTO Products (st_id, pg_id, product_name, price, status) "
                + "VALUES((SELECT id FROM Stores WHERE (store = ?)), (SELECT id FROM Product_groups WHERE (st_id = "
                +"(SELECT id FROM Stores WHERE (store = ?)) AND product_group = ?)), ?, ?, ?)");
        try {
            ps.setString(1, product.getProductGroup().getStore().getName());
            ps.setString(2, product.getProductGroup().getStore().getName());
            ps.setString(3, product.getProductGroup().getName());
            ps.setString(4, product.getTitle());
            ps.setBigDecimal(5, product.getPrice());
            ps.setString(6,product.getStatus().name());
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
        return true;
    }

    public boolean getProductGroups(List<ProductGroup> productGroups) throws SQLException {
        if (!Service.isStoreAdded(this, conn)) {
            return false;
        }

        PreparedStatement ps = conn.prepareStatement("SELECT p.product_group FROM Product_groups p, Stores s "
                + "WHERE (p.st_id = s.id AND s.store = ?)");
        try {
            ps.setString(1, getName());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                productGroups.add(new ProductGroup(rs.getString(1),this));
            }
        } finally {
            ps.close();
        }
        return true;
    }

    public boolean getProducts(ProductGroup productGroup, List<Product> products) throws SQLException {
        if (!Service.isStoreAdded(productGroup.getStore(), conn)) {
            return false;
        }

        if (!Service.isProductGroupAdded(productGroup, conn)) {
            return false;
        }

        PreparedStatement ps = conn.prepareStatement("SELECT product_name, price, status "
                +"FROM Products pr, Product_groups pg, Stores st where (pr.st_id = st.id "
                +"AND pr.pg_id = pg.id AND st.store = ? AND pg.product_group = ?)");
        try {
            ps.setString(1, productGroup.getStore().getName());
            ps.setString(2, productGroup.getName());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(new Product(rs.getString(1),productGroup, rs.getBigDecimal(2), ProductStatus.valueOf(rs.getString(3))));
            }
        } finally {
            ps.close();
        }
        return true;
    }

    public boolean updateProduct(Product product, ProductStatus status) throws SQLException {
        if (!Service.isStoreAdded(product.getProductGroup().getStore(), conn)) {
            return false;
        }

        if (!Service.isProductGroupAdded(product.getProductGroup(), conn)) {
            return false;
        }

        if (!Service.isProductAdded(product, conn)) {
            return false;
        }

        PreparedStatement ps = conn.prepareStatement("UPDATE Products SET status = ? "
                + "WHERE (st_id = (SELECT id FROM Stores WHERE (store = ?)) AND product_name = ?)");
        try {
            ps.setString(1, status.name());
            ps.setString(2, product.getProductGroup().getStore().getName());
            ps.setString(3, product.getTitle());
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
        return true;
    }

    public boolean updateProduct(Product product, BigDecimal price) throws SQLException {
        if (!Service.isStoreAdded(product.getProductGroup().getStore(), conn)) {
            return false;
        }

        if (!Service.isProductGroupAdded(product.getProductGroup(), conn)) {
            return false;
        }

        if (!Service.isProductAdded(product, conn)) {
            return false;
        }

        PreparedStatement ps = conn.prepareStatement("UPDATE Products SET price = ? "
                + "WHERE (st_id = (SELECT id FROM Stores WHERE (store = ?)) AND product_name = ?)");
        try {
            ps.setBigDecimal(1, price);
            ps.setString(2, product.getProductGroup().getStore().getName());
            ps.setString(3, product.getTitle());
            ps.executeUpdate(); // for INSERT, UPDATE & DELETE
        } finally {
            ps.close();
        }
        return true;
    }
}