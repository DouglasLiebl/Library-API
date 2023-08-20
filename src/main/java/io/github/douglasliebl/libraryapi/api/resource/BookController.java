package io.github.douglasliebl.libraryapi.api.resource;

import io.github.douglasliebl.libraryapi.api.dto.BookDTO;
import io.github.douglasliebl.libraryapi.api.model.entity.Book;
import io.github.douglasliebl.libraryapi.api.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
