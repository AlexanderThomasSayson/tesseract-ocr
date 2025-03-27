package com.srllc.tesseract_ocr.dto;

import lombok.Data;

@Data
public class VoucherDTO {
    private Long id;
    private String originalText;
    private String ticketNo;
    private String originalImageURL;
    private String processedImageURL;
}

