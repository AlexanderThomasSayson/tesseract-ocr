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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
            byte[] originalImage = multipartFile.getBytes();

            // Read the image
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(originalImage));

            // Convert to grayscale
            BufferedImage gray = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = gray.createGraphics();
            g2d.drawImage(src, 0, 0, null);
            g2d.dispose();

            // Apply Gaussian blur
            float[] blurKernel = {
                    1 / 16f, 1 / 8f, 1 / 16f,
                    1 / 8f, 1 / 4f, 1 / 8f,
                    1 / 16f, 1 / 8f, 1 / 16f
            };
            Kernel kernel = new Kernel(3, 3, blurKernel);
            ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            BufferedImage blurredImage = op.filter(gray, null);

            // Convert processed image to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(blurredImage, "png", baos);
            byte[] processedImage = baos.toByteArray();

            // Save processed image temporarily for OCR
            Path tempFile = Files.createTempFile("processed_", ".png");
            Files.write(tempFile, processedImage);

            // Extract text using Tesseract OCR
            String extractedText = tesseract.doOCR(tempFile.toFile());

            // Clean up temporary file
            Files.delete(tempFile);

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

