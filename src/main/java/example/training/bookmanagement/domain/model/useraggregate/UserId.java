package example.training.bookmanagement.domain.model.useraggregate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class UserId {
    private final UUID userId;

    static public UserId fromString(String name) {
        return UserId.of(UUID.fromString(name));
    }

    @Override
    public String toString() {
        return userId.toString();
    }
}
