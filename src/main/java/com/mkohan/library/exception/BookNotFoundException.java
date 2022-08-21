package com.mkohan.library.exception;

public class BookNotFoundException extends EntityNotFoundException {

    public BookNotFoundException() {
        super("Book not found");
    }
}
