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
    public CommandLineRunner bookCommandLineRunner(
            BookRepository bookRepository,
            CategoryRepository categoryRepository) {

        return args -> {

            Category fiction = new Category("Fiction");
            Category fantasy = new Category("Fantasy");
            Category mystery = new Category("Mystery");

            categoryRepository.save(fiction);
            categoryRepository.save(fantasy);
            categoryRepository.save(mystery);

            bookRepository.save(new Book(
                    "The Hobbit",
                    "J.R.R. Tolkien",
                    1937,
                    "9780261102217",
                    25.00,
                    fantasy
            ));

            bookRepository.save(new Book(
                    "Harry Potter and the Philosopher's Stone",
                    "J.K. Rowling",
                    1997,
                    "9780747532743",
                    20.00,
                    fantasy
            ));

            bookRepository.save(new Book(
                    "1984",
                    "George Orwell",
                    1949,
                    "9780451524935",
                    15.00,
                    fiction
            ));
        };
    }
}
