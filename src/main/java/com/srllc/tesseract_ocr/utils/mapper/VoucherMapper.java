package com.srllc.tesseract_ocr.utils.mapper;

import com.srllc.tesseract_ocr.dto.VoucherDTO;
import com.srllc.tesseract_ocr.entity.Voucher;
import org.springframework.stereotype.Component;

@Component
public class VoucherMapper {

    public VoucherDTO mapToDto(Voucher voucher) {
        VoucherDTO voucherDTO = new VoucherDTO();
        voucherDTO.setId(voucher.getId());
        voucherDTO.setOriginalText(voucher.getOriginalText());
        voucherDTO.setTicketNo(voucher.getTicketNo());
        voucherDTO.setOriginalImageURL(voucher.getOriginalImageURL());
        voucherDTO.setProcessedImageURL(voucher.getProcessedImageURL());

        return voucherDTO;
    }

    public Voucher mapToEntity(VoucherDTO voucherDTO) {
        Voucher voucher = new Voucher();
        voucher.setId(voucherDTO.getId());
        voucher.setOriginalText(voucherDTO.getOriginalText());
        voucher.setTicketNo(voucherDTO.getTicketNo());
        voucher.setOriginalImageURL(voucherDTO.getOriginalImageURL());
        voucher.setProcessedImageURL(voucherDTO.getProcessedImageURL());

        return voucher;
    }
}
