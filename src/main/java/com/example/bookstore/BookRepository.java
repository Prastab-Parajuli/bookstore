package com.example.bookstore;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends CrudRepository<Book, Long> {

    List<Book> findByTitle(@Param("title") String title);

    List<Book> findByAuthor(@Param("author") String author);

    List<Book> findByTitleContainingIgnoreCase(@Param("title") String title);

    List<Book> findByCategoryName(@Param("name") String name);
}