package com.security.test.api.service;

import com.security.test.api.dto.request.BookRequestSaveDTO;
import com.security.test.api.dto.request.BookRequestUpdateDTO;
import com.security.test.api.dto.response.BookResponseDTO;
import com.security.test.api.entity.Book;
import com.security.test.api.exception.DatabaseException;
import com.security.test.api.repository.BookRepository;
import com.security.test.api.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<BookResponseDTO> findAll() {
        List<Book> books = repository.findAll();

        return books.stream()
                .map(book -> new BookResponseDTO(book.getId(), book.getName(), book.getAuthor()))
                .toList();
    }

    @Transactional(readOnly = true)
    public BookResponseDTO findById(Long id) {
        Book book = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found."));
        return new BookResponseDTO(book.getId(), book.getName(), book.getAuthor());
    }

    @Transactional
    public BookResponseDTO insert(BookRequestSaveDTO request) {
        Book entity = new Book();
        dtoToEntity(entity, request);
        entity = repository.save(entity);
        return new BookResponseDTO(entity.getId(), entity.getName(), entity.getAuthor());
    }

    @Transactional
    public BookResponseDTO update(Long id, BookRequestUpdateDTO request) {
        try {
            Book entity = repository.getReferenceById(id);
            updateEntityFromDto(entity, request);
            entity = repository.save(entity);
            return new BookResponseDTO(entity.getId(), entity.getName(), entity.getAuthor());
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Resource not found.");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found.");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity reference fail");
        }
    }

    private void dtoToEntity(Book entity, BookRequestSaveDTO request) {
        entity.setName(request.name());
        entity.setAuthor(request.author());
    }

    private void updateEntityFromDto(Book entity, BookRequestUpdateDTO request) {
        if (request.name() != null && !request.name().isBlank()) {
            entity.setName(request.name());
        }
        if (request.author() != null && !request.author().isBlank()) {
            entity.setAuthor(request.author());
        }
    }
}