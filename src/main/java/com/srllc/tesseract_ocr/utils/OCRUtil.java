package com.srllc.tesseract_ocr.utils;

import com.srllc.tesseract_ocr.exception.FileUploadException;
import com.srllc.tesseract_ocr.exception.ResourceNotFoundException;
import com.srllc.tesseract_ocr.service.OCRService;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;

@Slf4j
public class OCRUtil {

    public static String extractText(OCRService ocrService, String imagePath) {
        try {
            String extractedText = ocrService.extractText(imagePath);
            log.info("Text extracted successfully.");
            return extractedText;
        } catch (TesseractException e) {
            log.error("OCR processing failed for: {}", imagePath, e);
            throw new FileUploadException("OCR processing failed for: " + imagePath, e);
        }
    }

    public static String extractTicketNumber(OCRService ocrService, String text) {
        String ticketNo = ocrService.extractTicketNumber(text);
        if (ticketNo == null) {
            log.error("Ticket number not found in extracted text!");
            throw new ResourceNotFoundException("Ticket number not found in extracted text!");
        }
        log.info("Ticket number extracted: {}", ticketNo);
        return ticketNo;
    }
}
