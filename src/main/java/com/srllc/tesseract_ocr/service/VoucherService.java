package com.srllc.tesseract_ocr.service;

import com.srllc.tesseract_ocr.dto.VoucherDTO;
import org.springframework.web.multipart.MultipartFile;

public interface VoucherService {

    VoucherDTO processVoucher(MultipartFile multipartFile);

    VoucherDTO getOriginalImage(Long id);

    VoucherDTO getProcessedImage(Long id);
}
