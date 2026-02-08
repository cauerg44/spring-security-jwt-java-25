package com.security.test.api.controller;

import com.security.test.api.dto.request.BookRequestSaveDTO;
import com.security.test.api.dto.request.BookRequestUpdateDTO;
import com.security.test.api.dto.response.BookResponseDTO;
import com.security.test.api.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/books")
@Tag(name = "Books", description = "Book API for tests")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @Operation(
            description = "Endpoint for list all book",
            summary = "List all books",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Not found", responseCode = "404")
            }
    )
    @PreAuthorize("permitAll()")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookResponseDTO>> allBooks() {
        var books = service.findAll();
        return ResponseEntity.ok(books);
    }

    @Operation(
            description = "Endpoint for get book by id",
            summary = "Get book by id",
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200"),
                    @ApiResponse(description = "Not found", responseCode = "404"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401")
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BookResponseDTO> findBook(@PathVariable Long id) {
        var book = service.findById(id);
        return ResponseEntity.ok(book);
    }

    @Operation(
            description = "Endpoint for create a new book",
            summary = "Create a new book",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookResponseDTO> saveBook(@RequestBody @Valid BookRequestSaveDTO request) {
        var newBook = service.insert(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newBook.id()).toUri();
        return ResponseEntity.created(uri).body(newBook);
    }

    @Operation(
            description = "Endpoint for update a book",
            summary = "Update a book",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201"),
                    @ApiResponse(description = "Bad Request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Unprocessable Entity", responseCode = "422")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id, @RequestBody @Valid BookRequestUpdateDTO request) {
        var bookUpdated = service.update(id, request);
        return ResponseEntity.ok(bookUpdated);
    }

    @Operation(
            description = "Endpoint for delete book",
            summary = "Delete a book",
            responses = {
                    @ApiResponse(description = "Sucess", responseCode = "204"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Unauthorized", responseCode = "401"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not found", responseCode = "404"),
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteBook(@Valid @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}