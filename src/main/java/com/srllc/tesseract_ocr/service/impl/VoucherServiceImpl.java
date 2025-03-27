package com.srllc.tesseract_ocr.service.impl;

import com.srllc.tesseract_ocr.dao.VoucherDAO;
import com.srllc.tesseract_ocr.dto.VoucherDTO;
import com.srllc.tesseract_ocr.entity.Voucher;
import com.srllc.tesseract_ocr.exception.FileUploadException;
import com.srllc.tesseract_ocr.exception.ResourceNotFoundException;
import com.srllc.tesseract_ocr.service.OCRService;
import com.srllc.tesseract_ocr.service.VoucherService;
import com.srllc.tesseract_ocr.utils.mapper.VoucherMapper;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    private final OCRService ocrService;
    private final VoucherDAO voucherDAO;
    private final VoucherMapper voucherMapper;

    public VoucherServiceImpl(OCRService ocrService, VoucherDAO voucherDAO, VoucherMapper voucherMapper) {
        this.ocrService = ocrService;
        this.voucherDAO = voucherDAO;
        this.voucherMapper = voucherMapper;
    }

    private final String uploadDir = "uploads/";

    @Transactional
    @Override
    public VoucherDTO processVoucher(MultipartFile multipartFile) {
        log.info("Starting voucher processing...");
        try {
            Files.createDirectories(Paths.get(uploadDir));
            log.info("Upload directory ensured: {}", uploadDir);

            String originalFileName = multipartFile.getOriginalFilename();
            if (originalFileName == null || originalFileName.isBlank()) {
                log.error("File name is missing or invalid.");
                throw new ResourceNotFoundException("File name is missing or invalid.");
            }

            String savedFilePath = uploadDir + originalFileName;
            Files.copy(multipartFile.getInputStream(), Paths.get(savedFilePath), StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved at: {}", savedFilePath);

            String processedPath = ocrService.processImage(savedFilePath);
            log.info("Image processed and saved at: {}", processedPath);

            String extractedText;
            try {
                extractedText = ocrService.extractText(processedPath);
                log.info("Text extracted successfully from image.");
            } catch (TesseractException e) {
                log.error("OCR processing failed for: {}", originalFileName, e);
                throw new FileUploadException("OCR processing failed for: " + originalFileName, e);
            }
            log.info("Extracted text: {}", extractedText);

            String ticketNo = ocrService.extractTicketNumber(extractedText);
            if (ticketNo == null) {
                log.error("Ticket number not found in extracted text!");
                throw new ResourceNotFoundException("Ticket number not found in extracted text!");
            }
            log.info("Ticket number extracted: {}", ticketNo);

            VoucherDTO voucherDTO = new VoucherDTO();
            voucherDTO.setOriginalText(extractedText);
            voucherDTO.setTicketNo(ticketNo);
            voucherDTO.setOriginalImageURL(savedFilePath);
            voucherDTO.setProcessedImageURL(processedPath);

            Voucher voucher = voucherMapper.mapToEntity(voucherDTO);
            voucher = voucherDAO.save(voucher);
            log.info("Voucher saved with ID: {}", voucher.getId());

            return voucherMapper.mapToDto(voucher);
        } catch (IOException e) {
            log.error("Error saving the file: {}", multipartFile.getOriginalFilename(), e);
            throw new FileUploadException("Error saving the file: " + multipartFile.getOriginalFilename(), e);
        } catch (RuntimeException e) {
            log.error("Unexpected error occurred during voucher processing.", e);
            throw new FileUploadException("Unexpected error occurred during voucher processing.", e);
        }
    }

    @Override
    public VoucherDTO getOriginalImage(Long id) {
        log.info("Fetching original image for voucher ID: {}", id);
        Voucher voucher = voucherDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Voucher not found for ID: {}", id);
                    return new RuntimeException("Voucher not found!");
                });
        return voucherMapper.mapToDto(voucher);
    }

    @Override
    public VoucherDTO getProcessedImage(Long id) {
        log.info("Fetching processed image for voucher ID: {}", id);
        Voucher voucher = voucherDAO.findById(id)
                .orElseThrow(() -> {
                    log.error("Voucher not found for ID: {}", id);
                    return new RuntimeException("Voucher not found!");
                });
        return voucherMapper.mapToDto(voucher);
    }
}
