package io.github.douglasliebl.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.douglasliebl.libraryapi.api.dto.BookDTO;
import io.github.douglasliebl.libraryapi.api.exception.BusinessException;
import io.github.douglasliebl.libraryapi.api.model.entity.Book;
import io.github.douglasliebl.libraryapi.api.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;


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

    private static BookDTO createNewBook() {
        return BookDTO.builder().author("Author").title("My Book").isbn("123456").build();
    }

}
