package com.srllc.tesseract_ocr.common.mapper;

import com.srllc.tesseract_ocr.domain.dto.TicketDto;
import com.srllc.tesseract_ocr.domain.entity.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    Ticket mapToEntity(TicketDto ticketDto);

    TicketDto mapToDto(Ticket ticket);
}
