package com.security.test.api.factory;

import com.security.test.api.infra.entity.Book;

public class BookFactory {

    public static Book createBook() {
        return new Book(10L, "Factory design", "Unknown");
    }
}