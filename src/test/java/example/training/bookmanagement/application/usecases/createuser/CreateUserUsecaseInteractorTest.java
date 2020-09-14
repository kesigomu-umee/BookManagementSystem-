package example.training.bookmanagement.application.usecases.createuser;

import example.training.bookmanagement.domain.model.useraggregate.*;
import example.training.bookmanagement.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUsecaseInteractorTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    CreateUserUsecaseInteractor sut;

    @Test
    void 正常に登録できる() {
        CreateUserInputData createUserInputData = CreateUserInputData.builder()
                .name("user")
                .password("password")
                .role("GeneralUser")
                .build();
        UserId userId = UserId.fromString("00000000-0000-0000-0000-000000000001");

        User expectedUser = User.builder()
                .id(userId)
                .name(Name.of(createUserInputData.getName()))
                .password(Password.of("encodedPassword"))
                .role(Role.valueOf(createUserInputData.getRole()))
                .build();

        doReturn(false).when(userRepository).existByName(Name.of(createUserInputData.getName()));
        doReturn(userId).when(userRepository).generateId();
        doReturn(expectedUser.getPassword().toString()).when(passwordEncoder).encode(createUserInputData.getPassword());

        sut.handle(createUserInputData);

        verify(userRepository, times(1)).save(expectedUser);
    }

    @Test
    void 同一のユーザー名が存在する場合例外が発生する() {
        CreateUserInputData createUserInputData = CreateUserInputData.builder()
                .name("user")
                .password("password")
                .role("GeneralUser")
                .build();

        doReturn(true).when(userRepository).existByName(Name.of(createUserInputData.getName()));

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> sut.handle(createUserInputData)
        );
        assertAll(
                () -> assertEquals("name already exists", exception.getMessage()),
                () -> verify(userRepository, never()).save(any())
        );
    }
}
