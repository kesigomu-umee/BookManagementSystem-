package example.training.bookmanagement.domain.repositories;

import example.training.bookmanagement.domain.model.useraggregate.Name;
import example.training.bookmanagement.domain.model.useraggregate.User;
import example.training.bookmanagement.domain.model.useraggregate.UserId;

public interface UserRepository {
    UserId generateId();

    boolean existByName(Name name);

    void save(User user);
}
