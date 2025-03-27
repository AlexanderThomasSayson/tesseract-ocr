package com.srllc.tesseract_ocr.service;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class OCRService {

    private final Tesseract tesseract;

    public OCRService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("eng");
    }

    public String extractText(String imagePath) throws TesseractException {
        try {
            log.info("Extracting text from image: {}", imagePath);
            return tesseract.doOCR(new File(imagePath));
        } catch (TesseractException e) {
            log.error("Error extracting text from image: {}", imagePath, e);
            throw e;
        }
    }

    public String extractTicketNumber(String text) {
        log.info("Extracting ticket number from text.");
        Pattern pattern = Pattern.compile("MHS\\s+(\\d+)");
        Matcher matcher = pattern.matcher(text);
        log.debug("Attempting to extract ticket number from text: {}", text);
        if (matcher.find()) {
            String ticketNumber = matcher.group(1);
            log.info("Ticket number extracted: {}", ticketNumber);
            return ticketNumber;
        } else {
            log.warn("No ticket number found in extracted text.");
            return null;
        }
    }

    public String processImage(String imagePath) {
        try {
            log.info("Processing image: {}", imagePath);
            String absolutePath = new File(imagePath).getAbsolutePath();
            Mat img = Imgcodecs.imread(absolutePath);

            if (img.empty()) {
                log.error("Failed to load image: {}", imagePath);
                throw new RuntimeException("Failed to process image: Image is empty or corrupt.");
            }

            // Convert to grayscale
            Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

            // Apply Gaussian blur to reduce noise
            Imgproc.GaussianBlur(img, img, new Size(5, 5), 0);

            // Apply Adaptive Thresholding for better text extraction
            Imgproc.adaptiveThreshold(img, img, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

            String processedPath = imagePath.replace("uploads/", "uploads/processed_");
            Imgcodecs.imwrite(processedPath, img);
            log.info("Image processing complete. Processed image saved at: {}", processedPath);
            return processedPath;
        } catch (Exception e) {
            log.error("Error processing image: {}", imagePath, e);
            throw new RuntimeException("Unexpected error occurred during image processing.", e);
        }
    }

}
