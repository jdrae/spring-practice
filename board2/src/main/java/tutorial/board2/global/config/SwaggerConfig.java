package tutorial.board2.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration
@Import(BeanValidatorPluginsConfiguration.class) // bean validation 문서화를 위해
public class SwaggerConfig {

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select() // api 문서를 작성할 패키지 지정
                .apis(RequestHandlerSelectors.basePackage("tutorial.board2.domain.account.controller"))
                .paths(PathSelectors.any())
                .build()
                // 요청에 포함할 Authorization 헤더를 전역으로 지정해서 사용
                .securitySchemes(List.of(apiKey()))
                .securityContexts(List.of(securityContext()));
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Board2 Test")
                .description("Board2 Test Rest API Documentation")
                .version("1.0")
                .build();
    }

    private static ApiKey apiKey(){
        return new ApiKey("Authorization", "Bearer Token", "header");
    }

    private List<SecurityReference> defaultAuth(){
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "global access");
        return List.of(new SecurityReference("Authorization", new AuthorizationScope[]{authorizationScope}));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .operationSelector(oc -> oc.requestMappingPattern().startsWith("/api/")).build();
    }

}
