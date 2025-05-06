package com.srllc.tesseract_ocr.domain.dao;

import com.srllc.tesseract_ocr.domain.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketDao extends JpaRepository<Ticket, Long> {
    boolean existsByTicketNumber(String ticketNumber);

}
