package com.srllc.tesseract_ocr.common.utils;

import com.srllc.tesseract_ocr.domain.exception.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
public class FileStorageUtil {

    private static final String UPLOAD_DIR = "uploads/";

    public static String saveFile(MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            log.info("Upload directory ensured: {}", UPLOAD_DIR);

            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved at: {}", filePath);

            return filePath;
        } catch (IOException e) {
            log.error("Error saving the file: {}", file.getOriginalFilename(), e);
            throw new FileUploadException("Error saving the file: " + file.getOriginalFilename(), e);
        }
    }
}
