package com.srllc.tesseract_ocr.common.utils;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class ImageProcessingUtil {

    public static byte[] preprocessImage(MultipartFile file) throws IOException {
        // Decode image
        Mat image = Imgcodecs.imdecode(new MatOfByte(file.getBytes()), Imgcodecs.IMREAD_COLOR);

        // Convert to grayscale
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Increase contrast (alpha > 1.0 increases contrast)
        Mat contrastEnhanced = new Mat();
        double alpha = 3; // Contrast control (1.0–3.0)
        double beta = 0;    // Brightness control (0–100)
        grayImage.convertTo(contrastEnhanced, -1, alpha, beta);

        // Invert the contrast-enhanced image
        Mat invertedImage = new Mat();
        Core.bitwise_not(contrastEnhanced, invertedImage);

        // Save for reference
        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) uploadDir.mkdirs();

        Imgcodecs.imwrite("uploads/original.png", image);
        Imgcodecs.imwrite("uploads/processed.png", invertedImage);

        // Convert to byte array
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", invertedImage, matOfByte);

        return matOfByte.toArray();
    }

}
