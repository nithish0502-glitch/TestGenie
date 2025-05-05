package com.examly.springapp.service.impl;

import java.util.List;

import com.examly.springapp.dao.ProductInMemoryDAO;
import com.examly.springapp.dao.impl.ProductInMemoryDAOImpl;
import com.examly.springapp.model.Product;
import com.examly.springapp.service.ProductInMemoryService;

public class ProductInMemoryServiceImpl implements ProductInMemoryService {

    private ProductInMemoryDAO productDAO;

    public ProductInMemoryServiceImpl() {
        this.productDAO = new ProductInMemoryDAOImpl(); // using in-memory DAO
    }

    @Override
    public Product createProduct(Product product) {
        return productDAO.createProduct(product);
    }

    @Override
    public Product getProductById(int productId) {
        return productDAO.getProductById(productId);
    }

    @Override
    public List<Product> updateProductByCategory(String category, double newPrice, int newStockQuantity) {
        return productDAO.updateProductByCategory(category, newPrice, newStockQuantity);
    }

    @Override
    public List<Product> deleteProductByPrice(double priceThreshold) {
        return productDAO.deleteProductByPrice(priceThreshold);
    }

    @Override
    public List<Product> viewProductDetailsByCategory(String category) {
        return productDAO.viewProductDetailsByCategory(category);
    }
}
