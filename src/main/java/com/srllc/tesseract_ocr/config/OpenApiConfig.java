package com.srllc.tesseract_ocr.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (SWAGGER) documentation.
 * <p>
 * This class defines the OpenAPI configuration for the application,
 * providing API metadata such as title, description, version, license,
 * and external documentation.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI documentation for the application.
     * <p>
     * This method initializes an {@link OpenAPI} instance with
     * application metadata including title, description, version,
     * license information, security requirements, and external documentation links.
     * </p>
     *
     * @return An {@link OpenAPI} instance containing the API documentation configuration.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tesseracts Optical Character Recognition  API v1.0.0")
                        .version("1.0")
                        .description("""
                                API documentation for Tesseracts Optical Character Recognition (OCR).
                                
                                **Developer:**
                                - Alexander Thomas Sayson
                                """)
                        .contact(new Contact()
                                .name("Alexander Thomas Sayson")
                                .email("alexanderthomassayson@gmail.com")));
    }
}
