package com.examly.springapp.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.examly.springapp.dao.ProductInMemoryDAO;
import com.examly.springapp.exception.LowStockException;
import com.examly.springapp.model.Product;

public class ProductInMemoryDAOImpl implements ProductInMemoryDAO {

    private List<Product> productDatabase = new ArrayList<>();

    // Adds a new product and returns it
    @Override
    public Product createProduct(Product product) throws LowStockException {
        if (product.getStock() < 10) {
            throw new LowStockException("Stock is low for the product");
        }
        productDatabase.add(product);
        return product;
    }

    // Returns a product by its productId
    @Override
    public Product getProductById(int id) throws LowStockException {
        for (Product product : productDatabase) {
            if (product.getProductId() == id) {
                if (product.getStock() < 10) {
                    throw new LowStockException("Stock is low for the product");
                }
                return product;
            }
        }
        return null;
    }
 
    // Updates all products of a given category, returns the updated products
    @Override
    public List<Product> updateProductByCategory(String category, double newPrice, int newStockQuantity) throws LowStockException {
        if (newStockQuantity < 10) {
            throw new LowStockException("Stock is low for the product");
        }

        List<Product> updatedProducts = new ArrayList<>();
        for (Product product : productDatabase) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                product.setPrice(newPrice);
                product.setStock(newStockQuantity);
                updatedProducts.add(product);
            }
        }
        return updatedProducts;
    }

    // Deletes all products with price below threshold, returns deleted products
    @Override
    public List<Product> deleteProductByPrice(double priceThreshold) {
        List<Product> deletedProducts = new ArrayList<>();
        Iterator<Product> iterator = productDatabase.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (product.getPrice() < priceThreshold) {
                deletedProducts.add(product);
                iterator.remove();
            }
        }
        return deletedProducts;
    }

    // Returns products filtered by category and sorted alphabetically by name
    @Override
    public List<Product> viewProductDetailsByCategory(String category) {
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : productDatabase) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                filteredProducts.add(product);
            }
        }

        // Bubble sort by name (case insensitive)
        for (int i = 0; i < filteredProducts.size() - 1; i++) {
            for (int j = 0; j < filteredProducts.size() - i - 1; j++) {
                String name1 = filteredProducts.get(j).getName().toLowerCase();
                String name2 = filteredProducts.get(j + 1).getName().toLowerCase();
                if (name1.compareTo(name2) > 0) {
                    Product temp = filteredProducts.get(j);
                    filteredProducts.set(j, filteredProducts.get(j + 1));
                    filteredProducts.set(j + 1, temp);
                }
            }
        }

        return filteredProducts;
    }
}
