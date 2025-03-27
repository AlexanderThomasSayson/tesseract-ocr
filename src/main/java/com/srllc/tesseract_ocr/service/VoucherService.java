package com.srllc.tesseract_ocr.service;

import com.srllc.tesseract_ocr.dto.VoucherDTO;
import org.springframework.web.multipart.MultipartFile;

public interface VoucherService {

    VoucherDTO processVoucher(MultipartFile multipartFile);

    byte[] getOriginalImage(Long id);

    byte[] getProcessedImage(Long id);
}
