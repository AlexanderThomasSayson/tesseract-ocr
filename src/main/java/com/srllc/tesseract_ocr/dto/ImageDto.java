package com.srllc.tesseract_ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
    private Long id;
    private String extractedText;
    private String originalImageBase64;
    private String processedImageBase64;
}
