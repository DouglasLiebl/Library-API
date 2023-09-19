package io.github.douglasliebl.msbooks.api.model.repository;

import io.github.douglasliebl.msbooks.api.model.entity.Book;
import io.github.douglasliebl.msbooks.api.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndReturnedIsFalseOrReturnedIsNull(Book book);

    Page<Loan> findByBookIsbnOrCustomer(@Param("isbn") String isbn, @Param("customer") String customer, Pageable pageRequest);
}
