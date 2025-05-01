package com.srllc.tesseract_ocr.domain.entity;

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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_voucher_aws")
public class DocumentText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticketNo;
    private String amount;
    private String serialNo;

    public DocumentText(String ticketNo, String amount, String serialNo) {
        this.ticketNo = ticketNo;
        this.amount = amount;
        this.serialNo = serialNo;
    }
}
