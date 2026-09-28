package com.kaluzny.web;

import com.kaluzny.domain.BookRepository;
import com.kaluzny.dto.BookCreateRequest;
import com.kaluzny.dto.BookResponse;
import com.kaluzny.dto.BookUpdateRequest;
import com.kaluzny.exception.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookRestControllerTest {

    @Autowired
    private BookRestController controller;

    @Autowired
    private BookRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateBook() {
        BookCreateRequest request = new BookCreateRequest(
                "Java Programming",
                "Learn Java",
                List.of("java", "programming")
        );

        BookResponse response = controller.createBook(request);

        assertNotNull(response.id());
        assertEquals("Java Programming", response.name());
        assertEquals("Learn Java", response.description());
        assertEquals(2, response.tags().size());
    }

    @Test
    void shouldGetAllBooksWithPagination() {
        controller.createBook(new BookCreateRequest("Book 1", "Desc 1", null));
        controller.createBook(new BookCreateRequest("Book 2", "Desc 2", null));
        controller.createBook(new BookCreateRequest("Book 3", "Desc 3", null));

        Page<BookResponse> page = controller.getAllBooks(PageRequest.of(0, 2));

        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void shouldGetBookById() {
        BookResponse created = controller.createBook(
                new BookCreateRequest("Kotlin", "Kotlin Guide", null)
        );

        BookResponse found = controller.getBookById(created.id());

        assertEquals("Kotlin", found.name());
        assertEquals("Kotlin Guide", found.description());
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        assertThrows(BookNotFoundException.class, () ->
                controller.getBookById(999L)
        );
    }

    @Test
    void shouldFindBooksByName() {
        controller.createBook(new BookCreateRequest("Java", "Java book", null));
        controller.createBook(new BookCreateRequest("Java", "Another Java book", null));
        controller.createBook(new BookCreateRequest("Kotlin", "Kotlin book", null));

        List<BookResponse> results = controller.findBookByName("Java");

        assertEquals(2, results.size());
    }

    @Test
    void shouldUpdateBook() {
        BookResponse created = controller.createBook(
                new BookCreateRequest("Old Name", "Old Description", null)
        );

        BookUpdateRequest updateRequest = new BookUpdateRequest(
                "New Name",
                "New Description",
                List.of("updated")
        );

        BookResponse updated = controller.updateBook(created.id(), updateRequest);

        assertEquals("New Name", updated.name());
        assertEquals("New Description", updated.description());
        assertEquals(1, updated.tags().size());
        assertEquals("updated", updated.tags().get(0));
    }

    @Test
    void shouldDeleteBook() {
        BookResponse created = controller.createBook(
                new BookCreateRequest("To Delete", "Will be deleted", null)
        );

        controller.deleteBook(created.id());

        assertThrows(BookNotFoundException.class, () ->
                controller.getBookById(created.id())
        );
    }

    @Test
    void shouldThrowWhenDeletingNonExistentBook() {
        assertThrows(BookNotFoundException.class, () ->
                controller.deleteBook(999L)
        );
    }
}
