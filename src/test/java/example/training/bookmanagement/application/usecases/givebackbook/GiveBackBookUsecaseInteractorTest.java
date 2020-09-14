package example.training.bookmanagement.application.usecases.givebackbook;

import example.training.bookmanagement.domain.model.bookaggregate.*;
import example.training.bookmanagement.domain.model.useraggregate.UserId;
import example.training.bookmanagement.domain.repositories.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiveBackBookUsecaseInteractorTest {
    @InjectMocks
    GiveBackBookUsecaseInteractor sut;

    @Mock
    BookRepository bookRepository;

    @Test
    void 書籍を返却できる() {
        var giveBackBookInputData = GiveBackBookInputData.builder()
                .bookId("00000000-0000-0000-0001-000000000001")
                .borrowerId("00000000-0000-0000-0000-000000000001")
                .build();
        var book = Book.builder()
                .id(BookId.fromString("00000000-0000-0000-0001-000000000001"))
                .isbn13(Isbn13.of("9784774153773"))
                .title(Title.of("JUnit実践入門"))
                .status(BookStatus.InLending)
                .borrowerId(UserId.fromString("00000000-0000-0000-0000-000000000001"))
                .build();
        var expectBook = Book.builder()
                .id(BookId.fromString("00000000-0000-0000-0001-000000000001"))
                .isbn13(Isbn13.of("9784774153773"))
                .title(Title.of("JUnit実践入門"))
                .status(BookStatus.Lendable)
                .borrowerId(null)
                .build();

        doReturn(Optional.of(book)).when(bookRepository).findById(book.getId());
        doNothing().when(bookRepository).save(expectBook);

        sut.handle(giveBackBookInputData);

        verify(bookRepository, times(1)).save(expectBook);
    }

    @Test
    void 書籍が存在しない場合例外が発生する() {
        var giveBackBookInputData = GiveBackBookInputData.builder()
                .bookId("00000000-0000-0000-0001-000000000001")
                .borrowerId("00000000-0000-0000-0000-000000000001")
                .build();

        doReturn(Optional.empty()).when(bookRepository).findById(BookId.fromString("00000000-0000-0000-0001-000000000001"));
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> sut.handle(giveBackBookInputData)
        );

        assertAll(
                () -> assertEquals("book not found", exception.getMessage()),
                () -> verify(bookRepository, never()).save(any())
        );
    }

    @Test
    void 利用者が書籍のBollowerIdと一致しない場合例外が発生する() {
        var giveBackBookInputData = GiveBackBookInputData.builder()
                .bookId("00000000-0000-0000-0001-000000000001")
                .borrowerId("00000000-0000-0000-0000-000000000001")
                .build();
        var book = Book.builder()
                .id(BookId.fromString("00000000-0000-0000-0001-000000000001"))
                .isbn13(Isbn13.of("9784774153773"))
                .title(Title.of("JUnit実践入門"))
                .status(BookStatus.InLending)
                .borrowerId(UserId.fromString("00000000-0000-0000-0000-000000000002"))
                .build();

        doReturn(Optional.of(book)).when(bookRepository).findById(book.getId());

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> sut.handle(giveBackBookInputData)
        );
        assertAll(
                () -> assertEquals("borrowerId does not match", exception.getMessage()),
                () -> verify(bookRepository, never()).save(any())
        );
    }
}