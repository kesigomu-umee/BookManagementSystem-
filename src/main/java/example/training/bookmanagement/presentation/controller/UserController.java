package example.training.bookmanagement.presentation.controller;

import example.training.bookmanagement.application.usecases.Usecase;
import example.training.bookmanagement.application.usecases.createuser.CreateUserInputData;
import example.training.bookmanagement.domain.model.useraggregate.User;
import example.training.bookmanagement.presentation.dto.UserDto;
import example.training.bookmanagement.presentation.form.CreateUserForm;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api/users")
@RestController
public class UserController {
    private final Usecase<CreateUserInputData, User> createUserUsecase;

    public UserController(Usecase<CreateUserInputData, User> createUserUsecase) {
        this.createUserUsecase = createUserUsecase;
    }

    @PostMapping("")
    @Secured("ROLE_Administrator")
    public UserDto createUser(@RequestBody @Valid CreateUserForm createUserForm) {
        CreateUserInputData createUserInputData = CreateUserInputData.builder()
                .name(createUserForm.getName())
                .password(createUserForm.getPassword())
                .role(createUserForm.getRole())
                .build();
        User user = createUserUsecase.handle(createUserInputData);
        return UserDto.fromModel(user);
    }
}
