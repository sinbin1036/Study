package com.kaluzny.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Book response")
public record BookResponse(
        @Schema(description = "Book ID", example = "1")
        Long id,

        @Schema(description = "Book title", example = "Java Programming")
        String name,

        @Schema(description = "Book description", example = "Complete guide to Java programming")
        String description,

        @Schema(description = "List of tags", example = "[\"java\", \"programming\"]")
        List<String> tags
) {
}
