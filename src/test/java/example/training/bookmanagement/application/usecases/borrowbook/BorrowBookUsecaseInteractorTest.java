package example.training.bookmanagement.application.usecases.borrowbook;

import example.training.bookmanagement.domain.model.bookaggregate.*;
import example.training.bookmanagement.domain.model.borroweraggregate.Borrower;
import example.training.bookmanagement.domain.model.useraggregate.Name;
import example.training.bookmanagement.domain.model.useraggregate.Role;
import example.training.bookmanagement.domain.model.useraggregate.UserId;
import example.training.bookmanagement.domain.repositories.BookRepository;
import example.training.bookmanagement.domain.repositories.BorrowerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowBookUsecaseInteractorTest {
    @InjectMocks
    BorrowBookUsecaseInteractor sut;

    @Mock
    BookRepository bookRepository;

    @Mock
    BorrowerRepository borrowerRepository;

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/application/usecases/borrowbook/BorrowBook.csv", numLinesToSkip = 1)
    void 書籍を貸出できる(String comment, int bookSize) {
        var borrowBookInputData = BorrowBookInputData.builder()
                .bookId("00000000-0000-0000-0001-000000000001")
                .borrowerId("00000000-0000-0000-0000-000000000001")
                .build();
        var book = Book.create(
                BookId.fromString("00000000-0000-0000-0001-000000000001"),
                Isbn13.of("9784774153773"),
                Title.of("JUnit実践入門")
        );
        List<Book> borrowBooks = mock(List.class);
        var borrower = Borrower.builder()
                .id(UserId.fromString("00000000-0000-0000-0000-000000000001"))
                .name(Name.of("user"))
                .role(Role.GeneralUser)
                .borrowBooks(borrowBooks)
                .build();

        doReturn(bookSize).when(borrowBooks).size();
        doReturn(Optional.of(book)).when(bookRepository).findById(book.getId());
        doReturn(Optional.of(borrower)).when(borrowerRepository).findById(borrower.getId());
        doNothing().when(bookRepository).save(any());

        sut.handle(borrowBookInputData);

        verify(bookRepository, times(1)).save(argThat(updatedBook -> {
            return updatedBook.getBorrowerId().orElseThrow().equals(UserId.fromString("00000000-0000-0000-0000-000000000001"))
                    && updatedBook.getStatus() == BookStatus.InLending;
        }));
    }

    @Test
    void 書籍が存在しない場合例外が発生する() {
        var borrowBookInputData = BorrowBookInputData.builder()
                .bookId("00000000-0000-0000-0001-000000000001")
                .borrowerId("00000000-0000-0000-0000-000000000001")
                .build();

        doReturn(Optional.empty()).when(bookRepository).findById(BookId.fromString("00000000-0000-0000-0001-000000000001"));

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> sut.handle(borrowBookInputData)
        );

        assertAll(
                () -> assertEquals("book not found", exception.getMessage()),
                () -> verify(bookRepository, never()).save(any())
        );
    }

    @Test
    void 利用者が存在しない場合例外が発生する() {
        var borrowBookInputData = BorrowBookInputData.builder()
                .bookId("00000000-0000-0000-0001-000000000001")
                .borrowerId("00000000-0000-0000-0000-000000000001")
                .build();
        var book = Book.create(
                BookId.fromString("00000000-0000-0000-0001-000000000001"),
                Isbn13.of("9784774153773"),
                Title.of("JUnit実践入門")
        );

        doReturn(Optional.of(book)).when(bookRepository).findById(book.getId());
        doReturn(Optional.empty()).when(borrowerRepository).findById(UserId.fromString("00000000-0000-0000-0000-000000000001"));

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> sut.handle(borrowBookInputData)
        );

        assertAll(
                () -> assertEquals("borrower not found", exception.getMessage()),
                () -> verify(bookRepository, never()).save(any())
        );
    }

    @Test
    void 利用者が書籍を5冊以上借りていた場合例外が発生する() {
        var borrowBookInputData = BorrowBookInputData.builder()
                .bookId("00000000-0000-0000-0001-000000000001")
                .borrowerId("00000000-0000-0000-0000-000000000001")
                .build();
        var book = Book.create(
                BookId.fromString("00000000-0000-0000-0001-000000000001"),
                Isbn13.of("9784774153773"),
                Title.of("JUnit実践入門")
        );
        List<Book> borrowBooks = mock(List.class);
        var borrower = Borrower.builder()
                .id(UserId.fromString("00000000-0000-0000-0000-000000000001"))
                .name(Name.of("user"))
                .role(Role.GeneralUser)
                .borrowBooks(borrowBooks)
                .build();

        doReturn(5).when(borrowBooks).size();
        doReturn(Optional.of(book)).when(bookRepository).findById(book.getId());
        doReturn(Optional.of(borrower)).when(borrowerRepository).findById(borrower.getId());

        var exception = assertThrows(
                IllegalStateException.class,
                () -> sut.handle(borrowBookInputData)
        );

        assertAll(
                () -> assertEquals("borrowed books must be up to 5", exception.getMessage()),
                () -> verify(bookRepository, never()).save(any())
        );
    }
}