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
@Tag(name = "2. Voucher Redemption Form Controller", description = "Operations for managing SYT voucher redemption forms.")
public class VoucherRedemptionController {

    private final VoucherRedemptionFormService voucherRedemptionFormService;

    public VoucherRedemptionController(VoucherRedemptionFormService voucherRedemptionFormService) {
        this.voucherRedemptionFormService = voucherRedemptionFormService;
    }

    @Operation(summary = "Voucher Redemption From Extraction", description = "This endpoint turns text into a JSON table with voucher redemption form..")
    @PostMapping(value = "/extract-table", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<Map<String, Object>>> extractTextTable(@RequestParam("file") MultipartFile file) throws IOException {
        List<Map<String, Object>> tables = voucherRedemptionFormService.extractTablesFromFile(file);
        return DefaultResponse.displayFoundObject(tables);
    }
}
