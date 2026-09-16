package com.example.bookstore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner bookCommandLineRunner(BookRepository repository) {
        return args -> {
            repository.save(new Book(
                    "The Hobbit",
                    "J.R.R. Tolkien",
                    1937,
                    "9780261102217",
                    25.00
            ));

            repository.save(new Book(
                    "Harry Potter and the Philosopher's Stone",
                    "J.K. Rowling",
                    1997,
                    "9780747532743",
                    20.00
            ));

            repository.save(new Book(
                    "1984",
                    "George Orwell",
                    1949,
                    "9780451524935",
                    15.00
            ));
        };
    }
}