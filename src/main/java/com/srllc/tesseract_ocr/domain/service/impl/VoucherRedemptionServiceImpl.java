package com.srllc.tesseract_ocr.domain.service.impl;

import com.srllc.tesseract_ocr.common.mapper.VoucherRedemptionFormMapper;
import com.srllc.tesseract_ocr.common.utils.FileStorageUtil;
import com.srllc.tesseract_ocr.common.utils.VoucherRedemptionFormOcrUtil;
import com.srllc.tesseract_ocr.domain.dao.VoucherRedemptionFormDAO;
import com.srllc.tesseract_ocr.domain.dto.VoucherRedemptionFormDTO;
import com.srllc.tesseract_ocr.domain.entity.VoucherRedemptionForm;
import com.srllc.tesseract_ocr.domain.service.VoucherRedemptionFormOcrService;
import com.srllc.tesseract_ocr.domain.service.VoucherRedemptionFromService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class VoucherRedemptionServiceImpl implements VoucherRedemptionFromService {

    private final VoucherRedemptionFormOcrService voucherRedemptionFormOcrService;
    private final VoucherRedemptionFormDAO voucherRedemptionFormDAO;
    private final VoucherRedemptionFormMapper voucherRedemptionFormMapper;

    public VoucherRedemptionServiceImpl(VoucherRedemptionFormOcrService voucherRedemptionFormOcrService,
                                        VoucherRedemptionFormDAO voucherRedemptionFormDAO,
                                        VoucherRedemptionFormMapper voucherRedemptionFormMapper) {
        this.voucherRedemptionFormOcrService = voucherRedemptionFormOcrService;
        this.voucherRedemptionFormDAO = voucherRedemptionFormDAO;
        this.voucherRedemptionFormMapper = voucherRedemptionFormMapper;
    }

    @Transactional
    @Override
    public VoucherRedemptionFormDTO processVoucher(MultipartFile multipartFile) {
        log.info("Starting voucher redemption form processing...");

        String savedFilePath = FileStorageUtil.saveFile(multipartFile);
        String processedPath = voucherRedemptionFormOcrService.processImage(savedFilePath);

        log.info("Image processed and saved at: {}", processedPath);

        String extractedText = VoucherRedemptionFormOcrUtil.extractText(voucherRedemptionFormOcrService, processedPath);
        String retailerName = VoucherRedemptionFormOcrUtil.extractRetailerName(voucherRedemptionFormOcrService, extractedText);

        VoucherRedemptionFormDTO voucherRedemptionFormDTO = new VoucherRedemptionFormDTO();
        voucherRedemptionFormDTO.setRetailerName(retailerName);


        VoucherRedemptionForm voucherRedemptionForm = voucherRedemptionFormMapper.mapToEntity(voucherRedemptionFormDTO);
        voucherRedemptionForm = voucherRedemptionFormDAO.save(voucherRedemptionForm);
        log.info("Voucher saved with ID: {}", voucherRedemptionForm.getId());

        return voucherRedemptionFormMapper.mapToDto(voucherRedemptionForm);
    }

    @Override
    public byte[] getOriginalImage(Long id) {
        return new byte[0];
    }

    @Override
    public byte[] getProcessedImage(Long id) {
        return new byte[0];
    }
}
