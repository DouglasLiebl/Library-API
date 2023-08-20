package io.github.douglasliebl.libraryapi.api.service;

import io.github.douglasliebl.libraryapi.api.model.entity.Book;
import io.github.douglasliebl.libraryapi.api.model.repository.BookRepository;
import io.github.douglasliebl.libraryapi.api.service.impl.BookServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;
    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }
    @Test
    @DisplayName("Must save a book")
    public void saveBookTest() {

        Book book = Book.builder().title("My Book").author("Author").isbn("123456").build();
        Mockito.when(repository.save(book))
                .thenReturn(
                        Book.builder()
                                .id(1L)
                                .title("My Book")
                                .author("Author")
                                .isbn("123456").build());

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123456");
        assertThat(savedBook.getTitle()).isEqualTo("My Book");
        assertThat(savedBook.getAuthor()).isEqualTo("Author");
    }


}