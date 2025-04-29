package com.srllc.tesseract_ocr.domain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentRequest;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentResponse;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.FeatureType;
import software.amazon.awssdk.services.textract.model.TextractException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.srllc.tesseract_ocr.common.utils.TableExtractionUtil.extractTables;

@RestController
@RequestMapping("/api/v1/aws")
@Tag(name = "AWS Textract", description = "Operation for AWS text extraction.")
public class TextractController {

    private final TextractClient textractClient;

    public TextractController(TextractClient textractClient) {
        this.textractClient = textractClient;
    }


    @Operation(summary = "AWS-Textract", description = "This endpoint extracts an image using AWS textract.")
    @PostMapping(value = "/extract",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> extractText(@RequestParam("file") MultipartFile file) {
        try {
            // Convert file to ByteBuffer
            ByteBuffer imageBytes = ByteBuffer.wrap(file.getBytes());

            // Prepare AWS Textract request
            Document document = Document.builder()
                    .bytes(SdkBytes.fromByteBuffer(imageBytes))
                    .build();

            DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                    .document(document)
                    .build();

            // Call AWS Textract
            DetectDocumentTextResponse response = textractClient.detectDocumentText(request);

            // Extract text from response
            List<String> lines = response.blocks().stream()
                    .filter(block -> block.blockType().equals(BlockType.LINE))
                    .map(Block::text)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(lines);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file: " + e.getMessage());
        } catch (TextractException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Textract error: " + e.awsErrorDetails().errorMessage());
        }
    }

    @Operation(summary = "Extract-table", description = "This endpoint turn a text to json table.")
    @PostMapping(value = "/extract-table", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> extractTextTable(@RequestParam("file") MultipartFile file) {
        try {
            ByteBuffer imageBytes = ByteBuffer.wrap(file.getBytes());

            Document document = Document.builder()
                    .bytes(SdkBytes.fromByteBuffer(imageBytes))
                    .build();

            AnalyzeDocumentRequest request = AnalyzeDocumentRequest.builder()
                    .document(document)
                    .featureTypes(FeatureType.TABLES)
                    .build();

            AnalyzeDocumentResponse response = textractClient.analyzeDocument(request);

            // Get all blocks
            List<Block> blocks = response.blocks();

            // Build a map of blockId -> Block for easy lookup
            Map<String, Block> blockMap = blocks.stream()
                    .collect(Collectors.toMap(Block::id, b -> b));

            // Extract tables
            List<Map<String, Object>> tables = extractTables(blocks, blockMap);

            return ResponseEntity.ok(tables);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file: " + e.getMessage());
        } catch (TextractException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Textract error: " + e.awsErrorDetails().errorMessage());
        }
    }




}
