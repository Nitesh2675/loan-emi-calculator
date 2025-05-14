package com.cg.loanemicalculator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI loanEmiApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Loan EMI Calculator API")
                        .version("v1.0")
                        .description("Endpoints for calculating EMIs and generating schedules")
                        .contact(new Contact()
                                .name("Abhinav Walia")
                                .email("abhinav.walia@yourdomain.com")));
    }
}
