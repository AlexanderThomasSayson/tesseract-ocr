package com.srllc.tesseract_ocr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_voucher")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @Column(name = "ticket_number")
    private String ticketNo;

    @Column(name = "original_image_url")
    private String originalImageURL;

    @Column(name = "processed_image_url")
    private String processedImageURL;
}

