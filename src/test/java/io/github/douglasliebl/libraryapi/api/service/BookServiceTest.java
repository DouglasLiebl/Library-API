package io.github.douglasliebl.libraryapi.api.service;

import io.github.douglasliebl.libraryapi.api.dto.BookDTO;
import io.github.douglasliebl.libraryapi.api.dto.BookUpdateDTO;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @DisplayName("Should get a book by id")
    public void getByIdTest() {
        // given
        Long id = 11L;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        // when
        Optional<Book> foundBook = service.getById(id);

        // then
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Should return empty when book not found")
    public void bookNotFoundTest() {
        // given
        Long id = 11L;

        // when
        Optional<Book> book = service.getById(id);

        //then
        assertThat(book.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should delete a book")
    public void deleteBookTest() {
        // given
        Book book = Book.builder().id(11L).build();

        // when
        assertDoesNotThrow(() -> service.delete(book));

        // then
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Should throw a exception when delete a inexistent book")
    public void deleteInexistentBookTest() {
        // given
        Book book = new Book();

        // when
        assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        // then
        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Should update a book")
    public void updateBookTest() {
        // given
        Long id = 11L;
        Book actualBook = Book.builder().id(id).isbn(createValidBook().getIsbn()).build();
        BookUpdateDTO updateData = BookUpdateDTO.builder()
                .title(createValidBook().getTitle())
                .author(createValidBook().getAuthor()).build();

        // when
        Book updatedBook = createValidBook();
        updatedBook.setId(id);
        Mockito.when(repository.save(actualBook)).thenReturn(updatedBook);

        // then
        BookDTO book = service.update(actualBook, updateData);

        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Should not update a book")
    public void updateInvalidBook() {
        // given
        Book actualBook = createValidBook();
        BookUpdateDTO updateData = new BookUpdateDTO();

        // when
        assertThrows(IllegalArgumentException.class, () -> service.update(actualBook, updateData));

        // then
        Mockito.verify(repository, Mockito.never()).save(actualBook);
    }

    private static Book createValidBook() {
        return Book.builder().title("My Book").author("Author").isbn("123456").build();
    }
}