package com.lysenko.andrii.stores.service;

import com.lysenko.andrii.stores.entity.Product;
import com.lysenko.andrii.stores.entity.ProductGroup;
import com.lysenko.andrii.stores.entity.Store;

import java.sql.*;

public class Service {
    public static boolean isStoreAdded(Store store, Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Stores WHERE (store = ?)");
        try {
            ps.setString(1, store.getName());
            ResultSet rs = ps.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                rowCount = rs.getInt(1);
            }
            if (rowCount > 0) {
                return true;
            }
        } finally {
            ps.close();
        }
        return false;
    }

    public static boolean isProductGroupAdded(ProductGroup productGroup, Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Product_groups p, Stores s "
                + "WHERE (p.st_id = s.id AND s.store = ? AND p.product_group = ?)");
        try {
            ps.setString(1, productGroup.getStore().getName());
            ps.setString(2, productGroup.getName());
            ResultSet rs = ps.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                rowCount = rs.getInt(1);
            }
            if (rowCount > 0) {
                return true;
            }
        } finally {
            ps.close();
        }
        return false;
    }

    public static boolean isProductAdded(Product product, Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Products p, Stores s "
                + "WHERE (p.st_id = s.id AND s.store = ? AND p.product_name = ?)");
        try {
            ps.setString(1, product.getProductGroup().getStore().getName());
            ps.setString(2, product.getTitle());
            ResultSet rs = ps.executeQuery();
            int rowCount = 0;
            while (rs.next()) {
                rowCount = rs.getInt(1);
            }
            if (rowCount > 0) {
                return true;
            }
        } finally {
            ps.close();
        }
        return false;
    }

    private static void initDB(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        try {
            st.execute("DROP TABLE IF EXISTS Stores");
            st.execute("CREATE TABLE Stores (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    +"store VARCHAR(20) NOT NULL)");
            st.execute("DROP TABLE IF EXISTS Product_groups");
            st.execute("CREATE TABLE Product_groups (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    +"st_id INT NOT NULL, product_group VARCHAR(20) NOT NULL)");
            st.execute("DROP TABLE IF EXISTS Products");
            st.execute("CREATE TABLE Products (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    +"st_id INT NOT NULL, pg_id INT NOT NULL, product_name VARCHAR(20) NOT NULL, "
                    +"price DECIMAL(10,2) NOT NULL, status VARCHAR(10) NOT NULL)");
        } finally {
            st.close();
        }
    }
}
