package com.srllc.tesseract_ocr.controller;

import com.srllc.tesseract_ocr.dto.VoucherDTO;
import com.srllc.tesseract_ocr.service.VoucherService;
import com.srllc.tesseract_ocr.utils.ApiResponse;
import com.srllc.tesseract_ocr.utils.DefaultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/vouchers")
@Tag(name = "Voucher Controller", description = "Operations for managing OCR for syngenta vouchers.")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @Operation(summary = "Upload voucher", description = "This endpoint allows uploading of vouchers and use tesseract for text extraction.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<VoucherDTO> uploadVoucher(@RequestPart("file") MultipartFile file) {
            VoucherDTO dto = voucherService.processVoucher(file);
            return DefaultResponse.displayCreatedObject(dto);

    }

    @Operation(summary = "Get original image", description = "This endpoint retrieves the original uploaded image by its ID.")
    @GetMapping("/image/original{id}")
    public ResponseEntity<byte[]> getOriginalImage(@PathVariable Long id) {
        byte[] imageBytes = voucherService.getOriginalImage(id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }

    @Operation(summary = "Get processed image", description = "This endpoint retrieves the processed image by its ID.")
    @GetMapping("/image/processed{id}")
    public ResponseEntity<byte[]> getProcessedImage(@PathVariable Long id) {
        byte[] imageBytes = voucherService.getProcessedImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }
}
