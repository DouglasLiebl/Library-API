package io.github.douglasliebl.library.api.resource;

import io.github.douglasliebl.library.api.dto.BookDTO;
import io.github.douglasliebl.library.api.dto.LoanDTO;
import io.github.douglasliebl.library.api.dto.LoanFilterDTO;
import io.github.douglasliebl.library.api.dto.ReturnedLoanDTO;
import io.github.douglasliebl.library.api.model.entity.Book;
import io.github.douglasliebl.library.api.model.entity.Loan;
import io.github.douglasliebl.library.api.service.BookService;
import io.github.douglasliebl.library.api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanservice;
    private final BookService bookservice;
    private final ModelMapper mapper;

    @PostMapping
    public ResponseEntity createLoan(@RequestBody LoanDTO request) {
        Book book = bookservice.getBookByIsbn(request.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan loan = Loan.builder()
                .customer(request.getCustomer())
                .email(request.getEmail())
                .book(book)
                .loanDate(LocalDate.now())
                .build();
        var response = loanservice.save(loan);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("{id}")
    public void returnedBook(@PathVariable Long id,
                             @RequestBody ReturnedLoanDTO request) {
        Loan response = loanservice.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        response.setReturned(response.getReturned());
        loanservice.update(response);
    }

    @GetMapping
    public ResponseEntity find(LoanFilterDTO request, Pageable pageRequest) {
        Page<Loan> result = loanservice.find(request, pageRequest);
        List<LoanDTO> response = result.stream()
                .map(entity -> {
                    BookDTO book = mapper.map(entity.getBook(), BookDTO.class);
                    LoanDTO loan = mapper.map(entity, LoanDTO.class);
                    loan.setBook(book);
                    return loan;
                })
                .collect(Collectors.toList());
        PageImpl<LoanDTO> pagedResponse = new PageImpl<>(response, pageRequest, result.getTotalElements());

        return ResponseEntity.status(HttpStatus.OK).body(pagedResponse);
    }
}
