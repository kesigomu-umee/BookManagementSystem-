package example.training.bookmanagement.application.usecases.createuser;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateUserInputData {
    private String name;
    private String role;
    private String password;
}
