package com.srllc.tesseract_ocr.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherDto {
    private String ticketNo;
    private String amount;
    private String serialNo;
    private boolean existsInTicketTable;

}
