package example.training.bookmanagement.domain.repositories;

import example.training.bookmanagement.domain.model.bookaggregate.Book;
import example.training.bookmanagement.domain.model.bookaggregate.BookId;

import java.util.Optional;

public interface BookRepository {
    BookId generateId();

    Optional<Book> findById(BookId bookId);

    void save(Book book);
}
