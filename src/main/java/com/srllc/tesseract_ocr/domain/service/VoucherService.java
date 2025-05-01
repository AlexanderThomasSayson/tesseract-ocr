package com.srllc.tesseract_ocr.domain.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VoucherService {
    List<String> extractTextFromFile(MultipartFile file) throws IOException;
}
