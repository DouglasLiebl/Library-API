package io.github.douglasliebl.libraryapi.api.service.impl;

import io.github.douglasliebl.libraryapi.api.dto.BookDTO;
import io.github.douglasliebl.libraryapi.api.dto.BookUpdateDTO;
import io.github.douglasliebl.libraryapi.api.exception.BusinessException;
import io.github.douglasliebl.libraryapi.api.model.entity.Book;
import io.github.douglasliebl.libraryapi.api.model.repository.BookRepository;
import io.github.douglasliebl.libraryapi.api.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book request) {
        if (repository.existsByIsbn(request.getIsbn())) {
            throw new BusinessException("Isbn already exists.");
        }
        return repository.save(request);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id cannot be null");
        }
        repository.delete(book);
    }

    @Override
    public BookDTO update(Book actualBook, BookUpdateDTO request) {
        if (request == null || actualBook.getId() == null) {
            throw new IllegalArgumentException("Update information or actual book id cannot be null");
        }

        actualBook.setTitle(request.getTitle());
        actualBook.setAuthor(request.getAuthor());
        Book updatedBook = repository.save(actualBook);

        return BookDTO.builder()
                .id(updatedBook.getId())
                .title(updatedBook.getTitle())
                .author(updatedBook.getAuthor())
                .isbn(updatedBook.getIsbn()).build();
    }

    @Override
    public Page<Book> find(Book filter, Pageable pageInfo) {
        Example<Book> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example, pageInfo);
    }

}
