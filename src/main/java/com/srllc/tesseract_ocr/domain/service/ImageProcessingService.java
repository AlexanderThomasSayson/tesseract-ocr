package com.srllc.tesseract_ocr.domain.service;

import com.srllc.tesseract_ocr.domain.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageProcessingService {

    ImageDto processAndSaveImage(MultipartFile multipartFile);

    ImageDto getImageById(Long id);
}
