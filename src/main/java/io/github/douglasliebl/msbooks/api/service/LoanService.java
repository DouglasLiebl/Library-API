package io.github.douglasliebl.msbooks.api.service;

import io.github.douglasliebl.msbooks.api.dto.LoanDTO;
import io.github.douglasliebl.msbooks.api.model.entity.Loan;

public interface LoanService {
    Loan save(Loan request);
}
