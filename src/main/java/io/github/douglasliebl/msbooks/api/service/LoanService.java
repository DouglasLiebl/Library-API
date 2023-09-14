package io.github.douglasliebl.msbooks.api.service;

import io.github.douglasliebl.msbooks.api.dto.LoanDTO;
import io.github.douglasliebl.msbooks.api.model.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan request);

    Optional<Loan> getById(Long id);

    Loan update(Loan response);
}
