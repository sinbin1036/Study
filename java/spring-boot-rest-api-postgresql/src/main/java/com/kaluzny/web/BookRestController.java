package com.kaluzny.web;

import com.kaluzny.dto.BookCreateRequest;
import com.kaluzny.dto.BookResponse;
import com.kaluzny.dto.BookUpdateRequest;
import com.kaluzny.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/books", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Books", description = "API for managing books")
public class BookRestController {

    private final BookService bookService;

    @Operation(summary = "Create a new book", description = "Creates a new book and returns the created book with ID")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@Valid @RequestBody BookCreateRequest request) {
        return bookService.create(request);
    }

    @Operation(summary = "Get all books", description = "Returns a paginated list of all books")
    @ApiResponse(responseCode = "200", description = "List of books retrieved successfully")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BookResponse> getAllBooks(
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get book by ID", description = "Returns a single book by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookResponse getBookById(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable Long id) {
        return bookService.findById(id);
    }

    @Operation(summary = "Search books by name", description = "Returns all books matching the given name")
    @ApiResponse(responseCode = "200", description = "List of matching books")
    @GetMapping(params = {"name"})
    @ResponseStatus(HttpStatus.OK)
    public List<BookResponse> findBookByName(
            @Parameter(description = "Book name to search", example = "Java")
            @RequestParam(value = "name") String name) {
        return bookService.findByName(name);
    }

    @Operation(summary = "Update a book", description = "Updates an existing book by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookResponse updateBook(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request) {
        return bookService.update(id, request);
    }

    @Operation(summary = "Delete a book", description = "Deletes a book by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable Long id) {
        bookService.delete(id);
    }
}
