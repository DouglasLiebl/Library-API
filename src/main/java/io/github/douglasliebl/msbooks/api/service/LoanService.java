package io.github.douglasliebl.msbooks.api.service;

import io.github.douglasliebl.msbooks.api.dto.LoanFilterDTO;
import io.github.douglasliebl.msbooks.api.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan request);

    Optional<Loan> getById(Long id);

    Loan update(Loan response);

    Page<Loan> find(LoanFilterDTO filter, Pageable pageable);
}
