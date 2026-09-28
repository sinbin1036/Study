package com.kaluzny.dto;

import com.kaluzny.domain.Book;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class BookMapper {

    public Book toEntity(BookCreateRequest request) {
        return Book.builder()
                .name(request.name())
                .description(request.description())
                .tags(request.tags() != null ? new ArrayList<>(request.tags()) : new ArrayList<>())
                .build();
    }

    public void updateEntity(Book entity, BookUpdateRequest request) {
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setTags(request.tags() != null ? new ArrayList<>(request.tags()) : new ArrayList<>());
    }

    public BookResponse toResponse(Book entity) {
        return new BookResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getTags() != null ? new ArrayList<>(entity.getTags()) : new ArrayList<>()
        );
    }
}
