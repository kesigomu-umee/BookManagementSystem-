package example.training.bookmanagement.presentation.config;


import example.training.bookmanagement.presentation.authentication.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled=true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    private final LoginUserDetailsService loginUserDetailsService;
    private final SimpleTokenFilter simpleTokenFilter;
    private final SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler;

    public SpringSecurityConfig(LoginUserDetailsService loginUserDetailsService, SimpleTokenFilter simpleTokenFilter, SimpleAuthenticationSuccessHandler simpleAuthenticationSuccessHandler) {
        this.loginUserDetailsService = loginUserDetailsService;
        this.simpleTokenFilter = simpleTokenFilter;
        this.simpleAuthenticationSuccessHandler = simpleAuthenticationSuccessHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // AUTHORIZE
                .authorizeRequests()
                .mvcMatchers("/logout")
                .authenticated()
//                .mvcMatchers("/api/books")
//                .hasRole(Role.Administrator.name())
//                .mvcMatchers("/api/books/**")
//                .hasRole(Role.GeneralUser.name())
//                .mvcMatchers("/api/users/**")
//                .hasRole(Role.Administrator.name())
                .anyRequest()
                .permitAll()
                .and()
                // EXCEPTION
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                // LOGIN
                .formLogin()
                .loginProcessingUrl("/login")
                .permitAll()
                .usernameParameter("name")
                .passwordParameter("password")
                .successHandler(simpleAuthenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler())
                .and()
                // LOGOUT
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .and()
                // CSRF
                .csrf()
                .disable()
                // AUTHORIZE
                .addFilterBefore(simpleTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // SESSION
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.eraseCredentials(true)
                .userDetailsService(loginUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    AuthenticationEntryPoint authenticationEntryPoint() {
        return new SimpleAuthenticationEntryPoint();
    }

    AccessDeniedHandler accessDeniedHandler() {
        return new SimpleAccessDeniedHandler();
    }

    AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleAuthenticationFailureHandler();
    }

    LogoutSuccessHandler logoutSuccessHandler() {
        return new HttpStatusReturningLogoutSuccessHandler();
    }
}
