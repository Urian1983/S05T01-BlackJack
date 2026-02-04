package it.academy.blackjack.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Sprint 5 Task 1 Documentation - Josep Julià Roca Blanco")
                        .version("0.1")
                        .description("Sprint 5 Task 1 Documentation")
                        .contact(new Contact()
                                .name("Josep Julià Roca Blanco")
                                .email("urianr@gmail.com")));
    }
}
