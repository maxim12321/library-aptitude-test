package com.mkohan.library.service;

import com.mkohan.library.entity.Author;

import java.util.List;

public interface AuthorService {

    Author create(String name);

    List<Author> findAll();

    Author findById(long id);

    void save(Author author);

    void deleteById(long id);
}
