package com.srllc.tesseract_ocr.service;

import com.srllc.tesseract_ocr.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageProcessingService {

    ImageDto processAndSaveImage(MultipartFile multipartFile);

    ImageDto getImageById(Long id);
}
