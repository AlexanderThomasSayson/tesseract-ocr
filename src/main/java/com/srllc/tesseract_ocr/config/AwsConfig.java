package com.srllc.tesseract_ocr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix ="aws")
public class AwsConfig {
    private String accessKeyId;
    private String secretAccessKey;
    private String region;
}
