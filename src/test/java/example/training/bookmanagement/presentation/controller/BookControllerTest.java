package example.training.bookmanagement.presentation.controller;

import example.training.bookmanagement.application.usecases.borrowbook.BorrowBookInputData;
import example.training.bookmanagement.application.usecases.borrowbook.BorrowBookUsecaseInteractor;
import example.training.bookmanagement.application.usecases.createbook.CreateBookInputData;
import example.training.bookmanagement.application.usecases.createbook.CreateBookUsecaseInteractor;
import example.training.bookmanagement.application.usecases.findbook.FindBookInputData;
import example.training.bookmanagement.application.usecases.findbook.FindBookUsecaseInteractor;
import example.training.bookmanagement.application.usecases.givebackbook.GiveBackBookInputData;
import example.training.bookmanagement.application.usecases.givebackbook.GiveBackBookUsecaseInteractor;
import example.training.bookmanagement.domain.model.bookaggregate.Book;
import example.training.bookmanagement.domain.model.bookaggregate.BookId;
import example.training.bookmanagement.domain.model.bookaggregate.Isbn13;
import example.training.bookmanagement.domain.model.bookaggregate.Title;
import example.training.bookmanagement.presentation.authentication.LoginUserDetailsService;
import example.training.bookmanagement.presentation.authentication.SimpleAuthenticationSuccessHandler;
import example.training.bookmanagement.presentation.authentication.SimpleLoginUser;
import example.training.bookmanagement.presentation.authentication.SimpleTokenFilter;
import example.training.bookmanagement.presentation.config.SecurityConfig;
import example.training.bookmanagement.presentation.config.SpringSecurityConfig;
import example.training.bookmanagement.presentation.handler.ConstraintViolationExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringSecurityConfig.class, SecurityConfig.class})
@Import({BookController.class, SimpleTokenFilter.class, SimpleAuthenticationSuccessHandler.class, ConstraintViolationExceptionHandler.class})
@WebMvcTest(value = BookController.class)
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FindBookUsecaseInteractor findBookUsecaseInteractor;

    @MockBean
    private CreateBookUsecaseInteractor createBookUsecaseInteractor;

    @MockBean
    private BorrowBookUsecaseInteractor borrowBookUsecaseInteractor;

    @MockBean
    private GiveBackBookUsecaseInteractor giveBackBookUsecaseInteractor;

    @MockBean
    private LoginUserDetailsService loginUserDetailsService;

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/presentation/controller/FindBook.csv", numLinesToSkip = 1)
    void 書籍情報を取得できる(String comment, String userid, String name, String password, String role) throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser(userid, name, password, role);
        var bookId = "00000000-0000-0000-0001-000000000001";
        var expectJson = "{\"id\":\"00000000-0000-0000-0001-000000000001\",\"isbn13\":\"9784774153773\",\"title\":\"JUnit実践入門\"}";

        var findBookInputData = FindBookInputData.builder()
                .bookId(bookId)
                .build();

        var book = Book.create(
                BookId.fromString(bookId),
                Isbn13.of("9784774153773"),
                Title.of("JUnit実践入門"));

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);
        doReturn(Optional.of(book)).when(findBookUsecaseInteractor).handle(findBookInputData);

        mockMvc.perform(
                get("/api/books/{0}", bookId)
                        .header("Authorization", "Bearer " + authorization))
                .andExpect(status().isOk())
                .andExpect(content().json(expectJson, true));
        verify(findBookUsecaseInteractor, times(1)).handle(findBookInputData);
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/presentation/controller/FindBookValidationFail.csv", numLinesToSkip = 1)
    void 書籍情報取得時にバリデーションに失敗するとステータスコード400が返却される(String comment, String bookId) throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser("00000000-0000-0000-0000-000000000001", "loginUser", "hashedpassword", "Administrator");

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);

        mockMvc.perform(
                get("/api/books/{0}", bookId)
                        .header("Authorization", "Bearer " + authorization))
                .andExpect(status().isBadRequest());
        verify(findBookUsecaseInteractor, never()).handle(any(FindBookInputData.class));
    }

    @Test
    void 管理者は書籍を登録できる() throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser("00000000-0000-0000-0000-000000000001", "loginUser", "hashedpassword", "Administrator");
        var inputJson = "{ \"isbn13\": \"9784774153773\", \"title\": \"JUnit実践入門\" }";
        var expectJson = "{\"id\":\"00000000-0000-0000-0001-000000000001\",\"isbn13\":\"9784774153773\",\"title\":\"JUnit実践入門\"}";
        var createBookInputData = CreateBookInputData.builder()
                .isbn13("9784774153773")
                .title("JUnit実践入門")
                .build();
        var bookId = BookId.fromString("00000000-0000-0000-0001-000000000001");
        var book = Book.create(bookId, Isbn13.of("9784774153773"), Title.of("JUnit実践入門"));

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);
        doReturn(book).when(createBookUsecaseInteractor).handle(createBookInputData);

        MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));
        mockMvc.perform(
                post("/api/books")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MEDIA_TYPE_JSON_UTF8)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectJson, true));
        verify(createBookUsecaseInteractor, times(1)).handle(createBookInputData);
    }

    @Test
    void 一般ユーザーが書籍を登録した場合ステータスコード403が返却される() throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser("00000000-0000-0000-0000-000000000001", "loginUser", "hashedpassword", "GeneralUser");
        var inputJson = "{ \"isbn13\": \"9784774153773\", \"title\": \"JUnit実践入門\" }";

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);

        MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));
        mockMvc.perform(
                post("/api/books")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MEDIA_TYPE_JSON_UTF8)
                        .content(inputJson))
                .andExpect(status().isForbidden());
        verify(createBookUsecaseInteractor, never()).handle(any());
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/presentation/controller/CreateBookValidationPass.csv", numLinesToSkip = 1)
    void 書籍を登録した場合バリデーションに成功する(String comment, String inputJson) throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser("00000000-0000-0000-0000-000000000001", "loginUser", "hashedpassword", "Administrator");
        var book = Book.create(BookId.fromString("00000000-0000-0000-0001-000000000001"), Isbn13.of("1234567890123"), Title.of("DummyBook"));

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);
        doReturn(book).when(createBookUsecaseInteractor).handle(any(CreateBookInputData.class));

        MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));
        mockMvc.perform(
                post("/api/books")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MEDIA_TYPE_JSON_UTF8)
                        .content(inputJson))
                .andExpect(status().isOk());
        verify(createBookUsecaseInteractor, times(1)).handle(any(CreateBookInputData.class));
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/presentation/controller/CreateBookValidationFail.csv", numLinesToSkip = 1)
    void 書籍を登録した場合バリデーションに失敗する(String comment, String inputJson) throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser("00000000-0000-0000-0000-000000000001", "loginUser", "hashedpassword", "Administrator");

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);

        MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));
        mockMvc.perform(
                post("/api/books")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MEDIA_TYPE_JSON_UTF8)
                        .content(inputJson))
                .andExpect(status().isBadRequest());
        verify(createBookUsecaseInteractor, never()).handle(any(CreateBookInputData.class));
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/presentation/controller/BorrowBook.csv", numLinesToSkip = 1)
    void 書籍貸出を実施できる(String comment, String userid, String name, String password, String role) throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser(userid, name, password, role);
        var bookId = "00000000-0000-0000-0001-000000000001";

        var borrowBookInputData = BorrowBookInputData.builder()
                .bookId(bookId)
                .borrowerId(userid)
                .build();

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);
        doNothing().when(borrowBookUsecaseInteractor).handle(borrowBookInputData);

        mockMvc.perform(
                put("/api/books/{0}/borrow", bookId)
                        .header("Authorization", "Bearer " + authorization))
                .andExpect(status().isOk());
        verify(borrowBookUsecaseInteractor, times(1)).handle(borrowBookInputData);
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/presentation/controller/BorrowBookValidationFail.csv", numLinesToSkip = 1)
    void 書籍貸出時にバリデーションに失敗する(String comment, String bookId) throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser("00000000-0000-0000-0000-000000000001", "loginUser", "hashedpassword", "GeneralUser");

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);

        mockMvc.perform(
                put("/api/books/{0}/borrow", bookId)
                        .header("Authorization", "Bearer " + authorization))
                .andExpect(status().isBadRequest());
        verify(borrowBookUsecaseInteractor, never()).handle(any(BorrowBookInputData.class));
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/presentation/controller/GiveBackBook.csv", numLinesToSkip = 1)
    void 書籍返却を実施できる(String comment, String userid, String name, String password, String role) throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser(userid, name, password, role);
        var bookId = "00000000-0000-0000-0001-000000000001";

        var giveBackBookInputData = GiveBackBookInputData.builder()
                .bookId(bookId)
                .borrowerId(userid)
                .build();

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);
        doNothing().when(giveBackBookUsecaseInteractor).handle(giveBackBookInputData);

        mockMvc.perform(
                put("/api/books/{0}/giveback", bookId)
                        .header("Authorization", "Bearer " + authorization))
                .andExpect(status().isOk());
        verify(giveBackBookUsecaseInteractor, times(1)).handle(giveBackBookInputData);
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/presentation/controller/GiveBackBookValidationFail.csv", numLinesToSkip = 1)
    void 書籍返却時にバリデーションに失敗する(String comment, String bookId) throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser("00000000-0000-0000-0000-000000000001", "loginUser", "hashedpassword", "GeneralUser");

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);

        mockMvc.perform(
                put("/api/books/{0}/giveback", bookId)
                        .header("Authorization", "Bearer " + authorization))
                .andExpect(status().isBadRequest());
        verify(giveBackBookUsecaseInteractor, never()).handle(any(GiveBackBookInputData.class));
    }
}
