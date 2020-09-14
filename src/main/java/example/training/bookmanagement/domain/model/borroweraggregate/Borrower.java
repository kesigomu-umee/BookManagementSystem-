package example.training.bookmanagement.domain.model.borroweraggregate;

import example.training.bookmanagement.domain.model.bookaggregate.Book;
import example.training.bookmanagement.domain.model.useraggregate.Name;
import example.training.bookmanagement.domain.model.useraggregate.Role;
import example.training.bookmanagement.domain.model.useraggregate.UserId;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
public class Borrower {
    @NonNull
    private final UserId id;
    @NonNull
    private final Name name;
    @NonNull
    private final Role role;
    @NonNull
    private List<Book> borrowBooks;
}
