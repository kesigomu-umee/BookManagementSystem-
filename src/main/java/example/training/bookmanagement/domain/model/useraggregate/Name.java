package example.training.bookmanagement.domain.model.useraggregate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class Name {
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
