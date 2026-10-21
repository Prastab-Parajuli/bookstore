package com.example.bookstore;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional 
public class BookRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private CategoryRepository crepository;

    private Category fantasy;
    private Category fiction;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        crepository.deleteAll();

        fantasy = crepository.save(new Category("Fantasy"));
        fiction = crepository.save(new Category("Fiction"));
        repository.save(new Book("The Hobbit", "J.R.R. Tolkien", 1937, "9780261102217", 25.00, fantasy));
        repository.save(new Book("1984", "George Orwell", 1949, "9780451524935", 15.00, fiction));
    }

    // Search

    @Test
    public void findByTitleShouldReturnAuthor() {
        List<Book> books = repository.findByTitle("The Hobbit");

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getAuthor()).isEqualTo("J.R.R. Tolkien");
    }

    @Test
    public void findByAuthorShouldReturnBook() {
        List<Book> books = repository.findByAuthor("George Orwell");

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("1984");
    }

    @Test
    public void findByTitleContainingShouldIgnoreCase() {
        List<Book> books = repository.findByTitleContainingIgnoreCase("hobb");

        assertThat(books).hasSize(1);
    }

    @Test
    public void findByCategoryNameShouldReturnBooksOfCategory() {
        List<Book> books = repository.findByCategoryName("Fantasy");

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("The Hobbit");
    }

    @Test
    public void findByTitleShouldReturnEmptyListWhenNotFound() {
        assertThat(repository.findByTitle("No such book")).isEmpty();
    }

    // Create

    @Test
    public void createNewBook() {
        Book book = new Book("Dune", "Frank Herbert", 1965, "9780441013593", 18.50, fiction);
        repository.save(book);

        assertThat(book.getId()).isNotNull();
        assertThat(repository.count()).isEqualTo(3);
    }

    // Delete

    @Test
    public void deleteBook() {
        Book book = repository.findByTitle("1984").get(0);

        repository.deleteById(book.getId());

        assertThat(repository.findByTitle("1984")).isEmpty();
        assertThat(repository.count()).isEqualTo(1);
    }
}