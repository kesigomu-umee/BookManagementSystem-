package example.training.bookmanagement.domain.repositories;

import example.training.bookmanagement.domain.model.borroweraggregate.Borrower;
import example.training.bookmanagement.domain.model.useraggregate.UserId;

import java.util.Optional;

public interface BorrowerRepository {
    Optional<Borrower> findById(UserId userId);
}
