package example.training.bookmanagement.presentation.authentication;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface LoginUserDetailsService extends UserDetailsService {
    Optional<SimpleLoginUser> loadUserAndAuthenticationByToken(String token);
}
