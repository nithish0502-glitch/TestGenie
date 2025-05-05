package com.examly.springapp.dao.impl;

import java.sql.*;
import java.util.*;
import com.examly.springapp.config.JdbcUtils;
import com.examly.springapp.dao.ProductDAO;
import com.examly.springapp.exception.LowStockException;
import com.examly.springapp.model.Product;

public class ProductDAOImpl implements ProductDAO {
 
    @Override
    public void createProduct(Product product) throws LowStockException {
        if (product.getStock() < 10) {
            throw new LowStockException("Stock is low for the product");
        }
 
        try (Connection con = JdbcUtils.getConnection()) {
            String query = "INSERT INTO products(productId, name, category, price, stock, verified) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ptmt = con.prepareStatement(query);
            ptmt.setInt(1, product.getProductId());
            ptmt.setString(2, product.getName());
            ptmt.setString(3, product.getCategory());
            ptmt.setDouble(4, product.getPrice());
            ptmt.setInt(5, product.getStock());
            ptmt.setBoolean(6, product.isVerified());
            ptmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Product getProductById(int productId) throws LowStockException {
        try (Connection con = JdbcUtils.getConnection()) {
            String query = "SELECT * FROM products WHERE productId = ?";
            PreparedStatement ptmt = con.prepareStatement(query);
            ptmt.setInt(1, productId);
            ResultSet rs = ptmt.executeQuery();

            if (rs.next()) {
                int stock = rs.getInt("stock");
                if (stock < 10) {
                    throw new LowStockException("Stock is low for the product");
                }

                return new Product(
                    rs.getInt("productId"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    stock,
                    rs.getBoolean("verified")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Product> updateProductByCategory(String category, double newPrice, int newStockQuantity) throws LowStockException {
        if (newStockQuantity < 10) {
            throw new LowStockException("Stock is low for the product");
        }

        List<Product> updatedProducts = new ArrayList<>();
        try (Connection con = JdbcUtils.getConnection()) {
            String selectQuery = "SELECT * FROM products WHERE category = ?";
            PreparedStatement selectStmt = con.prepareStatement(selectQuery);
            selectStmt.setString(1, category);
            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("productId"),
                    rs.getString("name"),
                    category,
                    newPrice,
                    newStockQuantity,
                    rs.getBoolean("verified")
                );

                updatedProducts.add(product);
            }

            String updateQuery = "UPDATE products SET price = ?, stock = ? WHERE category = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateQuery);
            updateStmt.setDouble(1, newPrice);
            updateStmt.setInt(2, newStockQuantity);
            updateStmt.setString(3, category);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return updatedProducts;
    }

    @Override
    public List<Product> deleteProductByPrice(double priceThreshold) throws LowStockException {
        List<Product> deletedProducts = new ArrayList<>();
        try (Connection con = JdbcUtils.getConnection()) {
            String selectQuery = "SELECT * FROM products WHERE price < ?";
            PreparedStatement selectStmt = con.prepareStatement(selectQuery);
            selectStmt.setDouble(1, priceThreshold);
            ResultSet rs = selectStmt.executeQuery();
 
            while (rs.next()) {
                deletedProducts.add(new Product(
                    rs.getInt("productId"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getBoolean("verified")
                ));
            }
  
            String deleteQuery = "DELETE FROM products WHERE price < ?";
            PreparedStatement deleteStmt = con.prepareStatement(deleteQuery);
            deleteStmt.setDouble(1, priceThreshold);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return deletedProducts;
    }

    @Override
    public List<Product> viewProductDetailsByCategory(String category) throws LowStockException {
        List<Product> products = new ArrayList<>();
        try (Connection con = JdbcUtils.getConnection()) {
            String query = "SELECT * FROM products WHERE category = ?";
            PreparedStatement ptmt = con.prepareStatement(query);
            ptmt.setString(1, category);
            ResultSet rs = ptmt.executeQuery();

            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("productId"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getBoolean("verified")
                ));
            }

            // Sort alphabetically
            for (int i = 0; i < products.size() - 1; i++) {
                for (int j = 0; j < products.size() - i - 1; j++) {
                    String name1 = products.get(j).getName().toLowerCase();
                    String name2 = products.get(j + 1).getName().toLowerCase();
                    if (name1.compareTo(name2) > 0) {
                        Product temp = products.get(j);
                        products.set(j, products.get(j + 1));
                        products.set(j + 1, temp);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return products;
    }
}
