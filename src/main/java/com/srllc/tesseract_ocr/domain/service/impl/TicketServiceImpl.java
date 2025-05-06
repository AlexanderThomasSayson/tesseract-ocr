package com.srllc.tesseract_ocr.domain.service.impl;

import com.srllc.tesseract_ocr.common.mapper.TicketMapper;
import com.srllc.tesseract_ocr.domain.dao.TicketDao;
import com.srllc.tesseract_ocr.domain.dto.TicketDto;
import com.srllc.tesseract_ocr.domain.entity.Ticket;
import com.srllc.tesseract_ocr.domain.exception.FileReadingException;
import com.srllc.tesseract_ocr.domain.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketDao ticketDao;
    private final TicketMapper ticketMapper;

    public TicketServiceImpl(TicketDao ticketDao, TicketMapper ticketMapper) {
        this.ticketDao = ticketDao;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public List<TicketDto> handleCsvUpload(MultipartFile file) {
        List<TicketDto> savedTickets = new ArrayList<>();
        List<String> skippedTickets = new ArrayList<>();

        log.info("Starting CSV upload process...");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean header = true;

            while ((line = reader.readLine()) != null) {
                if (header) {
                    log.debug("Skipping CSV header: {}", line);
                    header = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 2) {
                    String ticketNumber = data[0].trim();
                    String siNumber = data[1].trim();
                    log.debug("Processing ticket: {}, SI: {}", ticketNumber, siNumber);

                    if (ticketDao.existsByTicketNumber(ticketNumber)) {
                        log.warn("Skipping duplicate ticket: {}", ticketNumber);
                        skippedTickets.add(ticketNumber);
                        continue;
                    }

                    TicketDto dto = new TicketDto(ticketNumber, siNumber);
                    Ticket ticket = ticketMapper.mapToEntity(dto);
                    Ticket saved = ticketDao.save(ticket);
                    TicketDto savedDto = ticketMapper.mapToDto(saved);
                    savedTickets.add(savedDto);

                    log.info("Saved ticket: {}", ticketNumber);
                } else {
                    log.warn("Skipping invalid CSV line: {}", line);
                }
            }

        } catch (IOException e) {
            log.error("Failed to read CSV file", e);
            throw new FileReadingException("Failed to process file: " + e.getMessage(), e);
        }

        log.info("CSV processing complete. {} ticket(s) saved. {} ticket(s) skipped due to duplicates.",
                savedTickets.size(), skippedTickets.size());

        if (!skippedTickets.isEmpty()) {
            log.info("Skipped ticket numbers: {}", String.join(", ", skippedTickets));
        }

        return savedTickets;
    }

}
