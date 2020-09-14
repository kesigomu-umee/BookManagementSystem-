package example.training.bookmanagement.presentation.config;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import example.training.bookmanagement.presentation.authentication.SimpleLoginUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_NAME = "JWT";
    private static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";
    private static final String LOGOUT_PATH = "/logout";

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(apiKey()))
                .ignoredParameterTypes(SimpleLoginUser.class);
    }

    private ApiKey apiKey() {
        return new ApiKey(TOKEN_NAME, AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(Predicates.or(
                        PathSelectors.regex(DEFAULT_INCLUDE_PATTERN),
                        PathSelectors.regex(LOGOUT_PATH))
                )
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("auth", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference(TOKEN_NAME, authorizationScopes));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("BookManagement API")
                .description("書籍管理システムのWebAPI")
                .version("0.0.1")
                .license("none")
                .licenseUrl("none")
                .build();
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .displayRequestDuration(true)
                .validatorUrl("")
                .build();
    }
}
