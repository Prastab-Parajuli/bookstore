package com.example.bookstore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookRestTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private BookRepository repository;

    @Autowired
    private CategoryRepository crepository;

    private MockMvc mockMvc;
    private Category fantasy;
    private Category fiction;
    private Book hobbit;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        repository.deleteAll();
        crepository.deleteAll();

        fantasy = crepository.save(new Category("Fantasy"));
        fiction = crepository.save(new Category("Fiction"));
        hobbit = repository.save(new Book("The Hobbit", "J.R.R. Tolkien", 1937, "9780261102217", 25.00, fantasy));
        repository.save(new Book("1984", "George Orwell", 1949, "9780451524935", 15.00, fiction));
    }

    // Security

    @Test
    public void notLoggedInUserIsRedirectedToLogin() throws Exception {
        this.mockMvc.perform(get("/books"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    // GET (your own REST endpoints)

    @Test
    @WithMockUser(roles = "USER")
    public void getAllBooksShouldReturnJson() throws Exception {
        this.mockMvc.perform(get("/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(containsString("The Hobbit")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getBookByIdShouldReturnBook() throws Exception {
        this.mockMvc.perform(get("/books/" + hobbit.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("The Hobbit")))
                .andExpect(jsonPath("$.author", is("J.R.R. Tolkien")))
                .andExpect(jsonPath("$.category.name", is("Fantasy")));
    }

    // Spring Data REST (/api)

    @Test
    @WithMockUser(roles = "USER")
    public void apiGetBooksShouldReturnAllBooks() throws Exception {
        this.mockMvc.perform(get("/api/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.books", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void apiGetUnknownBookShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/api/books/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void apiCreateBookShouldReturnCreated() throws Exception {
        String json = """
                {
                  "title": "Dune",
                  "author": "Frank Herbert",
                  "publicationYear": 1965,
                  "isbn": "9780441013593",
                  "price": 18.5,
                  "category": "http://localhost/api/categories/%d"
                }
                """.formatted(fiction.getId());

        this.mockMvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated());

        assertThat(repository.findByTitle("Dune")).hasSize(1);
        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void apiDeleteBookShouldReturnNoContent() throws Exception {
        this.mockMvc.perform(delete("/api/books/" + hobbit.getId()).with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        assertThat(repository.existsById(hobbit.getId())).isFalse();
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void apiSearchByAuthorShouldReturnBook() throws Exception {
        this.mockMvc.perform(get("/api/books/search/findByAuthor").param("author", "George Orwell"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.books", hasSize(1)))
                .andExpect(jsonPath("$._embedded.books[0].title", is("1984")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void apiSearchByTitleShouldReturnBook() throws Exception {
        this.mockMvc.perform(get("/api/books/search/findByTitle").param("title", "The Hobbit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.books", hasSize(1)))
                .andExpect(jsonPath("$._embedded.books[0].author", is("J.R.R. Tolkien")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void apiGetCategoriesShouldReturnAllCategories() throws Exception {
        this.mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.categories", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void apiCreateCategoryShouldReturnCreated() throws Exception {
        this.mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Mystery\"}"))
                .andExpect(status().isCreated());

        assertThat(crepository.findByName("Mystery")).hasSize(1);
    }

    // Delete (ADMIN only)

    @Test
    @WithMockUser(roles = "USER")
    public void normalUserCannotDeleteBook() throws Exception {
        this.mockMvc.perform(get("/delete/" + hobbit.getId()))
                .andExpect(status().isForbidden());

        assertThat(repository.existsById(hobbit.getId())).isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminCanDeleteBook() throws Exception {
        this.mockMvc.perform(get("/delete/" + hobbit.getId()))
                .andExpect(status().is3xxRedirection());

        assertThat(repository.existsById(hobbit.getId())).isFalse();
    }
}