package io.github.douglasliebl.libraryapi.api.resource;

import io.github.douglasliebl.libraryapi.api.dto.BookDTO;
import io.github.douglasliebl.libraryapi.api.dto.BookUpdateDTO;
import io.github.douglasliebl.libraryapi.api.model.entity.Book;
import io.github.douglasliebl.libraryapi.api.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final ModelMapper mapper;

    @PostMapping
    public ResponseEntity createBook(@RequestBody @Valid BookDTO request) {
        var book = mapper.map(bookService.save(mapper.map(request, Book.class)), BookDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @GetMapping("{id}")
    public ResponseEntity getBook(@PathVariable Long id) {
        var book = mapper.map(bookService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)), BookDTO.class);
        return ResponseEntity.status(HttpStatus.OK).body(book);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.delete(bookService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PutMapping("{id}")
    public ResponseEntity updateBook(@PathVariable Long id,
                                     @RequestBody BookUpdateDTO request) {
        var actualBook = bookService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var updatedBook = bookService.update(actualBook, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedBook);
    }

}
