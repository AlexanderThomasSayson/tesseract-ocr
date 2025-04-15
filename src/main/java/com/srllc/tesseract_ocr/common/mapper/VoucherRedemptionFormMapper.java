package com.srllc.tesseract_ocr.common.mapper;

import com.srllc.tesseract_ocr.domain.dto.VoucherRedemptionFormDTO;
import com.srllc.tesseract_ocr.domain.entity.VoucherRedemptionForm;
import org.springframework.stereotype.Component;

@Component
public class VoucherRedemptionFormMapper {

    public VoucherRedemptionFormDTO mapToDto(VoucherRedemptionForm voucherRedemptionForm){
        VoucherRedemptionFormDTO voucherRedemptionFormDTO = new VoucherRedemptionFormDTO();
        voucherRedemptionFormDTO.setId(voucherRedemptionForm.getId());
        voucherRedemptionFormDTO.setRetailerName(voucherRedemptionForm.getRetailerName());
        voucherRedemptionFormDTO.setBaranggay(voucherRedemptionForm.getBarangay());
        voucherRedemptionFormDTO.setMunicipality(voucherRedemptionForm.getMunicipality());
        voucherRedemptionFormDTO.setProvince(voucherRedemptionForm.getProvince());
        voucherRedemptionFormDTO.setContactNumber(voucherRedemptionForm.getContactNumber());
        voucherRedemptionFormDTO.setFarmArea(voucherRedemptionForm.getFarmArea());
        voucherRedemptionFormDTO.setNkHybridPurchased(voucherRedemptionForm.getNkHybridPurchased());
        voucherRedemptionFormDTO.setBagsPurchased(voucherRedemptionForm.getBagsPurchased());
        voucherRedemptionFormDTO.setReceiptNumber(voucherRedemptionForm.getReceiptNumber());
        voucherRedemptionFormDTO.setVoucherNumber(voucherRedemptionForm.getVoucherNumber());
        return  voucherRedemptionFormDTO;
    }

    public VoucherRedemptionForm mapToEntity(VoucherRedemptionFormDTO voucherRedemptionFormDTO) {
        VoucherRedemptionForm voucherRedemptionForm = new VoucherRedemptionForm();
        voucherRedemptionForm.setId(voucherRedemptionFormDTO.getId());
        voucherRedemptionForm.setRetailerName(voucherRedemptionFormDTO.getRetailerName());
        voucherRedemptionForm.setBarangay(voucherRedemptionFormDTO.getBaranggay());
        voucherRedemptionForm.setMunicipality(voucherRedemptionFormDTO.getMunicipality());
        voucherRedemptionForm.setProvince(voucherRedemptionFormDTO.getProvince());
        voucherRedemptionForm.setContactNumber(voucherRedemptionFormDTO.getContactNumber());
        voucherRedemptionForm.setFarmArea(voucherRedemptionFormDTO.getFarmArea());
        voucherRedemptionForm.setNkHybridPurchased(voucherRedemptionFormDTO.getNkHybridPurchased());
        voucherRedemptionForm.setBagsPurchased(voucherRedemptionFormDTO.getBagsPurchased());
        voucherRedemptionForm.setReceiptNumber(voucherRedemptionFormDTO.getReceiptNumber());
        voucherRedemptionForm.setVoucherNumber(voucherRedemptionFormDTO.getVoucherNumber());
        return voucherRedemptionForm;
    }

}
