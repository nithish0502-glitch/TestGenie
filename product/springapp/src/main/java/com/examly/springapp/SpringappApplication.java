package com.examly.springapp;

import java.util.List;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
import com.examly.springapp.exception.LowPriceException;
import com.examly.springapp.model.Book;
import com.examly.springapp.service.BookService;
import com.examly.springapp.service.impl.BookServiceImpl;

@SpringBootApplication
public class SpringappApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringappApplication.class, args);

        Scanner scanner = new Scanner(System.in);
        BookService bookService = new BookServiceImpl();

        int choice = 0;
        do {
            System.out.println("1. Add a new Book");
            System.out.println("2. Update a Book");
            System.out.println("3. Delete Books by Author");
            System.out.println("4. View All Books Sorted by Title");
            System.out.println("5. View Available Books");
            System.out.println("6. Exit");

            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        try {
                            System.out.println("Enter book details:");
                            int bookId = Integer.parseInt(scanner.nextLine());
                            String title = scanner.nextLine();
                            String author = scanner.nextLine();
                            float price = Float.parseFloat(scanner.nextLine());
                            boolean available = Boolean.parseBoolean(scanner.nextLine());

                            Book book = new Book(bookId, title, author, price, available);
                            bookService.createBook(book);
                            System.out.println("Book added successfully!");
                        } catch (LowPriceException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 2:
                        try {
                            System.out.println("Enter updated book details:");
                            int bookId = Integer.parseInt(scanner.nextLine());
                            String title = scanner.nextLine();
                            String author = scanner.nextLine();
                            float price = Float.parseFloat(scanner.nextLine());
                            boolean available = Boolean.parseBoolean(scanner.nextLine());

                            Book book = new Book(bookId, title, author, price, available);
                            bookService.updateBook(book);
                            System.out.println("Book updated successfully!");
                        } catch (LowPriceException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 3:
                        System.out.println("Enter author and delete limit:");
                        String author = scanner.nextLine();
                        int limit = Integer.parseInt(scanner.nextLine());
                        bookService.deleteBooksByAuthor(author, limit);
                        System.out.println("Books deleted successfully!");
                        break;
                    case 4:
                        try {
                            List<Book> books = bookService.getAllBooksByTitle();
                            System.out.println("Books sorted by title:");
                            for (Book b : books) {
                                displayBookDetails(b);
                            }
                        } catch (LowPriceException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 5:
                        try {
                            List<Book> availableBooks = bookService.getAvailableBooks();
                            System.out.println("Available Books:");
                            for (Book b : availableBooks) {
                                displayBookDetails(b);
                            }
                        } catch (LowPriceException e) {
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

    public static void displayBookDetails(Book book) {
        System.out.printf("BookId: %d, Title: %s, Author: %s, Price: %.2f, Available: %b\n",
                book.getBookId(), book.getTitle(), book.getAuthor(), book.getPrice(), book.isAvailable());
    }
}
