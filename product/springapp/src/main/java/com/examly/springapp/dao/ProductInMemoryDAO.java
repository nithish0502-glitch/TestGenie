package com.examly.springapp.dao;

import java.util.List;
import com.examly.springapp.model.Product;

public interface ProductInMemoryDAO {
    Product createProduct(Product product);
    Product getProductById(int id);
    List<Product> updateProductByCategory(String category, double newPrice, int newStockQuantity);
    List<Product> deleteProductByPrice(double priceThreshold);
    List<Product> viewProductDetailsByCategory(String category);
}
