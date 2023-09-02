package io.github.douglasliebl.libraryapi.api.model.repository;

import io.github.douglasliebl.libraryapi.api.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Necessário para não substituir o banco de dados de teste pelo H2;
class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Should return true when a isbn already exists")
    public void returnTrueWhenIsbnAlreadyExists() {

        String isbn = "123";
        Book book = createNewBook(isbn);
        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(isbn);

        assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("Should return false when a isbn does not exists")
    public void returnFalseWhenIsbnDoesntExist() {
        // given
        String isbn = "123";

        // when
        boolean exists = repository.existsByIsbn(isbn);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should get a book by id")
    public void findByIdTest() {
        // given
        Book book = createNewBook("123");
        entityManager.persist(book); // entityManager.persist() é usado para persistir um objeto no banco de dados, ou seja, salvar um objeto no banco de dados;

        // when
        Optional<Book> foundBook = repository.findById(book.getId());

        // then
        assertThat(foundBook.isPresent()).isTrue();
    }


    private static Book createNewBook(String isbn) {
        return Book.builder().title("My book").author("Author").isbn(isbn).build();
    }
}