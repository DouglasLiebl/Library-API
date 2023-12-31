package io.github.douglasliebl.library.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.douglasliebl.library.api.dto.LoanDTO;
import io.github.douglasliebl.library.api.dto.LoanFilterDTO;
import io.github.douglasliebl.library.api.dto.ReturnedLoanDTO;
import io.github.douglasliebl.library.api.exception.BusinessException;
import io.github.douglasliebl.library.api.model.entity.Book;
import io.github.douglasliebl.library.api.model.entity.Loan;
import io.github.douglasliebl.library.api.service.BookService;
import io.github.douglasliebl.library.api.service.LoanService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Should make a loan")
    public void createLoanTest() throws Exception {
        // given
        LoanDTO dto = LoanDTO.builder()
                .isbn("123")
                .customer("Customer")
                .email("customer@gmail.com").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(11L)
                .isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn(book.getIsbn()))
                .willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(1L)
                .customer("Customer")
                .email("customer@gmail.com")
                .book(book)
                .loanDate(LocalDate.now()).build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willReturn(loan);

        // when
        MockHttpServletRequestBuilder requestBuilders = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(requestBuilders)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L));
    }

    @Test
    @DisplayName("Should throw an error when try create a loan with a invalid book")
    public void invalidIsbnCreateLoanTest() throws Exception {
        // given
        LoanDTO dto = LoanDTO.builder()
                .isbn("123")
                .customer("Customer").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.empty());

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
    }

    @Test
    @DisplayName("Should throw an error when try create a loan with a already loaned book")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {
        // given
        LoanDTO dto = LoanDTO.builder()
                .isbn("123")
                .customer("Customer").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(11L)
                .isbn("123").build();
        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book already loaned."));

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned."));
    }

    @Test
    @DisplayName("Should return a book")
    public void returnBookTest() throws Exception {
        // given
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);
        Loan loan = Loan.builder().id(1L).build();

        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(loan));

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    @DisplayName("Should throw an Exception when try to return a not loaned book")
    public void returnNotLoanedBook() throws Exception {
        // given
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(loanService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should filter loans")
    public void findLoansTest() throws Exception {
        // given
        Book book = Book.builder().id(1L).isbn("123").build();
        Loan loan =  Loan.builder()
                .id(1L)
                .book(book)
                .customer("Customer")
                .loanDate(LocalDate.now())
                .returned(true)
                .build();

        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Collections.singletonList(loan), PageRequest.of(0, 10), 1));

        String query = String.format("?isbn=%s&customer=%s&page=0&size=10",
                loan.getBook().getIsbn(), loan.getCustomer());

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LOAN_API.concat(query))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(10))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }
}
