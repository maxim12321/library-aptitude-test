package com.mkohan.library.service.impl;

import com.mkohan.library.entity.Author;
import com.mkohan.library.entity.Book;
import com.mkohan.library.exception.BookNotFoundException;
import com.mkohan.library.repository.BookRepository;
import com.mkohan.library.service.AuthorService;
import com.mkohan.library.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final AuthorService authorService;

    private final BookRepository bookRepository;

    @Override
    public Book create(String title, Long authorId) {
        Author author = authorService.findById(authorId);
        Book book = new Book(title, author);

        bookRepository.save(book);
        return book;
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Book findById(long id) {
        return bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);
    }

    @Override
    public void save(Book author) {
        bookRepository.save(author);
    }

    @Override
    public void deleteById(long id) {
        Book book = findById(id);
        bookRepository.delete(book);
    }
}
