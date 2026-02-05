package com.security.test.api.controller;

import com.security.test.api.dto.request.BookRequestSaveDTO;
import com.security.test.api.dto.request.BookRequestUpdateDTO;
import com.security.test.api.dto.response.BookResponseDTO;
import com.security.test.api.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/books")
public class BookController {
        
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<BookResponseDTO>> allBooks() {
        var books = service.findAll();
        return ResponseEntity.ok(books);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{id}")
    public ResponseEntity<BookResponseDTO> findBook(@PathVariable Long id) {
        var book = service.findById(id);
        return ResponseEntity.ok(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BookResponseDTO> saveBook(@RequestBody @Valid BookRequestSaveDTO request) {
        var newBook = service.insert(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newBook.id()).toUri();
        return ResponseEntity.created(uri).body(newBook);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id, @RequestBody @Valid BookRequestUpdateDTO request) {
        var bookUpdated = service.update(id, request);
        return ResponseEntity.ok(bookUpdated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}