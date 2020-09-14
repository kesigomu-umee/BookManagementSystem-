package example.training.bookmanagement.presentation.authentication;

import example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway.UserJpaGateway;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthenticationUserRepository {
    private final UserJpaGateway userJpaGateway;

    public AuthenticationUserRepository(UserJpaGateway userJpaGateway) {
        this.userJpaGateway = userJpaGateway;
    }

    public Optional<SimpleLoginUser> findById(String id){
        return userJpaGateway.findById(id).map(SimpleLoginUser::createByUserJpaModel);
    }

    public Optional<SimpleLoginUser> findByName(String name){
        return userJpaGateway.findByName(name).map(SimpleLoginUser::createByUserJpaModel);
    }
}
