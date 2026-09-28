package com.kaluzny.service;

import com.kaluzny.domain.Book;
import com.kaluzny.domain.BookRepository;
import com.kaluzny.dto.BookCreateRequest;
import com.kaluzny.dto.BookMapper;
import com.kaluzny.dto.BookResponse;
import com.kaluzny.dto.BookUpdateRequest;
import com.kaluzny.exception.BookNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository repository;
    private final BookMapper mapper;

    @Transactional
    public BookResponse create(BookCreateRequest request) {
        log.debug("Creating book: {}", request.name());
        Book book = mapper.toEntity(request);
        Book saved = repository.save(book);
        log.info("Created book with id: {}", saved.getId());
        return mapper.toResponse(saved);
    }

    public Page<BookResponse> findAll(Pageable pageable) {
        log.debug("Finding all books, page: {}", pageable.getPageNumber());
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    public BookResponse findById(Long id) {
        log.debug("Finding book by id: {}", id);
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public List<BookResponse> findByName(String name) {
        log.debug("Finding books by name: {}", name);
        return repository.findByName(name).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public BookResponse update(Long id, BookUpdateRequest request) {
        log.debug("Updating book id: {}", id);
        Book book = repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        mapper.updateEntity(book, request);
        Book updated = repository.save(book);
        log.info("Updated book with id: {}", updated.getId());
        return mapper.toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Deleting book id: {}", id);
        if (!repository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        repository.deleteById(id);
        log.info("Deleted book with id: {}", id);
    }
}
