package com.security.test.api.service;

import com.security.test.api.dto.request.BookRequestSaveDTO;
import com.security.test.api.dto.request.BookRequestUpdateDTO;
import com.security.test.api.dto.response.BookResponseDTO;
import com.security.test.api.infra.entity.Book;
import com.security.test.api.exception.ResourceNotFoundException;
import com.security.test.api.factory.BookFactory;
import com.security.test.api.infra.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTests {

    @InjectMocks
    private BookService service;

    @Mock
    private BookRepository repository;

    private long existingBookId, nonExistingBookId;
    private String bookName;
    private Book book;
    private BookRequestSaveDTO requestSaveDTO;
    private BookRequestUpdateDTO requestUpdateDTO;
    private BookResponseDTO bookResponseDTO;
    private List<Book> list;

    @BeforeEach
    void setUp() throws Exception {
        existingBookId = 1L;
        nonExistingBookId = 2L;

        bookName = "Clean code";

        book = BookFactory.createBook();
        book.setId(existingBookId);

        list = new ArrayList<>();
        list.add(book);

        requestSaveDTO = new BookRequestSaveDTO(book.getName(), book.getAuthor());
        requestUpdateDTO = new BookRequestUpdateDTO(book.getName(), book.getAuthor());
    }

    @Test
    public void findByIdShouldReturnBookResponseDTOWhenIdExists() {

        when(repository.findById(existingBookId)).thenReturn(Optional.of(book));

        BookResponseDTO result = service.findById(existingBookId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.id(), existingBookId);
        Assertions.assertEquals(result.name(), book.getName());
        Assertions.assertEquals(result.author(), book.getAuthor());
    }

    @Test
    public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {

        when(repository.findById(nonExistingBookId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingBookId);
        });
    }

    @Test
    public void findAllShouldReturnBookResponseDTO() {

        when(repository.findAll()).thenReturn(list);

        List<BookResponseDTO> result = service.findAll();

        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(result.get(0).id(), book.getId());
        Assertions.assertEquals(result.get(0).name(), book.getName());
        Assertions.assertEquals(result.get(0).author(), book.getAuthor());
    }

    @Test
    public void insertShouldReturnBookResponseDTO() {

        when(repository.save(any())).thenReturn(book);

        BookResponseDTO response = service.insert(requestSaveDTO);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.id(), book.getId());
    }

    @Test
    public void updateShouldReturnBookResponseDTOWhenIdExists() {

        when(repository.getReferenceById(existingBookId)).thenReturn(book);
        when(repository.save(any())).thenReturn(book);

        BookResponseDTO response = service.update(existingBookId, requestUpdateDTO);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.id(), existingBookId);
        Assertions.assertEquals(response.name(), requestUpdateDTO.name());
    }

    @Test
    public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {

        when(repository.getReferenceById(nonExistingBookId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingBookId, requestUpdateDTO);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        when(repository.existsById(existingBookId)).thenReturn(true);

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingBookId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        when(repository.existsById(nonExistingBookId)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingBookId);
        });
    }
}