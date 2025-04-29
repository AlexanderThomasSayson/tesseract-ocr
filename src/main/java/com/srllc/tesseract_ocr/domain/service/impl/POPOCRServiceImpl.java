package com.srllc.tesseract_ocr.domain.service.impl;

import com.srllc.tesseract_ocr.domain.service.POPOCRService;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.srllc.tesseract_ocr.common.constants.POPConstants.SOLD_TO_KEYWORDS;

@Service
@Slf4j
public class POPOCRServiceImpl implements POPOCRService {

    @Override
    public String extractTextFromImage(File file) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata"); // Ensure tessdata is placed here
        tesseract.setLanguage("eng");

        try {
            BufferedImage preprocessed = preprocessImage(file);
            String text = tesseract.doOCR(preprocessed);
            log.info("Extracted Text:\n{}", text);
            return text;
        } catch (IOException | TesseractException e) {
            log.error("OCR Failed: {}", e.getMessage(), e);
            return "OCR Failed: " + e.getMessage();
        }
    }

    private BufferedImage preprocessImage(File file) throws IOException {
        // Read the image as OpenCV Mat
        Mat image = Imgcodecs.imread(file.getAbsolutePath());

        if (image.empty()) {
            throw new IOException("Could not read image file: " + file.getAbsolutePath());
        }

        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Reduce noise with Gaussian blur
        Imgproc.GaussianBlur(gray, gray, new Size(3, 3), 0);

        // Apply adaptive thresholding
        Mat thresh = new Mat();
        Imgproc.adaptiveThreshold(
                gray,
                thresh,
                255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY,
                11,
                4
        );

        // Convert Mat back to BufferedImage
        BufferedImage processedImage = matToBufferedImage(thresh);

        // Save for inspection
        File output = new File("uploads/processed_" + file.getName());
        ImageIO.write(processedImage, "jpeg", output);

        return processedImage;
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] buffer = new byte[bufferSize];
        mat.get(0, 0, buffer);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    @Override
    public Map<String, Object> extractStructuredData(String text) {
        Map<String, Object> data = new HashMap<>();

        //for date field
        String dateRegex = "Date\\s*[:_-]*\\s*(\\d{2}[/-]\\d{2})[^\\d]*(\\d{2,4})";
        List<String> dateParts = extractMultipleGroups(text, dateRegex);
        if (!dateParts.get(0).equals("Not found")) {
            String formattedDate = cleanAndFormatDate(dateParts.get(0), dateParts.get(1));
            data.put("date", formattedDate);
        } else {
            data.put("date", "Not found");
        }

        // for distributor field.
        String distributor = extractPattern(text, "(?i)Distributor\\s*[:\\-—_]*\\s*(.+)");
        data.put("distributor", distributor);

        String keywordPattern = String.join("|", SOLD_TO_KEYWORDS);
        String regex = "(" + keywordPattern + ")\\s*(\\w+)";
        data.put("soldTo", extractPattern(text, regex));
        data.put("address", extractPattern(text, "Address\\s*[:]*\\s*(.*?)(?=Item Code|Qty|\\n)"));
        data.put("totalAmountDue", extractPattern(text, "TOTAL AMOUNT DUE\\s*\\|\\s*([\\d,]+)\\s*\\|"));
        data.put("tinNumber", extractPattern(text, "TIN:\\s*([\\d\\-]+)"));

        List<Map<String, String>> items = new ArrayList<>();
        Pattern itemLinePattern = Pattern.compile("(?m)^(\\d+)\\s+(BTL|BOX|CN)\\s+(.*?)\\s+(\\d{2,4})\\s+(\\d{1,3}(?:,\\d{3})*)$");
        Matcher matcher = itemLinePattern.matcher(text);
        while (matcher.find()) {
            Map<String, String> item = new LinkedHashMap<>();
            item.put("qty", matcher.group(1));
            item.put("unit", matcher.group(2));
            item.put("description", matcher.group(3));
            item.put("unitPrice", matcher.group(4));
            item.put("amount", matcher.group(5));
            item.put("tinNumber", matcher.group(6));
            items.add(item);
        }
        data.put("items", items);

        return data;
    }

    private String extractPattern(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(matcher.groupCount()).trim();
        }
        return "Not found";
    }

    public String cleanAndFormatDate(String mmdd, String yyyy) {
        try {
            log.debug("Raw mmdd: {}, yyyy: {}", mmdd, yyyy);
            String[] parts = mmdd.split("[/-]");
            if (parts.length < 2) {
                log.warn("Invalid MM/DD structure: {}", mmdd);
                return "Invalid date format";
            }

            String month = parts[0];
            String day = parts[1];

            if (yyyy.length() == 2) {
                yyyy = "20" + yyyy;
                log.debug("Expanded year: {}", yyyy);
            }

            String dateStr = yyyy + "-" + month + "-" + day;
            log.info("Formatted date: {}", dateStr);
            return dateStr;
        } catch (Exception e) {
            log.error("Exception while formatting date: {}", e.getMessage(), e);
            return "Invalid date format";
        }
    }



    public List<String> extractMultipleGroups(String text, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            List<String> groups = new ArrayList<>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                log.debug("Group {}: {}", i, group);
                // Keep digits and separators only (like 03-25)
                groups.add(group.replaceAll("[^\\d/-]", ""));
            }
            return groups;
        }
        log.warn("No match found for regex: {}", regex);
        return Collections.singletonList("Not found");
    }



}
