package example.training.bookmanagement.infrastructure.repositories.jparepositories;

import example.training.bookmanagement.domain.model.bookaggregate.Book;
import example.training.bookmanagement.domain.model.bookaggregate.BookId;
import example.training.bookmanagement.domain.model.bookaggregate.Isbn13;
import example.training.bookmanagement.domain.model.bookaggregate.Title;
import example.training.bookmanagement.domain.repositories.BookRepository;
import example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway.BookJpaGateway;
import example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway.UserJpaGateway;
import example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway.jpamodel.BookJpaModel;
import example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway.jpamodel.UserJpaModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaBookRepository implements BookRepository {
    private final BookJpaGateway bookJpaGateway;
    private final UserJpaGateway userJpaGateway;

    public JpaBookRepository(BookJpaGateway bookJpaGateway, UserJpaGateway userJpaGateway) {
        this.bookJpaGateway = bookJpaGateway;
        this.userJpaGateway = userJpaGateway;
    }

    //この部分はfactoryで実装するべき
    private static Book convertBookJpaModelToBook(BookJpaModel bookJpaModel) {
        return Book.create(
                BookId.fromString(bookJpaModel.getId()),
                bookJpaModel.getIsbn13() == null ? null : Isbn13.of(bookJpaModel.getIsbn13()),
                Title.of(bookJpaModel.getTitle())
        );
    }

    private BookJpaModel convertBookToBookJpaModel(Book book) {
        UserJpaModel borrower = null;
        if (book.getBorrowerId().isPresent()) {
            borrower = userJpaGateway.findById(book.getBorrowerId().toString())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        }

        BookJpaModel bookJpaModel = BookJpaModel.builder()
                .id(book.getId().toString())
                .title(book.getTitle().toString())
                .isbn13(book.getIsbn13().map(Isbn13::toString).orElseThrow())
                .borrower(borrower)
                .status(book.getStatus().name())
                .build();

        return bookJpaModel;
    }

    @Override
    public BookId generateId() {
        return BookId.of(UUID.randomUUID());
    }

    @Override
    public Optional<Book> findById(BookId bookId) {
        return bookJpaGateway.findById(bookId.toString())
                .map(bookJpaModel -> convertBookJpaModelToBook(bookJpaModel));
    }

    @Override
    public void save(Book book) {
        BookJpaModel bookJpaModel = convertBookToBookJpaModel(book);
        bookJpaGateway.save(bookJpaModel);
    }
}
