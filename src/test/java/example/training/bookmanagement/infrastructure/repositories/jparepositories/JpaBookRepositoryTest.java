package example.training.bookmanagement.infrastructure.repositories.jparepositories;

import example.training.bookmanagement.domain.model.bookaggregate.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({JpaBookRepository.class})
@TestPropertySource(properties = {
        "spring.datasource.initialization-mode=never"
})
class JpaBookRepositoryTest {
    @Autowired
    JpaBookRepository sut;

    @Test
    @Sql("/infrastructure/repositories/jparepositories/FindBook.sql")
    void 書籍IDから書籍を取得できる() {
        var expectBook = Book.builder()
                .id(BookId.fromString("00000000-0000-0000-0001-000000000001"))
                .isbn13(Isbn13.of("9784774153773"))
                .title(Title.of("JUnit実践入門"))
                .borrowerId(null)
                .status(BookStatus.Lendable)
                .build();

        var book = sut.findById(BookId.fromString("00000000-0000-0000-0001-000000000001")).orElseThrow();

        Assertions.assertEquals(expectBook, book);
    }

    @Test
    void 存在しない書籍IDを取得するとOptional_emptyが返される() {
        var result = sut.findById(BookId.fromString("00000000-0000-0000-0001-000000000001"));

        assertEquals(Optional.empty(), result);
    }

    @Test
    void 書籍を永続化することができる() {
        var bookId = BookId.fromString("00000000-0000-0000-0001-000000000001");
        var book = Book.builder()
                .id(BookId.fromString("00000000-0000-0000-0001-000000000001"))
                .isbn13(Isbn13.of("9784774153773"))
                .title(Title.of("JUnit実践入門"))
                .borrowerId(null)
                .status(BookStatus.Lendable)
                .build();

        sut.save(book);

        var savedBook = sut.findById(bookId).orElseThrow();
        Assertions.assertEquals(book, savedBook);
    }
}