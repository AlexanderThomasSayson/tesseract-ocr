package com.srllc.tesseract_ocr.utils;

import com.srllc.tesseract_ocr.entity.Image;
import com.srllc.tesseract_ocr.exception.FailedToLoadImageException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
public class ImageProcessingHelper {

    private final Tesseract tesseract;

    public ImageProcessingHelper(Tesseract tesseract) {
        this.tesseract = tesseract;
    }

    /**
     * Create a temporary file with the given byte array
     */
    public Path createTempFile(byte[] imageBytes, String prefix, String suffix) throws IOException {
        Path tempFile = Files.createTempFile(prefix, suffix);
        Files.write(tempFile, imageBytes, StandardOpenOption.CREATE);
        return tempFile;
    }

    /**
     * Process the image using OpenCV
     */
    public byte[] processImage(Path inputFile) throws IOException {
        // Read the image using OpenCV
        Mat src = Imgcodecs.imread(inputFile.toString());

        // Check if image is loaded successfully
        if (src.empty()) {
            throw new FailedToLoadImageException("Failed to load image");
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
        Files.delete(tempOutputFile);

        return processedImage;
    }

    /**
     * Extract text from the processed image using Tesseract OCR
     */
    public String extractText(Path imageFile) throws TesseractException {
        return tesseract.doOCR(imageFile.toFile());
    }

    /**
     * Create Image entity with processing details
     */
    public Image createImageEntity(byte[] originalImage, byte[] processedImage, String extractedText) {
        Image image = new Image();
        image.setOriginalImage(originalImage);
        image.setProcessedImage(processedImage);
        image.setExtractedText(extractedText);
        return image;
    }
}
