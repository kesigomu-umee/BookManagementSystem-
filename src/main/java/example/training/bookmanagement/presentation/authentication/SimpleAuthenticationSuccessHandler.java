package example.training.bookmanagement.presentation.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import example.training.bookmanagement.presentation.config.SecurityConfig;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Component
public class SimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//    @Value("${security.secret-key}")
//    private String secretKey;
    //60minutes
    private static final Long EXPIRATION_TIME = 60L * 60L * 1000L;
    private final Algorithm algorithm;

    public SimpleAuthenticationSuccessHandler(SecurityConfig securityConfig) {
        Objects.requireNonNull(securityConfig.getSecretKey(), "secret key must be not null");
        this.algorithm = Algorithm.HMAC256(securityConfig.getSecretKey());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication auth) throws IOException, ServletException {
        if (response.isCommitted()) {
//            log.info("Response has already been committed.");
            return;
        }
        setToken(response, generateToken(auth));
        response.setStatus(HttpStatus.OK.value());
        clearAuthenticationAttributes(request);
    }

    private String generateToken(Authentication auth) {
        SimpleLoginUser loginUser = (SimpleLoginUser) auth.getPrincipal();
        Date issuedAt = new Date();
        Date notBefore = new Date(issuedAt.getTime());
        Date expiresAt = new Date(issuedAt.getTime() + EXPIRATION_TIME);
        String token = JWT.create()
                .withIssuedAt(issuedAt)
                .withNotBefore(notBefore)
                .withExpiresAt(expiresAt)
                .withSubject(loginUser.getUserId())
                .sign(this.algorithm);
//        log.debug("generate token : {}", token);
        return token;
    }

    private void setToken(HttpServletResponse response, String token) {
        response.setHeader("Authorization", String.format("Bearer %s", token));
    }

    /**
     * Removes temporary authentication-related data which may have been stored in the
     * session during the authentication process.
     */
    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
