package com.examly.springapp.model;

import com.examly.springapp.exception.LowStockException;

public class Product {
    int productId;
    String name;
    String category;
    double price;
    int stock;
    boolean verified;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) throws LowStockException {
        if (stock < 10) {
            throw new LowStockException("Stock is low for the product");
        }
        this.stock = stock;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
     
    public Product(int productId, String name, String category, double price, int stock, boolean verified) throws LowStockException {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        setStock(stock); 
        this.verified = verified;
    }

    @Override
    public String toString() {
        return "Product [productId=" + productId + ", name=" + name + ", category=" + category + ", price=" + price
                + ", stock=" + stock + ", verified=" + verified + "]";
    }

    public Product() {
    }
}
