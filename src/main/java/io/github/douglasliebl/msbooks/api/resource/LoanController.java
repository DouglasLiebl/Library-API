package io.github.douglasliebl.msbooks.api.resource;

import io.github.douglasliebl.msbooks.api.dto.LoanDTO;
import io.github.douglasliebl.msbooks.api.dto.ReturnedLoanDTO;
import io.github.douglasliebl.msbooks.api.model.entity.Book;
import io.github.douglasliebl.msbooks.api.model.entity.Loan;
import io.github.douglasliebl.msbooks.api.service.BookService;
import io.github.douglasliebl.msbooks.api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanservice;
    private final BookService bookservice;

    @PostMapping
    public ResponseEntity createLoan(@RequestBody LoanDTO request) {
        Book book = bookservice.getBookByIsbn(request.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan loan = Loan.builder()
                .customer(request.getCustomer())
                .book(book)
                .loanDate(LocalDate.now())
                .build();
        var response = loanservice.save(loan);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("{id}")
    public void returnedBook(@PathVariable Long id,
                             @RequestBody ReturnedLoanDTO request) {
        Loan response = loanservice.getById(id).get();
        response.setReturned(response.getReturned());
        loanservice.update(response);

    }
}
