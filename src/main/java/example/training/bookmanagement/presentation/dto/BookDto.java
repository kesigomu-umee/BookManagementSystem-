package example.training.bookmanagement.presentation.dto;

import example.training.bookmanagement.domain.model.bookaggregate.Book;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class BookDto {
    private String id;
    private String isbn13;
    private String title;

    static public BookDto fromModel(Book book){
         return BookDto.builder()
                .id(book.getId().toString())
                .isbn13(book.getIsbn13().map(isbn13 -> isbn13.toString()).orElse(null))
                .title(book.getTitle().toString())
                .build();
    }
}
