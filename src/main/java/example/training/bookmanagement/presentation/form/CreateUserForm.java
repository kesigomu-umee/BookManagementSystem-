package example.training.bookmanagement.presentation.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CreateUserForm {
    @Size(max = 32)
    @NotBlank
    private String name;

    @Size(max = 32)
    @NotBlank
    private String password;

    @Pattern(regexp = "Administrator|GeneralUser")
    @NotBlank
    private String role;
}
