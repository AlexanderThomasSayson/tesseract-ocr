package com.srllc.tesseract_ocr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "original_image",nullable = false, columnDefinition = "LONGBLOB")
    private byte[] originalImage;

    @Lob
    @Column(name = "processed_image",nullable = false,columnDefinition = "LONGBLOB")
    private byte[] processedImage;

    @Column(name = "extracted_text",length = 5000)
    private String extractedText;
}
