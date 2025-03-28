package com.srllc.tesseract_ocr.domain.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException(String message, Exception e) {
        super(message);
    }
}
