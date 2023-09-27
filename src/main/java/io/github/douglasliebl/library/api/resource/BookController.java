package io.github.douglasliebl.library.api.resource;

import io.github.douglasliebl.library.api.dto.BookDTO;
import io.github.douglasliebl.library.api.dto.BookUpdateDTO;
import io.github.douglasliebl.library.api.dto.LoanDTO;
import io.github.douglasliebl.library.api.model.entity.Book;
import io.github.douglasliebl.library.api.model.entity.Loan;
import io.github.douglasliebl.library.api.service.BookService;
import io.github.douglasliebl.library.api.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;
    private final ModelMapper mapper;
    private final LoanService loanService;

    @PostMapping
    @Operation(summary = "Create Book")
    public ResponseEntity createBook(@RequestBody @Valid BookDTO request) {
        log.info("creating a book for isbn: {}", request.getIsbn());
        var book = mapper.map(bookService.save(mapper.map(request, Book.class)), BookDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @GetMapping("{id}")
    @Operation(summary = "Find a book based on id")
    public ResponseEntity getBook(@PathVariable Long id) {
        log.info("obtaining details for book id: {}", id);
        var book = mapper.map(bookService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)), BookDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(book);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes a book by id")
    public void deleteBook(@PathVariable Long id) {
        log.info("deleting book with id: {}", id);
        bookService.delete(bookService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PutMapping("{id}")
    @Operation(summary = "Updates a book")
    public ResponseEntity updateBook(@PathVariable Long id,
                                     @RequestBody BookUpdateDTO request) {
        log.info("updating book with id: {}", id);
        var actualBook = bookService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var updatedBook = bookService.update(actualBook, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedBook);
    }

    @GetMapping
    @Operation(summary = "Get all books")
    public ResponseEntity find(BookDTO book, Pageable pageRequest) {
        var result = bookService.find(mapper.map(book, Book.class), pageRequest);
        List<BookDTO> response = result
                .stream()
                .map(entity -> mapper.map(entity, BookDTO.class))
                .toList();
        PageImpl<BookDTO> pagedResponse = new PageImpl<>(response, pageRequest, result.getTotalElements());

        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }

    @GetMapping("{id}/loans")
    public ResponseEntity loansByBook(@PathVariable Long id, Pageable pageRequest) {
        var book = bookService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Page<Loan> result = loanService.getLoansByBook(book, pageRequest);
        List<LoanDTO> response = result.stream()
                .map(entity -> {
                    BookDTO loanedBook = mapper.map(entity.getBook(), BookDTO.class);
                    LoanDTO loan = mapper.map(entity, LoanDTO.class);
                    loan.setBook(loanedBook);
                    return loan;
                })
                .collect(Collectors.toList());
        PageImpl<LoanDTO> pagedResponse = new PageImpl<>(response, pageRequest, result.getTotalElements());

        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }
}
