package com.srllc.tesseract_ocr.domain.controller;

import com.srllc.tesseract_ocr.domain.service.POPOCRService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/pop-controller")
@Tag( name = "Proof Of Purchase Controller", description = "Operations for managing POP Validations.")
public class POPController {

    private final POPOCRService popocrService;

    public POPController(POPOCRService popocrService) {
        this.popocrService = popocrService;
    }


    @PostMapping(value = "/extract",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> extractText(@RequestPart("file") MultipartFile file) throws IOException {
        Path uploadDir = Paths.get("uploads");
        Files.createDirectories(uploadDir);

        Path uploadedFile = uploadDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        Files.copy(file.getInputStream(), uploadedFile, StandardCopyOption.REPLACE_EXISTING);

        String ocrText = popocrService.extractTextFromImage(uploadedFile.toFile());
        Map<String, Object> extractedData = popocrService.extractStructuredData(ocrText);

        return ResponseEntity.ok(extractedData);
    }
}
