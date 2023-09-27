package io.github.douglasliebl.library.api.model.repository;

import io.github.douglasliebl.library.api.model.entity.Book;
import io.github.douglasliebl.library.api.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndReturnedIsFalseOrReturnedIsNull(Book book);

    Page<Loan> findByBookIsbnOrCustomer(String isbn, String customer, Pageable pageRequest);

    Page<Loan> findByBook(Book book, Pageable pageRequest);

    List<Loan> findByLoanDateLessThanAndReturnedIsFalseOrReturnedIsNull(LocalDate threeDaysAgo);
}
