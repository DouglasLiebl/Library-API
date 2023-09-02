package io.github.douglasliebl.libraryapi.api.service;

import io.github.douglasliebl.libraryapi.api.exception.BusinessException;
import io.github.douglasliebl.libraryapi.api.model.entity.Book;
import io.github.douglasliebl.libraryapi.api.model.repository.BookRepository;
import io.github.douglasliebl.libraryapi.api.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
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

        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
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

    @Test
    @DisplayName("Should throw an Exception when trying to register an already used isbn")
    public void shouldNotSaveBookWithDuplicatedISBN() {

        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn already exists.");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    private static Book createValidBook() {
        return Book.builder().title("My Book").author("Author").isbn("123456").build();
    }
}