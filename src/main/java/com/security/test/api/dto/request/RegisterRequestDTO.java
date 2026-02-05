package com.security.test.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank String login,
        @NotBlank String password
) {}