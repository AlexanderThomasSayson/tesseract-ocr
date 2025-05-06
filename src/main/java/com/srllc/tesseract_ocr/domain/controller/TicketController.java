package com.srllc.tesseract_ocr.domain.controller;

import com.srllc.tesseract_ocr.common.utils.ApiResponse;
import com.srllc.tesseract_ocr.common.utils.DefaultResponse;
import com.srllc.tesseract_ocr.domain.dto.TicketDto;
import com.srllc.tesseract_ocr.domain.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@Tag(
        name = "3. Ticket Controller",
        description = "Handles operations related to uploading and managing ticket numbers provided by SYT for the lookup table. This endpoint supports bulk uploads and facilitates quick reference matching."
)
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Operation(
            summary = "Upload CSV File",
            description = "Allows the upload of a CSV file containing ticket numbers and SI (Sales Invoice) numbers. The uploaded data will be processed and stored for lookup and validation purposes."
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<TicketDto>> uploadCsv(@RequestParam("file") MultipartFile file) {
        List<TicketDto> uploadedTickets = ticketService.handleCsvUpload(file);
        return DefaultResponse.displayCreatedObject(uploadedTickets);
    }

}
