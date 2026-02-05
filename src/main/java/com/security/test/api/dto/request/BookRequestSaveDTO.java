package com.security.test.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookRequestSaveDTO(
        @NotBlank(message = "Name is required")
        @Size(min = 8, max = 255, message = "Name must be between 5 and 255 characters")
        String name,
        @NotBlank(message = "Author is required")
        @Size(min = 8, max = 255, message = "Author must be between 5 and 255 characters")
        String author
) {
}