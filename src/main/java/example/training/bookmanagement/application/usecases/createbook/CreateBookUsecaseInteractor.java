package example.training.bookmanagement.application.usecases.createbook;

import example.training.bookmanagement.application.usecases.Usecase;
import example.training.bookmanagement.domain.model.bookaggregate.Book;
import example.training.bookmanagement.domain.model.bookaggregate.Isbn13;
import example.training.bookmanagement.domain.model.bookaggregate.Title;
import example.training.bookmanagement.domain.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBookUsecaseInteractor implements Usecase<CreateBookInputData, Book> {
    private final BookRepository bookRepository;

    public CreateBookUsecaseInteractor(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Book handle(CreateBookInputData createBookInputData) {
        Book book = Book.create(
                bookRepository.generateId(),
                Isbn13.of(createBookInputData.getIsbn13().orElse(null)),
                Title.of(createBookInputData.getTitle())
        );
        bookRepository.save(book);
        return book;
    }
}
