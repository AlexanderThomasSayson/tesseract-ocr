package com.srllc.tesseract_ocr.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 * <p>
 * This class sets up the OpenAPI specification for the application,
 * including metadata such as the API title, description, version, and contact information.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Defines the OpenAPI configuration for the application.
     * <p>
     * Initializes and returns an {@link OpenAPI} instance containing
     * metadata including the API title, version, description, and contact details.
     * </p>
     *
     * @return an {@link OpenAPI} instance with customized API documentation settings
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Voucher and Redemption Form Text Extraction API v1.0.0")
                        .version("1.0")
                        .description("""
                                API documentation for the Voucher and Voucher Redemption Form Text Extraction 
                                service, utilizing AWS Textract and Optical Character Recognition (OCR).
                                
                                **Developer**
                                - Alexander Thomas Sayson
                                """)
                        .contact(new Contact()
                                .name("Alexander Thomas Sayson")
                                .email("alexanderthomassayson@gmail.com")));
    }
}
