package io.github.douglasliebl.msbooks.api.service.impl;

import io.github.douglasliebl.msbooks.api.dto.LoanFilterDTO;
import io.github.douglasliebl.msbooks.api.exception.BusinessException;
import io.github.douglasliebl.msbooks.api.model.entity.Book;
import io.github.douglasliebl.msbooks.api.model.entity.Loan;
import io.github.douglasliebl.msbooks.api.model.repository.LoanRepository;
import io.github.douglasliebl.msbooks.api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan response) {
        return repository.save(response);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filter, Pageable pageable) {
        return repository.findByBookIsbnOrCustomer(filter.getIsbn(), filter.getCustomer(), pageable);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageRequest) {
        return repository.findByBook(book, pageRequest);
    }

    @Override
    public List<Loan> getAllLateLoans() {
        LocalDate threeDaysAgo = LocalDate.now().minusDays(4);
        return null;
    }


}
