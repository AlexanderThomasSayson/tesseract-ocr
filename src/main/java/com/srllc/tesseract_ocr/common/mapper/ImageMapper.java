package com.srllc.tesseract_ocr.common.mapper;

import com.srllc.tesseract_ocr.domain.dto.ImageDto;
import com.srllc.tesseract_ocr.domain.entity.Image;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class ImageMapper {

    public ImageDto mapToDTO(Image image) {
        ImageDto dto = new ImageDto();
        dto.setId(image.getId());
        dto.setExtractedText(image.getExtractedText());

        if (image.getOriginalImage() != null) {
            dto.setOriginalImageBase64(Base64.getEncoder().encodeToString(image.getOriginalImage()));
        }
        if (image.getProcessedImage() != null) {
            dto.setProcessedImageBase64(Base64.getEncoder().encodeToString(image.getProcessedImage()));
        }

        return dto;
    }

    public Image mapToEntity(ImageDto imageDto) {
        Image image = new Image();
        image.setId(imageDto.getId());
        image.setExtractedText(imageDto.getExtractedText());

        if (imageDto.getOriginalImageBase64() != null) {
            image.setOriginalImage(Base64.getDecoder().decode(imageDto.getOriginalImageBase64()));
        }
        if (imageDto.getProcessedImageBase64() != null) {
            image.setProcessedImage(Base64.getDecoder().decode(imageDto.getProcessedImageBase64()));
        }

        return image;
    }
}
