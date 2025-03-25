package com.srllc.tesseract_ocr.service.impl;

import com.srllc.tesseract_ocr.dao.ImageDao;
import com.srllc.tesseract_ocr.dto.ImageDto;
import com.srllc.tesseract_ocr.entity.Image;
import com.srllc.tesseract_ocr.exception.ResourceNotFoundException;
import com.srllc.tesseract_ocr.service.ImageProcessingService;
import com.srllc.tesseract_ocr.utils.mapper.ImageMapper;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {

    private final ImageDao imageDao;
    private final ImageMapper imageMapper;
    private final Tesseract tesseract;

    public ImageProcessingServiceImpl(ImageDao imageDao, ImageMapper imageMapper, Tesseract tesseract) {
        this.imageDao = imageDao;
        this.imageMapper = imageMapper;
        this.tesseract = tesseract;
    }

    @Override
    public ImageDto processAndSaveImage(MultipartFile multipartFile) {
        try {
            // Load OpenCV native library (ensure this is done once in your application startup)
            nu.pattern.OpenCV.loadShared();

            // Convert MultipartFile to byte array
            byte[] originalImage = multipartFile.getBytes();

            // Create a temporary file to work with OpenCV
            Path tempInputFile = Files.createTempFile("original_", ".png");
            Files.write(tempInputFile, originalImage, StandardOpenOption.CREATE);

            // Read the image using OpenCV
            Mat src = Imgcodecs.imread(tempInputFile.toString());

            // Check if image is loaded successfully
            if (src.empty()) {
                throw new RuntimeException("Failed to load image");
            }

            // Convert to grayscale
            Mat grayImage = new Mat();
            Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY);

            // Apply Gaussian blur
            Mat blurredImage = new Mat();
            Imgproc.GaussianBlur(grayImage, blurredImage, new Size(3, 3), 0);

            // Create temporary file for processed image
            Path tempOutputFile = Files.createTempFile("processed_", ".png");
            Imgcodecs.imwrite(tempOutputFile.toString(), blurredImage);

            // Read processed image bytes
            byte[] processedImage = Files.readAllBytes(tempOutputFile);

            // Extract text using Tesseract OCR
            String extractedText = tesseract.doOCR(tempOutputFile.toFile());

            // Clean up temporary files
            Files.delete(tempInputFile);
            Files.delete(tempOutputFile);

            // Save image data to database
            Image image = new Image();
            image.setOriginalImage(originalImage);
            image.setProcessedImage(processedImage);
            image.setExtractedText(extractedText);

            Image savedImage = imageDao.save(image);
            return imageMapper.mapToDTO(savedImage);

        } catch (IOException | TesseractException e) {
            throw new RuntimeException("Error processing image: " + e.getMessage(), e);
        }
    }

    @Override
    public ImageDto getImageById(Long id) {
        Image image = imageDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image with ID: " + id + " not found!"));
        return imageMapper.mapToDTO(image);
    }

}

