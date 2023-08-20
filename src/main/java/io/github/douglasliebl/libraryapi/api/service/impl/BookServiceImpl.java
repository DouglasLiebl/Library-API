package io.github.douglasliebl.libraryapi.api.service.impl;

import io.github.douglasliebl.libraryapi.api.model.entity.Book;
import io.github.douglasliebl.libraryapi.api.model.repository.BookRepository;
import io.github.douglasliebl.libraryapi.api.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book request) {
        return repository.save(request);
    }
}
