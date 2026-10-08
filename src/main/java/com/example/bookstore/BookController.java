package com.example.bookstore;

import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BookController {

    private final BookRepository repository;
    private final CategoryRepository categoryRepository;

    public BookController(BookRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    // REST: return all books as JSON
    @GetMapping("/books")
    @ResponseBody
    public Iterable<Book> getBooksRest() {
        return repository.findAll();
    }

    // REST: return one book by id as JSON
    @GetMapping("/books/{id}")
    @ResponseBody
    public Optional<Book> findBookRest(@PathVariable("id") Long id) {
        return repository.findById(id);
    }

    @GetMapping("/booklist")
    public String bookList(Model model) {
        model.addAttribute("books", repository.findAll());
        return "booklist";
    }

    @GetMapping("/addbook")
    public String addBook(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryRepository.findAll());
        return "addbook";
    }

    @PostMapping("/save")
    public String save(Book book, @RequestParam("category") Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElse(null);
        book.setCategory(category);

        repository.save(book);

        return "redirect:/booklist";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBook(@PathVariable("id") Long id) {
        repository.deleteById(id);
        return "redirect:/booklist";
    }

    @GetMapping("/edit/{id}")
    public String editBook(@PathVariable("id") Long id, Model model) {
        Book book = repository.findById(id).orElse(null);

        model.addAttribute("book", book);
        model.addAttribute("categories", categoryRepository.findAll());

        return "editbook";
    }

    @PostMapping("/update")
    public String updateBook(Book book, @RequestParam("category") Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElse(null);
        book.setCategory(category);

        repository.save(book);

        return "redirect:/booklist";
    }
}