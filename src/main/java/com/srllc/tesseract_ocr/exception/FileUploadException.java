package com.srllc.tesseract_ocr.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException(String message, Exception e) {
        super(message);
    }
}
