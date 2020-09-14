package example.training.bookmanagement.presentation.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import example.training.bookmanagement.presentation.config.SecurityConfig;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtLoginUserDetailsService implements LoginUserDetailsService {
    private final AuthenticationUserRepository authenticationUserRepository;
    private final Algorithm algorithm;

    public JwtLoginUserDetailsService(AuthenticationUserRepository authenticationUserRepository, SecurityConfig securityConfig) {
        this.authenticationUserRepository = authenticationUserRepository;
        this.algorithm = Algorithm.HMAC256(securityConfig.getSecretKey());
    }

    @Override
    public UserDetails loadUserByUsername(final String name) {
        return authenticationUserRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    private DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    @Override
    public Optional<SimpleLoginUser> loadUserAndAuthenticationByToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        String userId = jwt.getSubject();
        return authenticationUserRepository.findById(userId);
    }
}
