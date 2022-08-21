package com.mkohan.library.controller;

import com.mkohan.library.dto.AuthorDto;
import com.mkohan.library.entity.Author;
import com.mkohan.library.service.AuthorService;
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
@RequestMapping("/authors")
@AllArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public List<Author> getAllAuthors() {
        return authorService.findAll();
    }

    @GetMapping("/{authorId}")
    public Author getAuthorById(@PathVariable long authorId) {
        return authorService.findById(authorId);
    }

    @PostMapping
    public Author createAuthor(@RequestBody @Valid AuthorDto authorDto) {
        return authorService.create(authorDto.getName());
    }

    @PutMapping("/{authorId}")
    public void updateAuthor(@PathVariable long authorId, @RequestBody @Valid AuthorDto authorDto) {
        Author currentAuthor = authorService.findById(authorId);
        currentAuthor.setName(authorDto.getName());

        authorService.save(currentAuthor);
    }

    @DeleteMapping("/{authorId}")
    public void deleteAuthor(@PathVariable long authorId) {
        authorService.deleteById(authorId);
    }
}
