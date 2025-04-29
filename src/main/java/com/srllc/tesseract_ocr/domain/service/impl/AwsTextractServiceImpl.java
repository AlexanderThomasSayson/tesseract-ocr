package com.srllc.tesseract_ocr.domain.service.impl;

import com.srllc.tesseract_ocr.domain.service.AwsTextractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.srllc.tesseract_ocr.common.utils.TableExtractionUtil.extractTables;

@Slf4j
@Service
public class AwsTextractServiceImpl implements AwsTextractService {

    private final TextractClient textractClient;

    public AwsTextractServiceImpl(TextractClient textractClient) {
        this.textractClient = textractClient;
    }

    @Override
    public List<String> extractTextFromFile(MultipartFile file) throws IOException {
        ByteBuffer imageBytes = ByteBuffer.wrap(file.getBytes());

        Document document = Document.builder()
                .bytes(SdkBytes.fromByteBuffer(imageBytes))
                .build();

        DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                .document(document)
                .build();

        DetectDocumentTextResponse response = textractClient.detectDocumentText(request);

        return response.blocks().stream()
                .filter(block -> block.blockType().equals(BlockType.LINE))
                .map(Block::text)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> extractTablesFromFile(MultipartFile file)  throws IOException {
        ByteBuffer imageBytes = ByteBuffer.wrap(file.getBytes());

        Document document = Document.builder()
                .bytes(SdkBytes.fromByteBuffer(imageBytes))
                .build();

        AnalyzeDocumentRequest request = AnalyzeDocumentRequest.builder()
                .document(document)
                .featureTypes(FeatureType.TABLES)
                .build();

        AnalyzeDocumentResponse response = textractClient.analyzeDocument(request);
        List<Block> blocks = response.blocks();

        Map<String, Block> blockMap = blocks.stream()
                .collect(Collectors.toMap(Block::id, b -> b));

        return extractTables(blocks, blockMap);
    }
}
