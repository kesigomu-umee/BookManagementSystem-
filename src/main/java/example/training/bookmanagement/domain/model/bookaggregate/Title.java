package example.training.bookmanagement.domain.model.bookaggregate;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class Title {
    private final String title;

    @Override
    public String toString() {
        return title;
    }
}
