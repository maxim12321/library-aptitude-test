package com.mkohan.library.service.impl;

import com.mkohan.library.entity.Author;
import com.mkohan.library.exception.AuthorNotFoundException;
import com.mkohan.library.repository.AuthorRepository;
import com.mkohan.library.service.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public Author create(String name) {
        Author author = new Author(name);
        authorRepository.save(author);
        return author;
    }

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Author findById(long id) {
        return authorRepository.findById(id)
                .orElseThrow(AuthorNotFoundException::new);
    }

    @Override
    public void save(Author author) {
        authorRepository.save(author);
    }

    @Override
    public void deleteById(long id) {
        Author author = findById(id);
        authorRepository.delete(author);
    }
}
