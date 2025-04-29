package com.srllc.tesseract_ocr.domain.service;

import java.io.File;
import java.util.Map;

public interface POPOCRService {
    String extractTextFromImage(File file);

    Map<String, Object> extractStructuredData(String text);
}
