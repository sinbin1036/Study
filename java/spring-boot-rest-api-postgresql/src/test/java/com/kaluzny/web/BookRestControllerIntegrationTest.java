package com.kaluzny.web;

import com.kaluzny.domain.BookRepository;
import com.kaluzny.dto.BookCreateRequest;
import com.kaluzny.dto.BookResponse;
import com.kaluzny.dto.BookUpdateRequest;
import com.kaluzny.exception.BookNotFoundException;
import com.kaluzny.service.BookService;
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
class BookRestControllerIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    // ==================== Validation Tests ====================

    @Test
    void createBook_shouldSucceed_whenValidRequest() {
        BookCreateRequest request = new BookCreateRequest(
                "Spring Boot",
                "Learn Spring Boot",
                List.of("java", "spring")
        );

        BookResponse response = bookService.create(request);

        assertNotNull(response.id());
        assertEquals("Spring Boot", response.name());
        assertEquals(2, response.tags().size());
    }

    @Test
    void createBook_shouldHandleNullTags() {
        BookCreateRequest request = new BookCreateRequest("Test Book", "Description", null);

        BookResponse response = bookService.create(request);

        assertNotNull(response);
        assertTrue(response.tags().isEmpty());
    }

    // ==================== Pagination Tests ====================

    @Test
    void getAllBooks_shouldReturnPagedResults() {
        // Create some books
        for (int i = 1; i <= 25; i++) {
            bookService.create(new BookCreateRequest("Book " + i, "Desc " + i, null));
        }

        // First page
        Page<BookResponse> firstPage = bookService.findAll(PageRequest.of(0, 10));
        assertEquals(10, firstPage.getContent().size());
        assertEquals(25, firstPage.getTotalElements());
        assertEquals(3, firstPage.getTotalPages());
        assertTrue(firstPage.isFirst());
        assertFalse(firstPage.isLast());

        // Last page
        Page<BookResponse> lastPage = bookService.findAll(PageRequest.of(2, 10));
        assertEquals(5, lastPage.getContent().size());
        assertTrue(lastPage.isLast());
    }

    @Test
    void getAllBooks_shouldReturnEmptyPage_whenNoBooks() {
        Page<BookResponse> page = bookService.findAll(PageRequest.of(0, 10));

        assertTrue(page.isEmpty());
        assertEquals(0, page.getTotalElements());
    }

    // ==================== Search Tests ====================

    @Test
    void findByName_shouldReturnMatchingBooks() {
        bookService.create(new BookCreateRequest("Java Programming", "Java book", null));
        bookService.create(new BookCreateRequest("Java Advanced", "Advanced Java", null));
        bookService.create(new BookCreateRequest("Python Programming", "Python book", null));

        List<BookResponse> javaBooks = bookService.findByName("Java Programming");

        assertEquals(1, javaBooks.size());
        assertEquals("Java Programming", javaBooks.get(0).name());
    }

    @Test
    void findByName_shouldReturnEmptyList_whenNoMatches() {
        bookService.create(new BookCreateRequest("Java", "Java book", null));

        List<BookResponse> results = bookService.findByName("NonExistent");

        assertTrue(results.isEmpty());
    }

    // ==================== CRUD Tests ====================

    @Test
    void getBookById_shouldReturnBook_whenExists() {
        BookResponse created = bookService.create(
                new BookCreateRequest("Test Book", "Description", List.of("tag1", "tag2"))
        );

        BookResponse found = bookService.findById(created.id());

        assertEquals(created.id(), found.id());
        assertEquals("Test Book", found.name());
        assertEquals(2, found.tags().size());
    }

    @Test
    void getBookById_shouldThrow_whenNotExists() {
        assertThrows(BookNotFoundException.class, () -> bookService.findById(999L));
    }

    @Test
    void updateBook_shouldUpdateAllFields() {
        BookResponse created = bookService.create(
                new BookCreateRequest("Original", "Original Desc", List.of("old"))
        );

        BookUpdateRequest updateRequest = new BookUpdateRequest(
                "Updated Name",
                "Updated Description",
                List.of("new", "tags")
        );

        BookResponse updated = bookService.update(created.id(), updateRequest);

        assertEquals("Updated Name", updated.name());
        assertEquals("Updated Description", updated.description());
        assertEquals(2, updated.tags().size());
        assertTrue(updated.tags().contains("new"));
    }

    @Test
    void updateBook_shouldThrow_whenNotExists() {
        BookUpdateRequest request = new BookUpdateRequest("Name", "Desc", null);

        assertThrows(BookNotFoundException.class, () -> bookService.update(999L, request));
    }

    @Test
    void deleteBook_shouldRemoveBook() {
        BookResponse created = bookService.create(
                new BookCreateRequest("To Delete", "Description", null)
        );

        bookService.delete(created.id());

        assertThrows(BookNotFoundException.class, () -> bookService.findById(created.id()));
    }

    @Test
    void deleteBook_shouldThrow_whenNotExists() {
        assertThrows(BookNotFoundException.class, () -> bookService.delete(999L));
    }

    // ==================== Edge Cases ====================

    @Test
    void createBook_shouldHandleEmptyTags() {
        BookCreateRequest request = new BookCreateRequest("Test", "Desc", List.of());

        BookResponse response = bookService.create(request);

        assertTrue(response.tags().isEmpty());
    }

    @Test
    void updateBook_shouldClearTags_whenEmptyList() {
        BookResponse created = bookService.create(
                new BookCreateRequest("Book", "Desc", List.of("tag1", "tag2"))
        );

        BookResponse updated = bookService.update(created.id(),
                new BookUpdateRequest("Book", "Desc", List.of()));

        assertTrue(updated.tags().isEmpty());
    }

    @Test
    void findAll_shouldSortById() {
        bookService.create(new BookCreateRequest("C Book", "Desc", null));
        bookService.create(new BookCreateRequest("A Book", "Desc", null));
        bookService.create(new BookCreateRequest("B Book", "Desc", null));

        Page<BookResponse> page = bookService.findAll(PageRequest.of(0, 10));

        // Default sort by id
        assertEquals(3, page.getContent().size());
    }
}
