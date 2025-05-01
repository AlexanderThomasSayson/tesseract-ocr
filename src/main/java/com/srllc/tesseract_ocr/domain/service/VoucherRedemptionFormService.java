package com.srllc.tesseract_ocr.domain.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface VoucherRedemptionFormService {

    List<Map<String, Object>> extractTablesFromFile(MultipartFile file)throws IOException;
}
