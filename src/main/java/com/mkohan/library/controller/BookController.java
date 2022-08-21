package com.mkohan.library.controller;

import com.mkohan.library.dto.BookDto;
import com.mkohan.library.entity.Author;
import com.mkohan.library.entity.Book;
import com.mkohan.library.service.AuthorService;
import com.mkohan.library.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/books")
@AllArgsConstructor
public class BookController {

    private final AuthorService authorService;
    private final BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{bookId}")
    public Book getBookById(@PathVariable long bookId) {
        return bookService.findById(bookId);
    }

    @PostMapping
    public Book createBook(@RequestBody @Valid BookDto bookDto) {
        return bookService.create(bookDto.getTitle(), bookDto.getAuthorId());
    }

    @PutMapping("/{bookId}")
    public void updateBook(@PathVariable long bookId, @RequestBody @Valid BookDto bookDto) {
        Author author = authorService.findById(bookDto.getAuthorId());
        Book currentBook = bookService.findById(bookId);

        currentBook.setAuthor(author);
        currentBook.setTitle(bookDto.getTitle());

        bookService.save(currentBook);
    }

    @DeleteMapping("/{bookId}")
    public void deleteBook(@PathVariable long bookId) {
        bookService.deleteById(bookId);
    }
}
