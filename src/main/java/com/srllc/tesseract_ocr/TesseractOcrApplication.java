package com.srllc.tesseract_ocr;

import com.srllc.tesseract_ocr.utils.OpenCVLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TesseractOcrApplication {

	public static void main(String[] args) {
		OpenCVLoader.loadOpenCV();

		SpringApplication.run(TesseractOcrApplication.class, args);
	}

}
