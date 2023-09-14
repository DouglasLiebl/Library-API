package io.github.douglasliebl.msbooks.api.service.impl;

import io.github.douglasliebl.msbooks.api.dto.LoanDTO;
import io.github.douglasliebl.msbooks.api.exception.BusinessException;
import io.github.douglasliebl.msbooks.api.model.entity.Book;
import io.github.douglasliebl.msbooks.api.model.entity.Loan;
import io.github.douglasliebl.msbooks.api.model.repository.LoanRepository;
import io.github.douglasliebl.msbooks.api.service.BookService;
import io.github.douglasliebl.msbooks.api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository repository;

    @Override
    public Loan save(Loan request) {
        if (repository.existsByBookAndReturnedIsFalseOrReturnedIsNull(request.getBook())) throw new BusinessException("Book already loaned.");

        return repository.save(request);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public Loan update(Loan response) {
        return null;
    }


}
