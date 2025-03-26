package com.srllc.tesseract_ocr.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfig {

    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();

         // path for the cloned tessdata.
         tesseract.setDatapath("src/main/resources/tessdata");
         // default language is eng, you can set any language you prefer.
         tesseract.setLanguage("eng");

        return tesseract;
    }
}
