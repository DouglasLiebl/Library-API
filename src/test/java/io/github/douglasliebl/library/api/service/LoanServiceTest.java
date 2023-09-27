package io.github.douglasliebl.library.api.service;

import io.github.douglasliebl.library.api.dto.LoanFilterDTO;
import io.github.douglasliebl.library.api.exception.BusinessException;
import io.github.douglasliebl.library.api.model.entity.Book;
import io.github.douglasliebl.library.api.model.entity.Loan;
import io.github.douglasliebl.library.api.model.repository.LoanRepository;
import io.github.douglasliebl.library.api.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

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

    @Test
    @DisplayName("Should get a loan details")
    public void getLoansDetailsTest() {
        // given
        Book book = Book.builder().id(1L).build();
        Loan loan = Loan.builder()
                .id(1L)
                .book(book)
                .customer("Customer")
                .loanDate(LocalDate.now())
                .build();

        // when
        Mockito.when(repository.findById(loan.getId()))
                .thenReturn(Optional.of(loan));

        Optional<Loan> response = service.getById(1L);

        // then
        assertThat(response.isPresent()).isTrue();
        assertThat(response.get().getId()).isEqualTo(loan.getId());
        assertThat(response.get().getBook()).isEqualTo(loan.getBook());
        assertThat(response.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(response.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(repository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should update a loan")
    public void updateLoanTest() {
        // given
        Book book = Book.builder().id(1L).build();
        Loan loan = Loan.builder()
                .id(1L)
                .book(book)
                .customer("Customer")
                .loanDate(LocalDate.now())
                .returned(true)
                .build();

        // when
        Mockito.when(repository.save(loan))
                .thenReturn(loan);

        Loan updatedLoan = service.update(loan);

        // then
        assertThat(updatedLoan.getReturned()).isTrue();
        Mockito.verify(repository, Mockito.times(1)).save(loan);
    }

    @Test
    @DisplayName("Should filter loans by properties")
    public void findLoansTest() {
        // given
        Book book = Book.builder().id(1L).build();
        Loan loan = Loan.builder()
                .id(1L)
                .book(book)
                .customer("Customer")
                .loanDate(LocalDate.now())
                .returned(true)
                .build();

        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder()
                .customer("Customer")
                .isbn("123")
                .build();

        Pageable pageRequest = PageRequest.of(0, 10);
        List<Loan> list = Collections.singletonList(loan);

        Page<Loan> page = new PageImpl<Loan>(list, pageRequest, list.size());

        // when
        Mockito.when(repository.findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Loan> result = service.find(loanFilterDTO, pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should find loans by book")
    public void findLoansByBookTest() {
        // given
        Book book = Book.builder().id(1L).build();
        Loan loan = Loan.builder()
                .id(1L)
                .book(book)
                .customer("Customer")
                .loanDate(LocalDate.now())
                .returned(true)
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Mockito.when(repository.findByBook(book, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(loan), pageable, 1));

        Page<Loan> result = service.getLoansByBook(book, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(Collections.singletonList(loan));
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

}