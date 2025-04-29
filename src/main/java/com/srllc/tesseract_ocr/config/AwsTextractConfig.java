package com.srllc.tesseract_ocr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;

@Configuration
public class AwsTextractConfig {

    private final AwsConfig awsConfig;

    public AwsTextractConfig(AwsConfig awsConfig) {
        this.awsConfig = awsConfig;
    }

    @Bean
    public TextractClient textractClient() {
        return TextractClient.builder()
                .region(Region.of(awsConfig.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsConfig.getAccessKeyId(), awsConfig.getSecretAccessKey())
                ))
                .build();
    }
}
