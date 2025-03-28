package com.srllc.tesseract_ocr.domain.service.impl;

import com.srllc.tesseract_ocr.domain.dao.ImageDao;
import com.srllc.tesseract_ocr.domain.dto.ImageDto;
import com.srllc.tesseract_ocr.domain.entity.Image;
import com.srllc.tesseract_ocr.domain.exception.ImageProcessingException;
import com.srllc.tesseract_ocr.domain.exception.ResourceNotFoundException;
import com.srllc.tesseract_ocr.domain.service.ImageProcessingService;
import com.srllc.tesseract_ocr.common.utils.ImageProcessingHelper;
import com.srllc.tesseract_ocr.common.mapper.ImageMapper;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {

    private final ImageDao imageDao;
    private final ImageMapper imageMapper;
    private final ImageProcessingHelper imageProcessingHelper;


    public ImageProcessingServiceImpl(ImageDao imageDao,
                                      ImageMapper imageMapper,
                                      ImageProcessingHelper imageProcessingHelper) {
        this.imageDao = imageDao;
        this.imageMapper = imageMapper;
        this.imageProcessingHelper = imageProcessingHelper;
    }

    @Override
    public ImageDto processAndSaveImage(MultipartFile multipartFile) {
        try {
            // Initialize OpenCV
            nu.pattern.OpenCV.loadShared();

            // Convert MultipartFile to byte array and save original image
            byte[] originalImage = multipartFile.getBytes();

            // Create temporary file for image processing
            Path tempInputFile = imageProcessingHelper.createTempFile(originalImage, "original_", ".png");

            // Process the image
            byte[] processedImage = imageProcessingHelper.processImage(tempInputFile);

            // Extract text from processed image
            String extractedText = imageProcessingHelper.extractText(tempInputFile);

            // Clean up temporary files
            Files.delete(tempInputFile);

            // Save image data to database
            Image image = imageProcessingHelper.createImageEntity(originalImage, processedImage, extractedText);
            Image savedImage = imageDao.save(image);

            return imageMapper.mapToDTO(savedImage);

        } catch (IOException | TesseractException e) {
            throw new ImageProcessingException("Error processing image: " + e.getMessage(), e);
        }
    }

    @Override
    public ImageDto getImageById(Long id) {
        Image image = imageDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image with ID: " + id + " not found!"));
        return imageMapper.mapToDTO(image);
    }
}