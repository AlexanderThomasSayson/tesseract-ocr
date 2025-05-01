package com.srllc.tesseract_ocr.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticketNo;
    private String amount;
    private String serialNo;

    public Voucher(String ticketNo, String amount, String serialNo) {
        this.ticketNo = ticketNo;
        this.amount = amount;
        this.serialNo = serialNo;
    }
}
