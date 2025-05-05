package com.examly.springapp.dao;

import java.util.List;

import com.examly.springapp.exception.LowStockException;
import com.examly.springapp.model.Product;

public interface ProductInMemoryDAO {
    Product createProduct(Product product) throws LowStockException;
    Product getProductById(int id) throws LowStockException;
    List<Product> updateProductByCategory(String category, double newPrice, int newStockQuantity) throws LowStockException;
    List<Product> deleteProductByPrice(double priceThreshold);
    List<Product> viewProductDetailsByCategory(String category) ;
}
