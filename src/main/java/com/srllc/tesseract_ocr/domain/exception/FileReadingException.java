package com.srllc.tesseract_ocr.domain.exception;

public class FileReadingException extends RuntimeException {
    public FileReadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
