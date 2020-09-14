package example.training.bookmanagement.domain.model.useraggregate;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@EqualsAndHashCode
public class User {
    @NonNull
    private final UserId id;
    @NonNull
    private Name name;
    @NonNull
    private Role role;
    @NonNull
    private Password password;

}
