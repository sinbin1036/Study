package com.kaluzny.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request body for creating a new book")
public record BookCreateRequest(
        @Schema(description = "Book title", example = "Java Programming", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Name is required")
        @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
        String name,

        @Schema(description = "Book description", example = "Complete guide to Java programming")
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @Schema(description = "List of tags", example = "[\"java\", \"programming\", \"backend\"]")
        List<String> tags
) {
}
