package io.github.douglasliebl.library.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.douglasliebl.library.api.dto.BookDTO;
import io.github.douglasliebl.library.api.dto.BookUpdateDTO;
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

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;


    @Test
    @DisplayName("Must create a book with success.")
    public void createBookTest() throws Exception {

        BookDTO bookDTO = createNewBook();
        Book savedBook = Book.builder().id(1L).author("Author").title("My Book").isbn("123456").build();

        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willReturn(savedBook);
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));
    }

    @Test
    @DisplayName("Must throw an Exception.")
    public void creatInvalidBookTest() throws Exception{

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Should throw an Exception when trying to register an already used isbn")
    public void createBookWithDuplicatedIsbn() throws Exception {

        BookDTO bookDTO = createNewBook();
        String json = new ObjectMapper().writeValueAsString(bookDTO);

        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException("Isbn already exists."));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Isbn already exists."));
    }

    @Test
    @DisplayName("Should obtain book details")
    public void getBookTest() throws Exception {
        // given
        Long id = 11L;
        Book book = Book.builder().id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given(bookService.getById(id))
                .willReturn(Optional.of(book));

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

    }

    @Test
    @DisplayName("Should throw resource not found exception when book does not exist.")
    public void bookNotFoundTest() throws Exception {
        // given
        BDDMockito.given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Should delete a book")
    public void deleteBookTest() throws Exception{
        // given
        BDDMockito.given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(Book.builder().id(11L).build()));

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 11))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should throw resource not found when not found the book to delete")
    public void deleteNotRegisteredBookTest() throws Exception {
        // given
        BDDMockito.given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update a book")
    public void updateBookTeste() throws Exception {
        // given
        Long id = 11L;
        BookUpdateDTO updateData = BookUpdateDTO.builder().title("My Book").author("Author").build();
        String json = new ObjectMapper()
                .writeValueAsString(updateData);

        Book updatingBook = Book.builder()
                .id(id)
                .title("some title")
                .author("some Author")
                .isbn("123456").build();
        BDDMockito.given(bookService.getById(id))
                .willReturn(Optional.of(updatingBook));

        BookDTO updatedBook = BookDTO.builder()
                .id(id)
                .title("My Book")
                .author("Author")
                .isbn("123456").build();
        BDDMockito.given(bookService.update(updatingBook, updateData))
                .willReturn(updatedBook);

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 11))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value("My Book"))
                .andExpect(jsonPath("author").value("Author"))
                .andExpect(jsonPath("isbn").value("123456"));
    }

    @Test
    @DisplayName("Should return not found when not found the book to update")
    public void updateNotRegisteredBookTest() throws Exception {
        // given
        String json = new ObjectMapper().writeValueAsString(createNewBook());
        BDDMockito.given(bookService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should stream books")
    public void findBooksTest() throws Exception {
        // given
        Long id = 11L;
        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given(bookService.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Collections.singletonList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=t%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));

    }

    @Test
    @DisplayName("Should get loans by book")
    public void getLoansByBookTest() throws Exception {
        // given
        Long id = 11L;
        Book book = Book.builder().id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        Loan loan = Loan.builder()
                .id(id)
                .book(book)
                .returned(true)
                .customer("Customer")
                .build();

        BDDMockito.given(bookService.getById(id))
                .willReturn(Optional.of(book));

        BDDMockito.given(loanService.getLoansByBook(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Collections.singletonList(loan), PageRequest.of(0, 10), 1));

        String queryString = "?page=0&size=10";

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat("/11/loans" + queryString))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(10))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    @Test
    @DisplayName("Should throw an Exception when book who searched not are registered")
    public void getLoansByNotRegisteredBookTest() throws Exception {
        // given
        Book book = Book.builder().id(1L).build();
        Loan loan = Loan.builder()
                .id(1L)
                .book(book)
                .returned(true)
                .customer("Customer")
                .build();

        BDDMockito.given(bookService.getById(loan.getBook().getId()))
                .willReturn(Optional.empty());

        String queryString = "?page=0&size=10";

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat("/1/loans" + queryString))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    private static BookDTO createNewBook() {
        return BookDTO.builder().author("Author").title("My Book").isbn("123456").build();
    }


}