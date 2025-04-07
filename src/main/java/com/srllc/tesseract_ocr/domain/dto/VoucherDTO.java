package com.srllc.tesseract_ocr.domain.dto;

import lombok.Data;

@Data
public class VoucherDTO {
    private Long id;
    private String originalText;
    private String ticketNo;
    private String amount;
    private String originalImageURL;
    private String processedImageURL;
    private String siNumber;
}

