package io.github.douglasliebl.libraryapi.api.model.repository;

import io.github.douglasliebl.libraryapi.api.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
