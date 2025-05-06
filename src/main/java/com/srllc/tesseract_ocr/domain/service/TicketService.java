package com.srllc.tesseract_ocr.domain.service;

import com.srllc.tesseract_ocr.domain.dto.TicketDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TicketService {

    List<TicketDto> handleCsvUpload(MultipartFile file);

}
