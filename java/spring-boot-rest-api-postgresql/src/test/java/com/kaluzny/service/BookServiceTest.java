package com.kaluzny.service;

import com.kaluzny.domain.Book;
import com.kaluzny.domain.BookRepository;
import com.kaluzny.dto.BookCreateRequest;
import com.kaluzny.dto.BookMapper;
import com.kaluzny.dto.BookResponse;
import com.kaluzny.dto.BookUpdateRequest;
import com.kaluzny.exception.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

    @Spy
    private BookMapper mapper = new BookMapper();

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .name("Test Book")
                .description("Test Description")
                .tags(List.of("test"))
                .build();
    }

    @Test
    void create_shouldSaveAndReturnBook() {
        BookCreateRequest request = new BookCreateRequest("New Book", "Description", List.of("tag1"));
        when(repository.save(any(Book.class))).thenAnswer(invocation -> {
            Book saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        BookResponse response = bookService.create(request);

        assertNotNull(response);
        assertEquals("New Book", response.name());
        verify(repository).save(any(Book.class));
    }

    @Test
    void findAll_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(book), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);

        Page<BookResponse> result = bookService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).name());
    }

    @Test
    void findById_shouldReturnBook_whenExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse response = bookService.findById(1L);

        assertEquals("Test Book", response.name());
        assertEquals("Test Description", response.description());
    }

    @Test
    void findById_shouldThrowException_whenNotExists() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.findById(999L));
    }

    @Test
    void findByName_shouldReturnMatchingBooks() {
        when(repository.findByName("Test")).thenReturn(List.of(book));

        List<BookResponse> results = bookService.findByName("Test");

        assertEquals(1, results.size());
        assertEquals("Test Book", results.get(0).name());
    }

    @Test
    void findByName_shouldReturnEmptyList_whenNoMatches() {
        when(repository.findByName("NonExistent")).thenReturn(List.of());

        List<BookResponse> results = bookService.findByName("NonExistent");

        assertTrue(results.isEmpty());
    }

    @Test
    void update_shouldUpdateAndReturnBook() {
        BookUpdateRequest request = new BookUpdateRequest("Updated Name", "Updated Desc", List.of("updated"));
        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(repository.save(any(Book.class))).thenReturn(book);

        BookResponse response = bookService.update(1L, request);

        assertNotNull(response);
        verify(repository).save(any(Book.class));
    }

    @Test
    void update_shouldThrowException_whenNotExists() {
        BookUpdateRequest request = new BookUpdateRequest("Name", "Desc", null);
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.update(999L, request));
        verify(repository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteBook_whenExists() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> bookService.delete(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenNotExists() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.delete(999L));
        verify(repository, never()).deleteById(any());
    }
}
