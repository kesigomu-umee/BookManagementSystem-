package example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway.jpamodel;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "book")
public class BookJpaModel {
    @Id
    @Column(length = 36)
    private String id;

    @Column(length = 13)
    private String isbn13;

    @Column(length = 32)
    private String title;

    @Column(length = 32)
    private String status;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private UserJpaModel borrower;
}
