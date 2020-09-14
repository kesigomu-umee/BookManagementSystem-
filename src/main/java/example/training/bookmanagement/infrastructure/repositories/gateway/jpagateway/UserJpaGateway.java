package example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway;

import example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway.jpamodel.UserJpaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UserJpaGateway extends JpaRepository<UserJpaModel, String> {
    Optional<UserJpaModel> findByName(String name);

    boolean existsByName(String name);
}
