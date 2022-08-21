package com.mkohan.library.service;

import com.mkohan.library.entity.Book;

import java.util.List;

public interface BookService {

    Book create(String name, Long authorId);

    List<Book> findAll();

    Book findById(long id);

    void save(Book author);

    void deleteById(long id);
}
