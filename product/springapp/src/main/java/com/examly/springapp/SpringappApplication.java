package com.examly.springapp;

import java.util.List;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.examly.springapp.exception.LowStockException;
import com.examly.springapp.model.Product;
import com.examly.springapp.service.ProductService;
import com.examly.springapp.service.impl.ProductServiceImpl;
 
@SpringBootApplication
public class SpringappApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringappApplication.class, args);
        
        Scanner scanner = new Scanner(System.in);
        ProductService productService = new ProductServiceImpl();
        
        int choice = 0;
        do {
            System.out.println("1. Add a new Product");
            System.out.println("2. Get Product by ID");
            System.out.println("3. Update Products by Category");
            System.out.println("4. Delete Products by Price");
            System.out.println("5. View Products by Category");
            System.out.println("6. Exit");
            
            try {
                choice = Integer.parseInt(scanner.nextLine());
                
                switch(choice){
                    case 1: 
                        try {
                            System.out.println("Enter product details:");
                            int productId = Integer.parseInt(scanner.nextLine());
                            String name = scanner.nextLine();
                            String category = scanner.nextLine();
                            double price = Double.parseDouble(scanner.nextLine());
                            int stock = Integer.parseInt(scanner.nextLine());
                            boolean verified = Boolean.parseBoolean(scanner.nextLine());
                            
                            Product product = new Product(productId, name, category, price, stock, verified);
                            productService.createProduct(product);
                            System.out.println("Product added successfully!");
                        } catch (LowStockException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 2:
                        System.out.println("Enter product ID:");
                        int productId = Integer.parseInt(scanner.nextLine());
                        try {
                            Product product = productService.getProductById(productId);
                            if (product != null) {
                                displayProductDetails(product);
                            } else {
                                System.out.println("Product not found!");
                            }
                        } catch (LowStockException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 3:
                        System.out.println("Enter category, new price, and new stock quantity:");
                        String category = scanner.nextLine();
                        double newPrice = Double.parseDouble(scanner.nextLine());
                        int newStockQuantity = Integer.parseInt(scanner.nextLine());
                        try {
                            List<Product> updatedProducts = productService.updateProductByCategory(category, newPrice, newStockQuantity);
                            System.out.println("Updated Products:");
                            for (Product p : updatedProducts) {
                                displayProductDetails(p);
                            }
                        } catch (LowStockException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 4:
                        System.out.println("Enter price threshold:");
                        double priceThreshold = Double.parseDouble(scanner.nextLine());
                        try {
                            List<Product> deletedProducts = productService.deleteProductByPrice(priceThreshold);
                            System.out.println("Deleted Products:");
                            for (Product p : deletedProducts) {
                                displayProductDetails(p);
                            }
                        } catch (LowStockException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 5:
                        System.out.println("Enter category:");
                        String categoryForView = scanner.nextLine();
                        try {
                            List<Product> products = productService.viewProductDetailsByCategory(categoryForView);
                            System.out.println("Products in category " + categoryForView + ":");
                            for (Product p : products) {
                                displayProductDetails(p);
                            }
                        } catch (LowStockException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 6:
                        System.out.println("Program Exited!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        } while (choice != 6);
    }

    // Method to display product details
    public static void displayProductDetails(Product product) {
        System.out.printf("ProductId: %d, Name: %s, Category: %s, Price: %.2f, Stock: %d, Verified: %b\n",
                product.getProductId(), product.getName(), product.getCategory(), product.getPrice(), product.getStock(), product.isVerified());
    }
}
 