package com.srllc.tesseract_ocr.domain.controller;

import com.srllc.tesseract_ocr.common.utils.ApiResponse;
import com.srllc.tesseract_ocr.common.utils.DefaultResponse;
import com.srllc.tesseract_ocr.domain.dto.VoucherRedemptionFormDTO;
import com.srllc.tesseract_ocr.domain.service.VoucherRedemptionFromService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/voucher-redemption")
@Tag(name = "Voucher Redemption Controller", description = "Operations for managing OCR for syngenta voucher redemption.")
public class VoucherRedemptionFormController {

    private final VoucherRedemptionFromService voucherRedemptionFromService;

    public VoucherRedemptionFormController(VoucherRedemptionFromService voucherRedemptionFromService) {
        this.voucherRedemptionFromService = voucherRedemptionFromService;
    }

    @Operation(summary = "Upload voucher", description = "This endpoint allows uploading of vouchers and use tesseract for text extraction.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<VoucherRedemptionFormDTO> uploadVoucher(@RequestPart("file") MultipartFile file) {
        VoucherRedemptionFormDTO dto = voucherRedemptionFromService.processVoucher(file);
        return DefaultResponse.displayCreatedObject(dto);

    }
}
