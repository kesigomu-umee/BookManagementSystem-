package example.training.bookmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = "example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway.jpamodel")
@EnableJpaRepositories("example.training.bookmanagement.infrastructure.repositories.gateway.jpagateway")
@EnableTransactionManagement
public class BookManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookManagementSystemApplication.class, args);
    }
}
