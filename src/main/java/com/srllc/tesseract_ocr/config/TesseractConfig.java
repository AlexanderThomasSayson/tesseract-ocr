package com.srllc.tesseract_ocr.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfig {

    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();

        // Optional: Set the tessdata path if needed
         tesseract.setDatapath("src/main/resources/tessdata");

        // Optional: Set the language if required (default is English)
         tesseract.setLanguage("eng");

        return tesseract;
    }
}
