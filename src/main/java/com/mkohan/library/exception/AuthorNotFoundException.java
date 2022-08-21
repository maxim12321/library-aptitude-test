package com.mkohan.library.exception;

public class AuthorNotFoundException extends EntityNotFoundException {

    public AuthorNotFoundException() {
        super("Author not found");
    }
}
