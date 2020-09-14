package example.training.bookmanagement.presentation.dto;

import example.training.bookmanagement.domain.model.useraggregate.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class UserDto {
    private String id;
    private String name;
    private String role;

    static public UserDto fromModel(User user){
        return UserDto.builder()
                .id(user.getId().toString())
                .name(user.getName().toString())
                .role(user.getRole().toString())
                .build();
    }
}
