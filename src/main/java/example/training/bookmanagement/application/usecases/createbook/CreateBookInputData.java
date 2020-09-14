package example.training.bookmanagement.application.usecases.createbook;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class CreateBookInputData {
    private String isbn13;
    @NonNull
    private String title;

    public Optional<String> getIsbn13() {
        return Optional.ofNullable(isbn13);
    }
}
