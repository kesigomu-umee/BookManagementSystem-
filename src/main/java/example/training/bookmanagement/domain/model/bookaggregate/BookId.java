package example.training.bookmanagement.domain.model.bookaggregate;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
public class BookId {
    private final UUID bookId;

    static public BookId fromString(String name) {
        return BookId.of(UUID.fromString(name));
    }

    @Override
    public String toString() {
        return bookId.toString();
    }
}
