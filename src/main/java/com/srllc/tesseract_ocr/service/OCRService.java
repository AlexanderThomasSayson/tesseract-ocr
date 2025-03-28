package com.srllc.tesseract_ocr.service;

import com.srllc.tesseract_ocr.exception.ImageProcessingException;
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

/**
 * Service class for Optical Character Recognition (OCR) processing.
 * Uses Tesseract OCR and OpenCV for text extraction and image preprocessing.
 */
@Service
@Slf4j
public class OCRService {

    private final Tesseract tesseract;

    /**
     * Initializes the Tesseract OCR engine with custom configurations.
     */
    public OCRService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(6); // Assume a single uniform block of text
        tesseract.setOcrEngineMode(1); // Use LSTM engine only for better accuracy
        tesseract.setTessVariable("tessedit_char_whitelist", "MHS0123456789 ");
    }

    /**
     * Extracts text from an image using Tesseract OCR.
     *
     * @param imagePath the path to the image file.
     * @return the extracted text.
     * @throws TesseractException if text extraction fails.
     */
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

    /**
     * Extracts a ticket number from the provided text.
     * The expected format is "MHS <digits>" or "HS <digits>".
     *
     * @param text the text to search for a ticket number.
     * @return the extracted ticket number, or null if not found.
     */
    public String extractTicketNumber(String text) {
        log.info("Extracting ticket number from text.");
        Pattern pattern = Pattern.compile("(MHS|HS)\\s+(\\d+)");
        Matcher matcher = pattern.matcher(text);
        log.debug("Attempting to extract ticket number from text: {}", text);
        if (matcher.find()) {
            String ticketNumber = matcher.group(2);
            String prefix = matcher.group(1);
            log.info("Ticket number extracted: {} (with prefix: {})", ticketNumber, prefix);
            return ticketNumber;
        } else {
            log.warn("No ticket number found in extracted text.");
            return null;
        }
    }

    /**
     * Processes an image by converting it to grayscale, enhancing contrast,
     * applying Gaussian blur, sharpening, and adaptive thresholding.
     *
     * @param imagePath the path to the image file.
     * @return the path to the processed image.
     * @throws RuntimeException if the image cannot be processed or saved.
     */
    public String processImage(String imagePath) {
        try {
            log.info("Processing image: {}", imagePath);
            String absolutePath = new File(imagePath).getAbsolutePath();
            Mat img = Imgcodecs.imread(absolutePath);

            if (img.empty()) {
                log.error("Failed to load image: {}", imagePath);
                throw new ImageProcessingException("Failed to process image: Image is empty or corrupt.");
            }

            // Convert to grayscale
            Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

            // Enhance contrast
            img.convertTo(img, -1, 1.2, 10);

            // Apply Gaussian blur
            Imgproc.GaussianBlur(img, img, new Size(3, 3), 0);

            // Apply sharpening kernel
            Mat sharpened = new Mat();
            Imgproc.GaussianBlur(img, sharpened, new Size(0, 0), 10);

            // Adaptive thresholding
            Imgproc.adaptiveThreshold(img, img, 255,
                    Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                    Imgproc.THRESH_BINARY,
                    15, 4);

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
