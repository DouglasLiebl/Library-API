package io.github.douglasliebl.library.api.service;

import io.github.douglasliebl.library.api.dto.BookDTO;
import io.github.douglasliebl.library.api.dto.BookUpdateDTO;
import io.github.douglasliebl.library.api.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {

    Book save(Book request);

    Optional<Book> getById(Long id);

    void delete(Book book);

    BookDTO update(Book actualBook, BookUpdateDTO request);

    Page<Book> find(Book filter, Pageable PageRequest);

    Optional<Book> getBookByIsbn(String isbn);
}
