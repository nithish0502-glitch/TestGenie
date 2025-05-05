package com.examly.springapp.service.impl;

import java.util.List;

import com.examly.springapp.dao.ProductDAO;
import com.examly.springapp.dao.impl.ProductDAOImpl;
import com.examly.springapp.exception.LowStockException;
import com.examly.springapp.model.Product;
import com.examly.springapp.service.ProductService;

public class ProductServiceImpl implements ProductService {

    private ProductDAO productDAO = new ProductDAOImpl();

    @Override
    public void createProduct(Product product) throws LowStockException {
        productDAO.createProduct(product);
    }

    @Override
    public Product getProductById(int productId) throws LowStockException {
        return productDAO.getProductById(productId);
    }

    @Override
    public List<Product> updateProductByCategory(String category, double newPrice, int newStockQuantity) throws LowStockException {
        return productDAO.updateProductByCategory(category, newPrice, newStockQuantity);
    }

    @Override
    public List<Product> deleteProductByPrice(double priceThreshold) throws LowStockException {
        return productDAO.deleteProductByPrice(priceThreshold);
    }

    @Override
    public List<Product> viewProductDetailsByCategory(String category) throws LowStockException {
        return productDAO.viewProductDetailsByCategory(category);
    }
}
 