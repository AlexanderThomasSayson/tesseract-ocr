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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class VoucherRedemptionFormOcrService {

    private final Tesseract tesseract;

    /**
     * Initializes the Tesseract OCR engine with custom configurations.
     */
    public VoucherRedemptionFormOcrService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(6); // Assume a single uniform block of text
        tesseract.setOcrEngineMode(1); // Use LSTM engine only for better accuracy
    }

    public VoucherRedemptionFormOcrService(Tesseract tesseract) {
        this.tesseract = tesseract;
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


    public String extractRetailerName(String text) {
        log.info("Extracting retailer name from text.");
        Pattern pattern = Pattern.compile("(TIWALA Retailer Name | Retailer Name)\\s+(\\D+)");
        Matcher matcher = pattern.matcher(text);
        log.debug("Attempting to extract retailer name from text: {}", text);
        if (matcher.find()) {
            String retailerName = matcher.group(2);
            String prefix = matcher.group(1);
            log.info("Retailer name extracted: {} (with prefix: {})", retailerName, prefix);
            return retailerName;
        } else {
            log.warn("No retailer name found in extracted text.");
            return null;
        }
    }

    public String extractRetailerAddress(String text) {
        log.info("Extracting retailer address from text.");
        Pattern pattern = Pattern.compile("(Retailer Address | Address)\\s+(\\D+)");
        Matcher matcher = pattern.matcher(text);
        log.debug("Attempting to extract retailer address from text: {}", text);
        if (matcher.find()) {
            String retailerName = matcher.group(2);
            String prefix = matcher.group(1);
            log.info("Retailer address extracted: {} (with prefix: {})", retailerName, prefix);
            return retailerName;
        } else {
            log.warn("No retailer address found in extracted text.");
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
            img.convertTo(img, -1, 1.5, 60);

            // Apply Gaussian blur
            Imgproc.GaussianBlur(img, img, new Size(3, 3), 0);

            // Apply sharpening kernel
            Mat sharpened = new Mat();
            Imgproc.GaussianBlur(img, sharpened, new Size(0, 0), 10);

            // Adaptive thresholding
            Imgproc.adaptiveThreshold(img, img, 255,
                    Imgproc.ADAPTIVE_THRESH_MEAN_C,
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
