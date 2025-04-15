package com.srllc.tesseract_ocr.domain.service;

import com.srllc.tesseract_ocr.domain.dto.VoucherRedemptionFormDTO;
import org.springframework.web.multipart.MultipartFile;

public interface VoucherRedemptionFromService {

    VoucherRedemptionFormDTO processVoucher(MultipartFile multipartFile);

    byte[] getOriginalImage(Long id);

    byte[] getProcessedImage(Long id);
}
