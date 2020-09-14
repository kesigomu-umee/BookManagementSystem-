package example.training.bookmanagement.application.usecases.createuser;

import example.training.bookmanagement.application.usecases.Usecase;
import example.training.bookmanagement.domain.model.useraggregate.Name;
import example.training.bookmanagement.domain.model.useraggregate.Password;
import example.training.bookmanagement.domain.model.useraggregate.Role;
import example.training.bookmanagement.domain.model.useraggregate.User;
import example.training.bookmanagement.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateUserUsecaseInteractor implements Usecase<CreateUserInputData, User> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUsecaseInteractor(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User handle(CreateUserInputData inputData) {
        Name name = Name.of(inputData.getName());
        if (userRepository.existByName(name)) {
            throw new IllegalArgumentException("name already exists");
        }

        User user = User.builder()
                .id(userRepository.generateId())
                .name(name)
                .role(Role.valueOf(inputData.getRole()))
                .password(Password.of(passwordEncoder.encode(inputData.getPassword())))
                .build();
        userRepository.save(user);
        return user;
    }
}
