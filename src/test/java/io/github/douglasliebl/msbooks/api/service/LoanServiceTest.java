package io.github.douglasliebl.msbooks.api.service;

import io.github.douglasliebl.msbooks.api.exception.BusinessException;
import io.github.douglasliebl.msbooks.api.model.entity.Book;
import io.github.douglasliebl.msbooks.api.model.entity.Loan;
import io.github.douglasliebl.msbooks.api.model.repository.LoanRepository;
import io.github.douglasliebl.msbooks.api.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LoanServiceTest {

    private LoanService service;

    @MockBean
    private LoanRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save a loan")
    public void saveLoanTest() {
        // given
        Book book = Book.builder().id(1L).isbn("123").build();
        String customer = "Customer";

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(1L)
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        // when
        Mockito.when(repository.existsByBookAndReturnedIsFalseOrReturnedIsNull(book))
                .thenReturn(false);
        Mockito.when(repository.save(savingLoan))
                .thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        // then
        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    @DisplayName("Should throw an exception when book already loaned")
    public void loanedBookTest() {
        // given
        Book book = Book.builder().id(1L).build();
        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Customer")
                .loanDate(LocalDate.now())
                .build();

        // when
        Mockito.when(repository.existsByBookAndReturnedIsFalseOrReturnedIsNull(book))
                .thenReturn(true);
        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        // then
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned.");

        Mockito.verify(repository, Mockito.never()).save(savingLoan);
    }

}