package example.training.bookmanagement.application.usecases.findbook;

import example.training.bookmanagement.domain.model.bookaggregate.Book;
import example.training.bookmanagement.domain.model.bookaggregate.BookId;
import example.training.bookmanagement.domain.model.bookaggregate.Isbn13;
import example.training.bookmanagement.domain.model.bookaggregate.Title;
import example.training.bookmanagement.domain.repositories.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindBookUsecaseInteractorTest {
    @InjectMocks
    FindBookUsecaseInteractor sut;

    @Mock
    BookRepository bookRepository;

    @Test
    void 書籍情報を正常に取得できる() {
        var findBookInputData = FindBookInputData.builder()
                .bookId("00000000-0000-0000-0001-000000000001")
                .build();
        var expectBook = Book.create(
                BookId.fromString("00000000-0000-0000-0001-000000000001"),
                Isbn13.of("9784774153773"),
                Title.of("JUnit実践入門")
        );

        doReturn(Optional.of(expectBook)).when(bookRepository).findById(expectBook.getId());

        var resultBook = sut.handle(findBookInputData).orElseThrow();

        assertAll(
                () -> Assertions.assertEquals(expectBook, resultBook),
                () -> verify(bookRepository, times(1)).findById(expectBook.getId())
        );
    }

    @Test
    void 書籍が存在しない場合Optinal_emptyが返却される() {
        var findBookInputData = FindBookInputData.builder()
                .bookId("00000000-0000-0000-0001-000000000001")
                .build();
        var bookId = BookId.fromString(findBookInputData.getBookId());

        doReturn(Optional.empty()).when(bookRepository).findById(bookId);

        var result = sut.handle(findBookInputData);

        assertAll(
                () -> assertEquals(Optional.empty(), result),
                () -> verify(bookRepository, times(1)).findById(bookId)
        );
    }
}