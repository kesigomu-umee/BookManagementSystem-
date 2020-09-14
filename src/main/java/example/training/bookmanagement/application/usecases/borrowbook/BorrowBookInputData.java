package example.training.bookmanagement.application.usecases.borrowbook;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class BorrowBookInputData {
    @NonNull
    private String borrowerId;

    @NonNull
    private String bookId;
}
