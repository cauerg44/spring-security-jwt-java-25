package com.security.test.api.dto.request;

import jakarta.validation.constraints.Size;

public record BookRequestUpdateDTO(
        @Size(min = 8, max = 255, message = "Name must be between 1 and 255 characters")
        String name,
        @Size(min = 8, max = 255, message = "Author must be between 1 and 255 characters")
        String author
) {
}