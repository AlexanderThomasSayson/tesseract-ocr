package com.srllc.tesseract_ocr.common.utils;

import com.srllc.tesseract_ocr.domain.entity.Image;
import com.srllc.tesseract_ocr.domain.exception.FailedToLoadImageException;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
@Slf4j
public class ImageProcessingHelper {

    private final Tesseract tesseract;

    public ImageProcessingHelper(Tesseract tesseract) {
        this.tesseract = tesseract;
    }

    /**
     * Create a temporary file with the given byte array
     */
    public Path createTempFile(byte[] imageBytes, String prefix, String suffix) throws IOException {
        log.info("Creating temporary file with prefix: {} and suffix: {}", prefix, suffix);
        Path tempFile = Files.createTempFile(prefix, suffix);
        Files.write(tempFile, imageBytes, StandardOpenOption.CREATE);
        log.debug("Temporary file created at: {}", tempFile.toAbsolutePath());
        return tempFile;
    }

    /**
     * Process the image using OpenCV
     */
    public byte[] processImage(Path inputFile) throws IOException {
        log.info("Processing image: {}", inputFile.toAbsolutePath());

        Mat src = Imgcodecs.imread(inputFile.toString());
        if (src.empty()) {
            log.error("Failed to load image from: {}", inputFile.toAbsolutePath());
            throw new FailedToLoadImageException("Failed to load image");
        }

        log.info("Converting image to grayscale...");
        Mat grayImage = new Mat();
        Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY);

        log.info("Applying adaptive thresholding...");
        Mat binaryImage = new Mat();
        Imgproc.adaptiveThreshold(grayImage, binaryImage, 255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY, 11, 2);

        log.info("Compressing image with lossless PNG compression...");
        MatOfInt compressionParams = new MatOfInt(Imgcodecs.IMWRITE_PNG_COMPRESSION, 9);

        log.info("Creating temporary file for the compressed processed image...");
        Path tempOutputFile = Files.createTempFile("compressed_", ".png");
        Imgcodecs.imwrite(tempOutputFile.toString(), binaryImage, compressionParams);

        log.info("Reading compressed image bytes from: {}", tempOutputFile.toAbsolutePath());
        byte[] compressedImage = Files.readAllBytes(tempOutputFile);
        Files.delete(tempOutputFile);
        log.debug("Temporary compressed image file deleted: {}", tempOutputFile.toAbsolutePath());

        return compressedImage;
    }


    /**
     * Extract text from the processed image using Tesseract OCR
     */
    public String extractText(Path imageFile) throws TesseractException {
        log.info("Extracting text from image: {}", imageFile.toAbsolutePath());
        String extractedText = tesseract.doOCR(imageFile.toFile());
        log.debug("Extracted text: {}", extractedText);
        return extractedText;
    }

    /**
     * Create Image entity with processing details
     */
    public Image createImageEntity(byte[] originalImage, byte[] processedImage, String extractedText) {
        log.info("Creating Image entity with extracted text length: {} characters", extractedText.length());
        Image image = new Image();
        image.setOriginalImage(originalImage);
        image.setProcessedImage(processedImage);
        image.setExtractedText(extractedText);
        log.debug("Image entity created: {}", image);
        return image;
    }
}
