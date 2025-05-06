package com.srllc.tesseract_ocr.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto {
    private Long id;
    private String ticketNumber;
    private String siNumber;

    public TicketDto(String ticketNumber, String siNumber) {
        this.ticketNumber = ticketNumber;
        this.siNumber = siNumber;
    }
}
