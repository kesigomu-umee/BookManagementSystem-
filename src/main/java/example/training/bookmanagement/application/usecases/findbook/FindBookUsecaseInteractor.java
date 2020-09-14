package example.training.bookmanagement.application.usecases.findbook;

import example.training.bookmanagement.application.usecases.Usecase;
import example.training.bookmanagement.domain.model.bookaggregate.Book;
import example.training.bookmanagement.domain.model.bookaggregate.BookId;
import example.training.bookmanagement.domain.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FindBookUsecaseInteractor implements Usecase<FindBookInputData, Optional<Book>> {
    private final BookRepository bookRepository;

    public FindBookUsecaseInteractor(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> handle(FindBookInputData findBookInputData) {
        return bookRepository.findById(BookId.fromString(findBookInputData.getBookId()));
    }
}
