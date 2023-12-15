package pl.cm.mytms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MyTMS API")
                        .description("This application has been created as a recruitment task for fireTMS. " +
                                "It provides vehicles and trailers management functions.")
                        .version("1.0")
                );
    }
}