package example.training.bookmanagement.application.usecases.createbook;

import example.training.bookmanagement.domain.model.bookaggregate.Book;
import example.training.bookmanagement.domain.model.bookaggregate.BookId;
import example.training.bookmanagement.domain.model.bookaggregate.Isbn13;
import example.training.bookmanagement.domain.model.bookaggregate.Title;
import example.training.bookmanagement.domain.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBookUsecaseInteractorTest {
    @InjectMocks
    CreateBookUsecaseInteractor sut;

    @Mock
    BookRepository bookRepository;

    @Test
    void 書籍を登録できる() {
        var createBookInputData = CreateBookInputData.builder()
                .isbn13("9784774153773")
                .title("JUnit実践入門")
                .build();
        var bookId = BookId.fromString("00000000-0000-0000-0001-000000000001");
        var expectBook = Book.create(
                bookId,
                Isbn13.of("9784774153773"),
                Title.of("JUnit実践入門")
        );

        doReturn(bookId).when(bookRepository).generateId();
        doNothing().when(bookRepository).save(expectBook);

        sut.handle(createBookInputData);

        verify(bookRepository, times(1)).save(expectBook);
    }
}