package com.examly.springapp.service;

import java.util.List;

import com.examly.springapp.exception.LowStockException;
import com.examly.springapp.model.Product;

public interface ProductInMemoryService {
    Product createProduct(Product product) throws LowStockException;

    Product getProductById(int productId) throws LowStockException;

    List<Product> updateProductByCategory(String category, double newPrice, int newStockQuantity) throws LowStockException;

    List<Product> deleteProductByPrice(double priceThreshold);

    List<Product> viewProductDetailsByCategory(String category);
}
 