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
        // Optimize Tesseract configuration
        tesseract.setPageSegMode(6); // Assume a single uniform block of text
        tesseract.setOcrEngineMode(1); // Use LSTM engine only for better accuracy
        // Optional: Add whitelist if ticket numbers have specific format
        tesseract.setTessVariable("tessedit_char_whitelist", "MHS0123456789 ");
    }

    public String extractText(String imagePath) throws TesseractException {
        try {
            log.info("Extracting text from image: {}", imagePath);
            String result = tesseract.doOCR(new File(imagePath)).trim();
            log.debug("Extracted text: {}", result);
            return result;
        } catch (TesseractException e) {
            log.error("Error extracting text from image: {}", imagePath, e);
            throw e;
        }
    }

    public String extractTicketNumber(String text) {
        log.info("Extracting ticket number from text.");
        Pattern pattern = Pattern.compile("(MHS|HS)\\s+(\\d+)");
        Matcher matcher = pattern.matcher(text);
        log.debug("Attempting to extract ticket number from text: {}", text);
        if (matcher.find()) {
            String ticketNumber = matcher.group(2); // Group 2 captures the digits
            String prefix = matcher.group(1);      // Group 1 captures "MHS" or "HS"
            log.info("Ticket number extracted: {} (with prefix: {})", ticketNumber, prefix);
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

            // Enhance contrast
            img.convertTo(img, -1, 1.2, 10); // Increase contrast by 20% and brightness by 10

            // Apply Gaussian blur with optimized kernel size
            Imgproc.GaussianBlur(img, img, new Size(3, 3), 0);

            // Apply sharpening kernel
            Mat sharpened = new Mat();
            Imgproc.GaussianBlur(img, sharpened, new Size(0, 0), 10);

            // Optimized adaptive thresholding
            Imgproc.adaptiveThreshold(img, img, 255,
                    Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                    Imgproc.THRESH_BINARY,
                    15, // Larger block size for better adaptation
                    4); // Adjusted constant for better binarization

            String processedPath = imagePath.replace("uploads/", "uploads/processed_");
            boolean saved = Imgcodecs.imwrite(processedPath, img);

            if (!saved) {
                log.error("Failed to save processed image: {}", processedPath);
                throw new RuntimeException("Failed to save processed image");
            }

            log.info("Image processing complete. Processed image saved at: {}", processedPath);
            return processedPath;

        } catch (Exception e) {
            log.error("Error processing image: {}", imagePath, e);
            throw new RuntimeException("Unexpected error occurred during image processing.", e);
        }
    }
}