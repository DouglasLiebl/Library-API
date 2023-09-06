package io.github.douglasliebl.libraryapi.api.service;

import io.github.douglasliebl.libraryapi.api.dto.BookDTO;
import io.github.douglasliebl.libraryapi.api.dto.BookUpdateDTO;
import io.github.douglasliebl.libraryapi.api.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {
    Book save(Book request);

    Optional<Book> getById(Long id);

    void delete(Book book);

    BookDTO update(Book actualBook, BookUpdateDTO request);

    Page<Book> find(Book filter, Pageable PageRequest);
}
