package com.srllc.tesseract_ocr.common.mapper;

import com.srllc.tesseract_ocr.domain.dto.VoucherDTO;
import com.srllc.tesseract_ocr.domain.entity.Voucher;
import org.springframework.stereotype.Component;

@Component
public class VoucherMapper {

    public VoucherDTO mapToDto(Voucher voucher) {
        VoucherDTO voucherDTO = new VoucherDTO();
        voucherDTO.setId(voucher.getId());
        voucherDTO.setOriginalText(voucher.getOriginalText());
        voucherDTO.setTicketNo(voucher.getTicketNo());
        voucherDTO.setAmount(voucher.getAmount());
        voucherDTO.setOriginalImageURL(voucher.getOriginalImageURL());
        voucherDTO.setProcessedImageURL(voucher.getProcessedImageURL());
        voucherDTO.setSiNumber(voucher.getSiNumber());

        return voucherDTO;
    }

    public Voucher mapToEntity(VoucherDTO voucherDTO) {
        Voucher voucher = new Voucher();
        voucher.setId(voucherDTO.getId());
        voucher.setOriginalText(voucherDTO.getOriginalText());
        voucher.setTicketNo(voucherDTO.getTicketNo());
        voucher.setAmount(voucherDTO.getAmount());
        voucher.setOriginalImageURL(voucherDTO.getOriginalImageURL());
        voucher.setProcessedImageURL(voucherDTO.getProcessedImageURL());
        voucher.setSiNumber(voucherDTO.getSiNumber());

        return voucher;
    }
}
