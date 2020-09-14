package example.training.bookmanagement.presentation.controller;

import example.training.bookmanagement.application.usecases.createuser.CreateUserInputData;
import example.training.bookmanagement.application.usecases.createuser.CreateUserUsecaseInteractor;
import example.training.bookmanagement.domain.model.useraggregate.*;
import example.training.bookmanagement.presentation.authentication.LoginUserDetailsService;
import example.training.bookmanagement.presentation.authentication.SimpleAuthenticationSuccessHandler;
import example.training.bookmanagement.presentation.authentication.SimpleLoginUser;
import example.training.bookmanagement.presentation.authentication.SimpleTokenFilter;
import example.training.bookmanagement.presentation.config.SecurityConfig;
import example.training.bookmanagement.presentation.config.SpringSecurityConfig;
import example.training.bookmanagement.presentation.handler.ConstraintViolationExceptionHandler;
import org.junit.jupiter.api.Tag;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringSecurityConfig.class, SecurityConfig.class})
@Import({UserController.class, SimpleTokenFilter.class, SimpleAuthenticationSuccessHandler.class, ConstraintViolationExceptionHandler.class})
@WebMvcTest(value = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateUserUsecaseInteractor createUserUsecaseInteractor;

    @MockBean
    private LoginUserDetailsService loginUserDetailsService;

    @Test
    void 管理ユーザはユーザー登録を呼び出しできる() throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser("00000000-0000-0000-0000-000000000001", "loginUser", "hashedpassword", "Administrator");
        var inputJson = "{ \"name\": \"user\", \"password\": \"password\", \"role\": \"GeneralUser\" }";
        var createUserInputData = CreateUserInputData.builder()
                .name("user")
                .password("password")
                .role("GeneralUser")
                .build();
        var user = User.builder()
                .id(UserId.fromString("00000000-0000-0000-0000-000000000002"))
                .name(Name.of("user"))
                .password(Password.of("encodedpassword"))
                .role(Role.GeneralUser)
                .build();
        var expectJson = "{ \"id\": \"00000000-0000-0000-0000-000000000002\", \"name\": \"user\", \"role\": \"GeneralUser\"}";

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);
        doReturn(user).when(createUserUsecaseInteractor).handle(createUserInputData);

        MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));
        mockMvc.perform(
                post("/api/users")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MEDIA_TYPE_JSON_UTF8)
                        .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectJson, true));
        verify(createUserUsecaseInteractor, times(1)).handle(createUserInputData);
    }

    @Test
    void 一般ユーザがユーザー登録を実行するとステータスコード403が返却される() throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser("00000000-0000-0000-0000-000000000001", "loginUser", "hashedpassword", "GeneralUser");
        var inputJson = "{ \"name\": \"user\", \"password\": \"password\", \"role\": \"GeneralUser\" }";

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);

        MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));
        mockMvc.perform(
                post("/api/users")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MEDIA_TYPE_JSON_UTF8)
                        .content(inputJson))
                .andExpect(status().isForbidden());
        verify(createUserUsecaseInteractor, never()).handle(any(CreateUserInputData.class));
    }

    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/presentation/controller/CreateUserValidationPass.csv", numLinesToSkip = 1)
    void ユーザー登録時にバリデーションに成功する(String comment, String inputJson) throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser("00000000-0000-0000-0000-000000000001", "loginUser", "hashedpassword", "Administrator");
        var user = User.builder()
                .id(UserId.fromString("00000000-0000-0000-0000-000000000001"))
                .name(Name.of("dummyuser"))
                .password(Password.of("encodedpassword"))
                .role(Role.GeneralUser)
                .build();

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);
        doReturn(user).when(createUserUsecaseInteractor).handle(any(CreateUserInputData.class));

        MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));
        mockMvc.perform(
                post("/api/users")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MEDIA_TYPE_JSON_UTF8)
                        .content(inputJson))
                .andExpect(status().isOk());
        verify(createUserUsecaseInteractor, times(1)).handle(any(CreateUserInputData.class));
    }

    @Tag("異常系")
    @ParameterizedTest(name = "{0}")
    @CsvFileSource(resources = "/presentation/controller/CreateUserValidationFail.csv", numLinesToSkip = 1)
    void ユーザー登録時にバリデーションに失敗するとステータスコード400が返却される(String comment, String inputJson) throws Exception {
        var authorization = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        var loginUser = new SimpleLoginUser("00000000-0000-0000-0000-000000000001", "loginUser", "hashedpassword", "Administrator");

        doReturn(loginUser).when(loginUserDetailsService).loadUserByUsername(loginUser.getUsername());
        doReturn(Optional.of(loginUser)).when(loginUserDetailsService).loadUserAndAuthenticationByToken(authorization);

        MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));
        mockMvc.perform(
                post("/api/users")
                        .header("Authorization", "Bearer " + authorization)
                        .contentType(MEDIA_TYPE_JSON_UTF8)
                        .content(inputJson))
                .andExpect(status().isBadRequest());
    }
}