package com.srllc.tesseract_ocr.domain.service;

import com.srllc.tesseract_ocr.domain.dto.VoucherDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VoucherService {

    VoucherDTO processVoucher(MultipartFile multipartFile);

    byte[] getOriginalImage(Long id);

    byte[] getProcessedImage(Long id);

    List<VoucherDTO> processMultipleVouchers(MultipartFile multipartFile);
}
