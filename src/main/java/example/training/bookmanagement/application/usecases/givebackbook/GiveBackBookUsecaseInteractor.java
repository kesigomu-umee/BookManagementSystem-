package example.training.bookmanagement.application.usecases.givebackbook;

import example.training.bookmanagement.application.usecases.Usecase;
import example.training.bookmanagement.domain.model.bookaggregate.Book;
import example.training.bookmanagement.domain.model.bookaggregate.BookId;
import example.training.bookmanagement.domain.model.useraggregate.UserId;
import example.training.bookmanagement.domain.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GiveBackBookUsecaseInteractor implements Usecase<GiveBackBookInputData, Void> {
    private final BookRepository bookRepository;

    public GiveBackBookUsecaseInteractor(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Void handle(GiveBackBookInputData giveBackBookInputData) {
        Book book = bookRepository.findById(BookId.fromString(giveBackBookInputData.getBookId()))
                .orElseThrow(() -> new IllegalArgumentException("book not found"));

        book.giveBack(UserId.fromString(giveBackBookInputData.getBorrowerId()));

        bookRepository.save(book);
        return null;
    }
}
