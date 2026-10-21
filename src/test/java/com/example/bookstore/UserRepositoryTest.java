package com.example.bookstore;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository urepository;

    @BeforeEach
    public void setUp() {
        urepository.deleteAll();

        urepository.save(new AppUser("user", "hash1", "user@example.com", "USER"));
        urepository.save(new AppUser("admin", "hash2", "admin@example.com", "ADMIN"));
    }

    // Search

    @Test
    public void findByUsernameShouldReturnUser() {
        AppUser user = urepository.findByUsername("admin");

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("admin@example.com");
        assertThat(user.getRole()).isEqualTo("ADMIN");
    }

    @Test
    public void findByUsernameShouldReturnNullWhenNotFound() {
        assertThat(urepository.findByUsername("ghost")).isNull();
    }

    // Create

    @Test
    public void createNewUser() {
        AppUser user = new AppUser("alice", "hash3", "alice@example.com", "USER");
        urepository.save(user);

        assertThat(user.getId()).isNotNull();
        assertThat(urepository.count()).isEqualTo(3);
    }

    // Delete

    @Test
    public void deleteUser() {
        AppUser user = urepository.findByUsername("user");

        urepository.deleteById(user.getId());

        assertThat(urepository.findByUsername("user")).isNull();
        assertThat(urepository.count()).isEqualTo(1);
    }
}