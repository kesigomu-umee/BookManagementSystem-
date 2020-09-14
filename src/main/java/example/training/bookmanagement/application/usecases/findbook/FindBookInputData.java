package example.training.bookmanagement.application.usecases.findbook;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FindBookInputData {
    private String bookId;
}
