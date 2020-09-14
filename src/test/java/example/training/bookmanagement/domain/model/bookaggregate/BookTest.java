package example.training.bookmanagement.domain.model.bookaggregate;

import example.training.bookmanagement.domain.model.useraggregate.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {
    Book sut;

    @Test
    void 書籍を生成できる() {
        sut = Book.create(
                BookId.of(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                Isbn13.of("978-4-7741-5377-3"),
                Title.of("JUnit実践入門")
        );
        assertAll(
                () -> assertEquals(BookId.of(UUID.fromString("00000000-0000-0000-0000-000000000001")), sut.getId()),
                () -> assertEquals(Optional.of(Isbn13.of("978-4-7741-5377-3")), sut.getIsbn13()),
                () -> assertEquals(Title.of("JUnit実践入門"), sut.getTitle()),
                () -> assertEquals(Optional.empty(), sut.getBorrowerId()),
                () -> assertEquals(BookStatus.Lendable, sut.getStatus())
        );
    }

    @Nested
    class 生成後状態 {
        @BeforeEach
        void setup() {
            sut = Book.create(
                    BookId.of(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                    Isbn13.of("978-4-7741-5377-3"),
                    Title.of("JUnit実践入門")
            );
        }

        @Test
        void 書籍を貸出できる() {
            var borrowerId = UserId.of(UUID.fromString("00000000-0000-0000-0001-000000000001"));
            sut.lend(borrowerId);

            assertAll(
                    () -> assertEquals(Optional.of(borrowerId), sut.getBorrowerId()),
                    () -> assertEquals(BookStatus.InLending, sut.getStatus())
            );
        }

        @Test
        void 書籍を返却すると例外が発生する() {
            var borrowerId = UserId.of(UUID.fromString("00000000-0000-0000-0001-000000000001"));
            var exception = assertThrows(
                    IllegalStateException.class,
                    () -> sut.giveBack(borrowerId)
            );
            assertEquals("bookStatus must be InLending", exception.getMessage());
        }

        @Nested
        class 書籍貸出後状態 {
            UserId borrowerId = UserId.of(UUID.fromString("00000000-0000-0000-0001-000000000001"));

            @BeforeEach
            void setup() {
                sut.lend(borrowerId);
            }

            @Test
            void 書籍を返却できる() {
                sut.giveBack(borrowerId);

                assertAll(
                        () -> assertEquals(Optional.empty(), sut.getBorrowerId()),
                        () -> assertEquals(BookStatus.Lendable, sut.getStatus())
                );
            }

            @Test
            void 書籍を返却する際にユーザーが一致しないと例外が発生する() {
                var otherBorrowerId = UserId.of(UUID.fromString("00000000-0000-0000-0001-000000000002"));

                var exception = assertThrows(
                        IllegalArgumentException.class,
                        () -> sut.giveBack(otherBorrowerId)
                );
                assertEquals("borrowerId does not match", exception.getMessage());
            }

            @Test
            void 書籍を貸出すると例外が発生する() {
                var exception = assertThrows(
                        IllegalStateException.class,
                        () -> sut.lend(borrowerId)
                );
                assertEquals("bookStatus must be Lendable", exception.getMessage());
            }
        }
    }
}