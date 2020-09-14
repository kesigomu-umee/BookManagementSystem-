package example.training.bookmanagement.application.usecases;

public interface Usecase<I, O> {
    O handle(I inputData);
}
