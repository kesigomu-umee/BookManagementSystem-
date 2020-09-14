package example.training.bookmanagement.domain.model.useraggregate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class Password {
    private final String password;

    @Override
    public String toString() {
        return password;
    }
}
