package example.training.bookmanagement.presentation.controller;

import example.training.bookmanagement.application.usecases.Usecase;
import example.training.bookmanagement.application.usecases.borrowbook.BorrowBookInputData;
import example.training.bookmanagement.application.usecases.createbook.CreateBookInputData;
import example.training.bookmanagement.application.usecases.findbook.FindBookInputData;
import example.training.bookmanagement.application.usecases.givebackbook.GiveBackBookInputData;
import example.training.bookmanagement.domain.model.bookaggregate.Book;
import example.training.bookmanagement.presentation.authentication.SimpleLoginUser;
import example.training.bookmanagement.presentation.dto.BookDto;
import example.training.bookmanagement.presentation.form.CreateBookForm;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Optional;

@RequestMapping("/api/books")
@RestController
@Validated
public class BookController {
    private final Usecase<CreateBookInputData, Book> createBookUsecase;
    private final Usecase<FindBookInputData, Optional<Book>> findBookUsecase;
    private final Usecase<BorrowBookInputData, Void> borrowBookUsecase;
    private final Usecase<GiveBackBookInputData, Void> giveBackBookUsecase;

    public BookController(
            Usecase<CreateBookInputData, Book> createBookUsecase,
            Usecase<FindBookInputData, Optional<Book>> findBookUsecase,
            Usecase<BorrowBookInputData, Void> borrowBookUsecase,
            Usecase<GiveBackBookInputData, Void> giveBackBookUsecase) {
        this.createBookUsecase = createBookUsecase;
        this.findBookUsecase = findBookUsecase;
        this.borrowBookUsecase = borrowBookUsecase;
        this.giveBackBookUsecase = giveBackBookUsecase;
    }

    @GetMapping("/{bookId}")
    @Secured("ROLE_GeneralUser")
    public BookDto findBook(
            @AuthenticationPrincipal SimpleLoginUser simpleLoginUser,
            @PathVariable("bookId") @Valid @Size(min = 36, max = 36) String bookId) {
        FindBookInputData findBookInputData = FindBookInputData.builder()
                .bookId(bookId)
                .build();
        Book book = findBookUsecase.handle(findBookInputData).orElseThrow();
        return BookDto.fromModel(book);
    }

    @PostMapping("")
    @Secured("ROLE_Administrator")
    public BookDto createBook(
            @AuthenticationPrincipal SimpleLoginUser simpleLoginUser,
            @RequestBody @Valid CreateBookForm createBookForm) {
        CreateBookInputData createBookInputData = CreateBookInputData.builder()
                .isbn13(createBookForm.getIsbn13())
                .title(createBookForm.getTitle())
                .build();
        Book book = createBookUsecase.handle(createBookInputData);
        return BookDto.fromModel(book);
    }

    @PutMapping("/{bookId}/borrow")
    @Secured("ROLE_GeneralUser")
    public void borrowBook(
            @AuthenticationPrincipal SimpleLoginUser simpleLoginUser,
            @PathVariable("bookId") @Valid @Size(min = 36, max = 36) String bookId) {
        BorrowBookInputData borrowBookInputData = BorrowBookInputData.builder()
                .borrowerId(simpleLoginUser.getUserId())
                .bookId(bookId)
                .build();
        borrowBookUsecase.handle(borrowBookInputData);
    }

    @PutMapping("/{bookId}/giveback")
    @Secured("ROLE_GeneralUser")
    public void giveBackBook(
            @AuthenticationPrincipal SimpleLoginUser simpleLoginUser,
            @PathVariable("bookId") @Valid @Size(min = 36, max = 36) String bookId) {
        GiveBackBookInputData giveBackBookInputData = GiveBackBookInputData.builder()
                .borrowerId(simpleLoginUser.getUserId())
                .bookId(bookId)
                .build();
        giveBackBookUsecase.handle(giveBackBookInputData);
    }
}
