package com.srllc.tesseract_ocr.common.utils;

import com.srllc.tesseract_ocr.domain.exception.FileUploadException;
import com.srllc.tesseract_ocr.domain.exception.ResourceNotFoundException;
import com.srllc.tesseract_ocr.domain.service.OCRService;
import com.srllc.tesseract_ocr.domain.service.VoucherRedemptionFormOcrService;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;

@Slf4j
public class VoucherRedemptionFormOcrUtil {

    public static String extractText(VoucherRedemptionFormOcrService voucherRedemptionFormOcrService, String imagePath) {
        try {
            String extractedText = voucherRedemptionFormOcrService.extractText(imagePath);
            log.info("Text extracted successfully.");
            return extractedText;
        } catch (TesseractException e) {
            log.error("OCR processing failed for: {}", imagePath, e);
            throw new FileUploadException("OCR processing failed for: " + imagePath, e);
        }
    }

    public static String extractRetailerName(VoucherRedemptionFormOcrService voucherRedemptionFormOcrService, String text) {
        String retailerName = voucherRedemptionFormOcrService.extractRetailerName(text);
        if (retailerName == null) {
            log.error("Retailer name not found in extracted text!");
            throw new ResourceNotFoundException("Retailer name not found in extracted text!");
        }
        log.info("Retailer name extracted: {}", retailerName);
        return retailerName;
    }

    public static String extractAmount(OCRService ocrService, String text) {
        String amount = ocrService.extractAmount(text);
        if (amount == null) {
            log.error("Amount not found in extracted text!");
            throw new ResourceNotFoundException("Amount not found in extracted text!");
        }
        log.info("Amount extracted: {}", amount);
        return amount;
    }
}
