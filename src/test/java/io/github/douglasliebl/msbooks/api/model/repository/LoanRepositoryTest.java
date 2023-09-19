package io.github.douglasliebl.msbooks.api.model.repository;

import io.github.douglasliebl.msbooks.api.model.entity.Book;
import io.github.douglasliebl.msbooks.api.model.entity.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LoanRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LoanRepository repository;


    @Test
    @DisplayName("Should verify if exists loan with a book not returned")
    public void existsByBookAndReturnedIsFalseTest() {
        // given
        Book book = Book.builder().build();
        Loan loan = Loan.builder()
                .book(book)
                .customer("Customer")
                .loanDate(LocalDate.now())
                .build();

        entityManager.persist(book);
        entityManager.persist(loan);

        // when
        boolean exists = repository.existsByBookAndReturnedIsFalseOrReturnedIsNull(book);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should find loan by book isbn or customer")
    public void findByBookIsbnOrCustomer() {
        // given
        Book book = Book.builder().isbn("123").build();
        Loan loan = Loan.builder()
                .book(book)
                .customer("Customer")
                .loanDate(LocalDate.now())
                .build();

        entityManager.persist(book);
        entityManager.persist(loan);

        // when
        Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Customer", PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);

    }
}