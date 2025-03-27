package com.srllc.tesseract_ocr.controller;

import com.srllc.tesseract_ocr.dao.VoucherDAO;
import com.srllc.tesseract_ocr.dto.VoucherDTO;
import com.srllc.tesseract_ocr.entity.Voucher;
import com.srllc.tesseract_ocr.exception.ResourceNotFoundException;
import com.srllc.tesseract_ocr.service.VoucherService;
import com.srllc.tesseract_ocr.utils.ApiResponse;
import com.srllc.tesseract_ocr.utils.DefaultResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    private final VoucherService voucherService;
    private final VoucherDAO voucherDAO;

    public VoucherController(VoucherService voucherService, VoucherDAO voucherDAO) {
        this.voucherService = voucherService;
        this.voucherDAO = voucherDAO;
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<VoucherDTO> uploadVoucher(@RequestPart("file") MultipartFile file) {
            VoucherDTO dto = voucherService.processVoucher(file);
            return DefaultResponse.displayCreatedObject(dto);

    }

    @GetMapping("/image/{id}/original")
    public ResponseEntity<byte[]> getOriginalImage(@PathVariable Long id) throws Exception {
        Voucher voucher = voucherDAO.findById(id).orElseThrow(() -> new Exception("Not found"));

        File file = new File(voucher.getOriginalImageURL());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Image file not found");
        }

        byte[] imageBytes = Files.readAllBytes(file.toPath());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }


    @GetMapping("/image/{id}/processed")
    public ResponseEntity<byte[]> getProcessedImage(@PathVariable Long id) throws Exception {
        Voucher voucher = voucherDAO.findById(id).orElseThrow(() -> new Exception("Not found"));
        File file = new File(voucher.getProcessedImageURL());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
    }
}
