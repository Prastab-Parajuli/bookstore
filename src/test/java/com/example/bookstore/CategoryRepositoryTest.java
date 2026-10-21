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
public class CategoryRepositoryTest {

    @Autowired
    private BookRepository repository;

    @Autowired
    private CategoryRepository crepository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        crepository.deleteAll();

        crepository.save(new Category("Fiction"));
        crepository.save(new Category("Fantasy"));
    }

    // Search

    @Test
    public void findByNameShouldReturnCategory() {
        List<Category> categories = crepository.findByName("Fantasy");

        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo("Fantasy");
    }

    @Test
    public void findByNameContainingShouldIgnoreCase() {
        List<Category> categories = crepository.findByNameContainingIgnoreCase("FIC");

        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo("Fiction");
    }

    @Test
    public void findByNameShouldReturnEmptyListWhenNotFound() {
        assertThat(crepository.findByName("Poetry")).isEmpty();
    }

    // Create

    @Test
    public void createNewCategory() {
        Category category = new Category("Mystery");
        crepository.save(category);

        assertThat(category.getId()).isNotNull();
        assertThat(crepository.count()).isEqualTo(3);
    }

    // Delete

    @Test
    public void deleteCategory() {
        Category category = crepository.findByName("Fiction").get(0);

        crepository.deleteById(category.getId());

        assertThat(crepository.findByName("Fiction")).isEmpty();
        assertThat(crepository.count()).isEqualTo(1);
    }
}