package com.security.test.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.test.api.dto.request.BookRequestSaveDTO;
import com.security.test.api.dto.request.BookRequestUpdateDTO;
import com.security.test.api.dto.response.BookResponseDTO;
import com.security.test.api.entity.Book;
import com.security.test.api.factory.TokenUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    private Long existingId, nonExistingId;
    private String bookName;

    private Book book;
    private BookResponseDTO bookResponseDTO;
    private BookRequestSaveDTO requestSaveDTO;
    private BookRequestUpdateDTO requestUpdateDTO;

    @BeforeEach
    void setUp() throws Exception {

        clientUsername = "user1@email.com";
        clientPassword = "123456";
        adminUsername = "admin@email.com";
        adminPassword = "123456";

        bookName = "Hey, brother!";

        existingId = 2L;
        nonExistingId = 404L;

        clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
        adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        invalidToken = adminToken + "invalid"; // Simulates a wrong token

        book = new Book(null, bookName, "Unknown Author");
        requestSaveDTO = new BookRequestSaveDTO(book.getName(), book.getAuthor());
        requestUpdateDTO = new BookRequestUpdateDTO(book.getName(), book.getAuthor());
    }

    @Test
    public void findAllShouldReturnListOfBookResponseDTO() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/books")
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.[0].id").value(1L));
        result.andExpect(jsonPath("$.[0].name").value("Clean Code"));
        result.andExpect(jsonPath("$.[0].author").value("Robert C. Martin"));
    }

    @Test
    public void findByIdShouldReturnBookResponseDTOWhenIdExists() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/books/{id}", existingId)
                                .header("Authorization", "Bearer " + clientToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(2L));
        result.andExpect(jsonPath("$.name").value("Effective Java"));
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/books/{id}", nonExistingId)
                                .header("Authorization", "Bearer " + clientToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isNotFound());
    }

    @Test
    public void insertShouldReturnBookResponseDTOCreatedWhenLoggedAsAdmin() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(requestSaveDTO);

        ResultActions result =
                mockMvc.perform(post("/books")
                                .header("Authorization", "Bearer " + adminToken)
                                .content(jsonBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andDo(MockMvcResultHandlers.print());

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").value(11L));
        result.andExpect(jsonPath("$.name").value(bookName));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidBookName() throws Exception {

        book.setName("er");
        requestSaveDTO = new BookRequestSaveDTO(book.getName(), book.getAuthor());

        String jsonBody = objectMapper.writeValueAsString(requestSaveDTO);

        ResultActions result =
                mockMvc.perform(post("/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidAuthorName() throws Exception {

        book.setAuthor("er");
        requestSaveDTO = new BookRequestSaveDTO(book.getName(), book.getAuthor());

        String jsonBody = objectMapper.writeValueAsString(requestSaveDTO);

        ResultActions result =
                mockMvc.perform(post("/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnBookResponseDTOWhenIdExistsAndAdminLogged() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(requestUpdateDTO);

        ResultActions result =
                mockMvc.perform(patch("/books/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(2L));
        result.andExpect(jsonPath("$.name").value(bookName));
        result.andExpect(jsonPath("$.author").value("Unknown Author"));
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndInvalidBookName() throws Exception {

        book.setName("er");
        requestUpdateDTO = new BookRequestUpdateDTO(book.getName(), book.getAuthor());

        String jsonBody = objectMapper.writeValueAsString(requestUpdateDTO);

        ResultActions result =
                mockMvc.perform(patch("/books/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnUnprocessableEntityWhenIdExistsAndAdminLoggedAndInvalidAuthorName() throws Exception {

        book.setAuthor("er");
        requestUpdateDTO = new BookRequestUpdateDTO(book.getName(), book.getAuthor());

        String jsonBody = objectMapper.writeValueAsString(requestUpdateDTO);

        ResultActions result =
                mockMvc.perform(patch("/books/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateShouldReturnForbiddenWhenIdExistsAndUserLogged() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(requestUpdateDTO);

        ResultActions result =
                mockMvc.perform(patch("/books/{id}", existingId)
                        .header("Authorization", "Bearer " + clientToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void updateShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(requestUpdateDTO);

        ResultActions result =
                mockMvc.perform(patch("/books/{id}", existingId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExistsAndAdminLogged() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/books/{id}", existingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/books/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + adminToken)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnForbiddenWhenIdExistsAndUserLogged() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/books/{id}", existingId)
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/books/{id}", existingId)
                        .header("Authorization", "Bearer " + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }
}