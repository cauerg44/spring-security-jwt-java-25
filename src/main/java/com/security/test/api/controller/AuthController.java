package com.security.test.api.controller;

import com.security.test.api.dto.request.LoginRequestDTO;
import com.security.test.api.dto.request.RegisterRequestDTO;
import com.security.test.api.dto.response.RegisterResponseDTO;
import com.security.test.api.dto.response.LoginResponseDTO;
import com.security.test.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        var response =  service.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Valid RegisterRequestDTO data) {
        var newUser = service.register(data);
        return ResponseEntity.ok(newUser);
    }
}