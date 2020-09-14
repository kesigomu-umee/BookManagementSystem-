package example.training.bookmanagement.presentation.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CreateBookForm {
    @Size(min = 13, max = 13)
    @Pattern(regexp = "\\d+")
    private String isbn13;

    @Size(max = 32)
    @NotBlank
    private String title;
}
