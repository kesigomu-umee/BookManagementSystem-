package example.training.bookmanagement.presentation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityConfig {
    private String secretKey;
}
