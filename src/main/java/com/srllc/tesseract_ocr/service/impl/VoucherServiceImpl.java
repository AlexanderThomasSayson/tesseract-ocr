package com.srllc.tesseract_ocr.service.impl;

import com.srllc.tesseract_ocr.dao.VoucherDAO;
import com.srllc.tesseract_ocr.dto.VoucherDTO;
import com.srllc.tesseract_ocr.entity.Voucher;
import com.srllc.tesseract_ocr.exception.FileReadingException;
import com.srllc.tesseract_ocr.exception.ResourceNotFoundException;
import com.srllc.tesseract_ocr.service.OCRService;
import com.srllc.tesseract_ocr.service.VoucherService;
import com.srllc.tesseract_ocr.utils.FileStorageUtil;
import com.srllc.tesseract_ocr.utils.OCRUtil;
import com.srllc.tesseract_ocr.utils.mapper.VoucherMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;

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

    @Transactional
    @Override
    public VoucherDTO processVoucher(MultipartFile multipartFile) {
        log.info("Starting voucher processing...");

        String savedFilePath = FileStorageUtil.saveFile(multipartFile);
        String processedPath = ocrService.processImage(savedFilePath);

        log.info("Image processed and saved at: {}", processedPath);

        String extractedText = OCRUtil.extractText(ocrService, processedPath);
        String ticketNo = OCRUtil.extractTicketNumber(ocrService, extractedText);

        String voucherNo = "MHS " + ticketNo;

        VoucherDTO voucherDTO = new VoucherDTO();
        voucherDTO.setOriginalText(extractedText);
        voucherDTO.setTicketNo(voucherNo);
        voucherDTO.setOriginalImageURL(savedFilePath);
        voucherDTO.setProcessedImageURL(processedPath);

        Voucher voucher = voucherMapper.mapToEntity(voucherDTO);
        voucher = voucherDAO.save(voucher);
        log.info("Voucher saved with ID: {}", voucher.getId());

        return voucherMapper.mapToDto(voucher);
    }

    @Override
    public byte[] getOriginalImage(Long id) {
        Voucher voucher = voucherDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with ID: " + id));

        File file = new File(voucher.getOriginalImageURL());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Image file not found for voucher ID: " + id);
        }

        try {
            return Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            throw new FileReadingException("Error reading image file", e);
        }
    }

    @Override
    public byte[] getProcessedImage(Long id) {
        Voucher voucher = voucherDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found with ID: " + id));

        File file = new File(voucher.getProcessedImageURL());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Image file not found for voucher ID: " + id);
        }

        try {
            return Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            throw new FileReadingException("Error reading image file", e);
        }
    }

}
