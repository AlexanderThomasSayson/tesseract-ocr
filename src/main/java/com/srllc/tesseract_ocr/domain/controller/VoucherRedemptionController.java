package com.srllc.tesseract_ocr.domain.controller;

import com.srllc.tesseract_ocr.common.utils.ApiResponse;
import com.srllc.tesseract_ocr.common.utils.DefaultResponse;
import com.srllc.tesseract_ocr.domain.service.VoucherRedemptionFormService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/voucher-redemption-forms")
@Tag(
        name = "2. Voucher Redemption Form Controller",
        description = "Handles operations related to SYT voucher redemption forms. These forms serve as the basis for verifying if a ticket number has already been used or redeemed, ensuring the integrity of the voucher redemption process."
)
public class VoucherRedemptionController {

    private final VoucherRedemptionFormService voucherRedemptionFormService;

    public VoucherRedemptionController(VoucherRedemptionFormService voucherRedemptionFormService) {
        this.voucherRedemptionFormService = voucherRedemptionFormService;
    }

    @Operation(
            summary = "Voucher Redemption Form Extraction",
            description = "Extracts text from uploaded voucher redemption form images using AWS Textract. This process identifies and retrieves key fields such as ticket numbers to determine if a voucher has been previously redeemed."
    )
    @PostMapping(value = "/extract-table", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<Map<String, Object>>> extractTextTable(@RequestParam("file") MultipartFile file) throws IOException {
        List<Map<String, Object>> tables = voucherRedemptionFormService.extractTablesFromFile(file);
        return DefaultResponse.displayFoundObject(tables);
    }
}
