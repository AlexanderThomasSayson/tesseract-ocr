package com.srllc.tesseract_ocr.domain.service;

import com.srllc.tesseract_ocr.domain.exception.ImageProcessingException;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.opencv.core.Core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
        tesseract.setTessVariable("tessedit_char_whitelist", "₱PMHS0123456789 ");
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
            log.info("Extracted text: {}", result);
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
     * Extracts amount from the provided text.
     * The expected format is "₱<digits>".
     *
     * @param text the text to search for a ticket number.
     * @return the extracted amount, or null if not found.
     */
    public String extractAmount(String text) {
        log.info("Extracting amount from text.");
        Pattern pattern = Pattern.compile("(₱|P)\\s*(\\d+)"); // Basic version
        // For decimals or commas: Pattern pattern = Pattern.compile("(₱|P)\\s*([\\d,]+\\.?\\d*)");
        Matcher matcher = pattern.matcher(text);
        log.debug("Attempting to extract amount from text: {}", text);
        if (matcher.find()) {
            String prefix = matcher.group(1); // ₱ or P
            String amount = matcher.group(2); // Digits
            log.info("Amount extracted: {} (with prefix: {})", amount, prefix);
            return amount;
        } else {
            log.warn("No amount found in extracted text: '{}'", text);
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
            Mat gray = new Mat();
            Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

            // Enhance contrast
            //img.convertTo(img, -1, 1.2, 10);

            // Apply Gaussian blur
            //Imgproc.GaussianBlur(img, img, new Size(3, 3), 0);

            // Apply sharpening kernel
            //Mat sharpened = new Mat();
            //Imgproc.GaussianBlur(img, sharpened, new Size(0, 0), 10);

            Mat equalized = new Mat();
            Imgproc.equalizeHist(gray, equalized);

            // Adaptive thresholding
            Mat thresh = new Mat();
            Imgproc.adaptiveThreshold(equalized, thresh, 255,
                    Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                    Imgproc.THRESH_BINARY_INV,
                    15, 9);

            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
            Imgproc.dilate(thresh, thresh, kernel);

            String processedPath = imagePath.replace("uploads/", "uploads/processed_");
            boolean saved = Imgcodecs.imwrite(processedPath, thresh);

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

    public List<String> extractMultipleTicketNumbers(String text) {
        log.info("Extracting multiple ticket numbers from text...");
        Pattern pattern = Pattern.compile("(MHS|HS)\\s+(\\d+)");
        Matcher matcher = pattern.matcher(text);
        List<String> ticketNumbers = new ArrayList<>();

        while (matcher.find()) {
            String ticketNumber = matcher.group(2);
            String prefix = matcher.group(1);
            log.info("Ticket number extracted: {} (with prefix: {})", ticketNumber, prefix);
            ticketNumbers.add(ticketNumber);
        }

        if (ticketNumbers.isEmpty()) {
            log.warn("No ticket numbers found in extracted text.");
        }

        return ticketNumbers;
    }

    public String extractSINumber(String imagePath) {
        log.info("Extracting SI number from rotated image...");

        try {
            String absolutePath = new File(imagePath).getAbsolutePath();
            Mat original = Imgcodecs.imread(absolutePath);

            if (original.empty()) {
                log.error("Failed to load image for SI extraction: {}", imagePath);
                throw new ImageProcessingException("Image is empty or corrupt.");
            }

            Mat rotated = new Mat();
            Core.transpose(original, rotated);
            Core.flip(rotated, rotated, 0);

            String rotatedPath = imagePath.replace("uploads/", "uploads/si_");
            boolean saved = Imgcodecs.imwrite(rotatedPath, rotated);

            if (!saved) {
                log.error("Failed to save rotated image for SI extraction: {}", rotatedPath);
                throw new RuntimeException("Failed to save rotated image.");
            }

            String ocrResult = tesseract.doOCR(new File(rotatedPath)).trim();
            log.info("Attempting to extract SI number from text: {}", ocrResult);

            Pattern pattern = Pattern.compile("(SI|S)\\s+(\\d+)");
            Matcher matcher = pattern.matcher(ocrResult);

            if (matcher.find()) {
                String siNumber = matcher.group(2);
                String prefix = matcher.group(1);
                log.info("SI number extracted: {} (with prefix: {})", siNumber, prefix);
                return siNumber;
            } else {
                log.warn("No SI number found in extracted text.");
                return null;
            }

        } catch (Exception e) {
            log.error("Error occurred while extracting SI number.", e);
            return null;
        }
    }
}
