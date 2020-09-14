package example.training.bookmanagement.application.usecases.givebackbook;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GiveBackBookInputData {
    private String bookId;
    private String borrowerId;
}
