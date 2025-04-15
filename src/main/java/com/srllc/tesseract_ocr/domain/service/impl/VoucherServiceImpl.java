package com.srllc.tesseract_ocr.domain.service.impl;

import com.srllc.tesseract_ocr.domain.dao.VoucherDAO;
import com.srllc.tesseract_ocr.domain.dto.VoucherDTO;
import com.srllc.tesseract_ocr.domain.entity.Voucher;
import com.srllc.tesseract_ocr.domain.exception.FileReadingException;
import com.srllc.tesseract_ocr.domain.exception.ResourceNotFoundException;
import com.srllc.tesseract_ocr.domain.service.OCRService;
import com.srllc.tesseract_ocr.domain.service.VoucherService;
import com.srllc.tesseract_ocr.common.utils.FileStorageUtil;
import com.srllc.tesseract_ocr.common.utils.OCRUtil;
import com.srllc.tesseract_ocr.common.constants.ConstantStrings;
import com.srllc.tesseract_ocr.common.mapper.VoucherMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class VoucherServiceImpl implements VoucherService {

    private final OCRService ocrService;
    private final VoucherDAO voucherDAO;
    private final VoucherMapper voucherMapper;

    public VoucherServiceImpl(OCRService ocrService,
                              VoucherDAO voucherDAO,
                              VoucherMapper voucherMapper) {
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
        String voucherNo = OCRUtil.extractTicketNumber(ocrService, extractedText);
        String amount = "500";

        VoucherDTO voucherDTO = new VoucherDTO();
        voucherDTO.setOriginalText(extractedText);
        voucherDTO.setTicketNo(ConstantStrings.TICKET_PREFIX + voucherNo);
        voucherDTO.setAmount(ConstantStrings.PESO_SIGN + amount);
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

    @Transactional
    @Override
    public List<VoucherDTO> processMultipleVouchers(MultipartFile multipartFile) {
        log.info("Starting multiple voucher processing...");

        String savedFilePath = FileStorageUtil.saveFile(multipartFile);
        String processedPath = ocrService.processImage(savedFilePath);

        log.info("Image processed and saved at {}", processedPath);

        String extractedText = OCRUtil.extractText(ocrService, processedPath);
        List<String> voucherNumbers = OCRUtil.extractMultipleTicketNumbers(ocrService, extractedText);
        String amount = "500";
        String siNumber = OCRUtil.extractSINumber(ocrService, processedPath);

        List<VoucherDTO> vouchers = new ArrayList<>();
        for (String voucherNo : voucherNumbers) {
            VoucherDTO voucherDTO = new VoucherDTO();
            voucherDTO.setOriginalText(extractedText);
            voucherDTO.setTicketNo(ConstantStrings.TICKET_PREFIX + voucherNo);
            voucherDTO.setAmount(ConstantStrings.PESO_SIGN + amount);
            voucherDTO.setOriginalImageURL(savedFilePath);
            voucherDTO.setProcessedImageURL(processedPath);
            voucherDTO.setSiNumber(ConstantStrings.SI_PREFIX + siNumber);

            Voucher voucher = voucherMapper.mapToEntity(voucherDTO);
            voucher = voucherDAO.save(voucher);
            log.info("Voucher saved with ID {}", voucher.getId());

            vouchers.add(voucherMapper.mapToDto(voucher));
        }
        return vouchers;
    }
}
