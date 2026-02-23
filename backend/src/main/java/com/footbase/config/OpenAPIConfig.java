package com.footbase.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI footBaseOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setEmail("footbase@example.com");
        contact.setName("FootBase API");

        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("FootBase API")
                .version("1.0.0")
                .contact(contact)
                .description("FootBase - Futbol Maç Yorumlama ve Oyuncu Puanlama Platformu API Dokümantasyonu")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}

