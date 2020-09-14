package example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway.jpamodel;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class UserJpaModel {
    @Id
    @Column(length = 36)
    private String id;

    @Column(length = 32, unique=true)
    private String name;

    @Column(length = 256)
    private String password;

    @Column(length = 32)
    private String role;

    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL)
    @Column(length = 32)
    private List<BookJpaModel> borrowBooks;
}
