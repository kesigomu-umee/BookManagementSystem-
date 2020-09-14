package example.training.bookmanagement.presentation.authentication;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SimpleTokenFilter extends GenericFilterBean {
    private final LoginUserDetailsService loginUserDetailsService;

    public SimpleTokenFilter(LoginUserDetailsService loginUserDetailsService) {
        this.loginUserDetailsService = loginUserDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String token = resolveToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            SimpleLoginUser simpleLoginUser = loginUserDetailsService.loadUserAndAuthenticationByToken(token)
                    .orElseThrow(() -> new UsernameNotFoundException("user not found by token"));
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(simpleLoginUser, null, simpleLoginUser.getAuthorities()));
        } catch (UsernameNotFoundException | JWTVerificationException e) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(ServletRequest request) {
        String token = ((HttpServletRequest) request).getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring(7);
    }
}
