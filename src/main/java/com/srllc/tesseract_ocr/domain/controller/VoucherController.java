package com.srllc.tesseract_ocr.domain.controller;

import com.srllc.tesseract_ocr.common.utils.ApiResponse;
import com.srllc.tesseract_ocr.common.utils.DefaultResponse;
import com.srllc.tesseract_ocr.domain.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers")
@Tag(name = "1. Voucher Controller", description = "Operations for managing SYT vouchers.")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @Operation(summary = "Voucher text extraction", description = "This endpoint extracts text from a voucher image using AWS Textract.")
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<String>> extractVoucher(@RequestParam("file") MultipartFile file) throws IOException {
        List<String> lines = voucherService.extractTextFromFile(file);
        return DefaultResponse.displayFoundObject(lines);
    }
}
