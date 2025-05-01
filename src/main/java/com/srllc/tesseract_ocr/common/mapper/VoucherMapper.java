package com.srllc.tesseract_ocr.common.mapper;

import com.srllc.tesseract_ocr.domain.dto.VoucherDto;
import com.srllc.tesseract_ocr.domain.entity.Voucher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    Voucher toEntity(VoucherDto dto);
    VoucherDto toDto(Voucher entity);
}
