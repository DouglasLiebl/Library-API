package io.github.douglasliebl.libraryapi.api.service.impl;

import io.github.douglasliebl.libraryapi.api.dto.BookDTO;
import io.github.douglasliebl.libraryapi.api.dto.BookUpdateDTO;
import io.github.douglasliebl.libraryapi.api.exception.BusinessException;
import io.github.douglasliebl.libraryapi.api.model.entity.Book;
import io.github.douglasliebl.libraryapi.api.model.repository.BookRepository;
import io.github.douglasliebl.libraryapi.api.service.BookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private BookRepository repository;
    private ModelMapper mapper;

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
        repository.delete(book);
    }

    @Override
    public BookDTO update(Book actualBook, BookUpdateDTO request) {
        actualBook.setTitle(request.getTitle());
        actualBook.setAuthor(request.getAuthor());

        return mapper.map(repository.save(actualBook), BookDTO.class);
    }
}
