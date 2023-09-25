package io.github.douglasliebl.msbooks.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        Contact contact = new Contact();
        contact.setName("Douglas Liebl");
        contact.setEmail("douglasliebl@outlook.com");
        contact.setUrl("https://github.com/DouglasLiebl");

        Info info = new Info()
                .title("Library-API")
                .description("Book rental API control project.")
                .version("1.0.0")
                .contact(contact);

        return new OpenAPI()
                .info(info);
    }
}
