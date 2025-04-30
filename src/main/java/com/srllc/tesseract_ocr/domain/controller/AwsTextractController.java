package com.srllc.tesseract_ocr.domain.controller;

import com.srllc.tesseract_ocr.common.utils.ApiResponse;
import com.srllc.tesseract_ocr.common.utils.DefaultResponse;
import com.srllc.tesseract_ocr.domain.service.AwsTextractService;
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
@RequestMapping("/api/v1/aws")
@Tag(name = "5. AWS Text Extract Controller", description = "Operation for AWS text extraction.")
public class AwsTextractController {

    private final AwsTextractService awsTextractService;

    public AwsTextractController(AwsTextractService awsTextractService) {
        this.awsTextractService = awsTextractService;
    }


    @Operation(summary = "AWS text extraction", description = "This endpoint extracts text from an image using AWS Textract.")
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<String>> extractText(@RequestParam("file") MultipartFile file) throws IOException {
        List<String> lines = awsTextractService.extractTextFromFile(file);
        return DefaultResponse.displayFoundObject(lines);
    }


    @Operation(summary = "AWS text extraction with tables", description = "This endpoint turns text into a JSON table.")
    @PostMapping(value = "/extract-table", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<Map<String, Object>>> extractTextTable(@RequestParam("file") MultipartFile file) throws IOException {
        List<Map<String, Object>> tables = awsTextractService.extractTablesFromFile(file);
        return DefaultResponse.displayFoundObject(tables);
    }

}
