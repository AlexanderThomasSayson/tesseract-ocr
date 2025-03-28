package com.srllc.tesseract_ocr.domain.exception;

public class FailedToLoadImageException extends RuntimeException {
    public FailedToLoadImageException(String message) {
        super(message);
    }
}
