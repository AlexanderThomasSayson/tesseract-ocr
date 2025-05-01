package com.srllc.tesseract_ocr.domain.service.impl;

import com.srllc.tesseract_ocr.common.utils.ImageProcessingUtil;
import com.srllc.tesseract_ocr.domain.dao.DocumentTextDao;
import com.srllc.tesseract_ocr.domain.entity.DocumentText;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.srllc.tesseract_ocr.common.utils.AwsTextractProcessor.extractForms;
import static com.srllc.tesseract_ocr.common.utils.AwsTextractProcessor.extractFromForms;
import static com.srllc.tesseract_ocr.common.utils.TableExtractionUtil.extractTables;

@Slf4j
@Service
public class AwsTextractServiceImpl implements AwsTextractService {

    private final TextractClient textractClient;
    private final DocumentTextDao documentTextDao;

    public AwsTextractServiceImpl(TextractClient textractClient, DocumentTextDao documentTextDao) {
        this.textractClient = textractClient;
        this.documentTextDao = documentTextDao;
    }

//    @Override
//    public List<String> extractTextFromFile(MultipartFile file) throws IOException {
//        byte[] processedImageBytes = ImageProcessingUtil.preprocessImage(file);
//        ByteBuffer imageBytes = ByteBuffer.wrap(processedImageBytes);
//
//        Document document = Document.builder()
//                .bytes(SdkBytes.fromByteBuffer(imageBytes))
//                .build();
//
//        DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
//                .document(document)
//                .build();
//
//        DetectDocumentTextResponse response = textractClient.detectDocumentText(request);
//
//        return response.blocks().stream()
//                .filter(block -> block.blockType().equals(BlockType.LINE))
//                .map(Block::text)
//                .collect(Collectors.toList());
//    }

    public List<String> extractTextFromFile(MultipartFile file) throws IOException {
        byte[] processedImageBytes = ImageProcessingUtil.preprocessImage(file);
        ByteBuffer imageBytes = ByteBuffer.wrap(processedImageBytes);

        Document document = Document.builder()
                .bytes(SdkBytes.fromByteBuffer(imageBytes))
                .build();

        DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                .document(document)
                .build();

        DetectDocumentTextResponse response = textractClient.detectDocumentText(request);

        List<String> extractedText = response.blocks().stream()
                .filter(block -> block.blockType().equals(BlockType.LINE))
                .map(Block::text)
                .collect(Collectors.toList());

        List<String> ticketNos = extractTicketNos(extractedText);
        List<String> serialNos = extractSerialNos(extractedText);

        // Static amount
        String amount = "500";

        // Save each ticket + serialNo combo to DB
        for (int i = 0; i < ticketNos.size(); i++) {
            String ticket = ticketNos.get(i);
            String serial = (i < serialNos.size()) ? serialNos.get(i) : null;
            DocumentText documentText = new DocumentText(ticket, amount, serial);
            documentTextDao.save(documentText);
        }

        return extractedText;
    }

    private List<String> extractTicketNos(List<String> lines) {
        Pattern ticketPattern = Pattern.compile("MHS\\s?\\d+");
        return lines.stream()
                .flatMap(line -> {
                    Matcher matcher = ticketPattern.matcher(line);
                    List<String> matches = new ArrayList<>();
                    while (matcher.find()) {
                        matches.add(matcher.group().replace(" ", "")); // Remove space if present
                    }
                    return matches.stream();
                })
                .collect(Collectors.toList());
    }


    private List<String> extractSerialNos(List<String> lines) {
        Pattern serialPattern = Pattern.compile("SI:\\s?\\d+");
        return lines.stream()
                .flatMap(line -> {
                    Matcher matcher = serialPattern.matcher(line);
                    List<String> matches = new ArrayList<>();
                    while (matcher.find()) {
                        matches.add(matcher.group().replace("SI:", "").trim());
                    }
                    return matches.stream();
                })
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

    @Override
    public Map<String, Object> extractContentFromFile(MultipartFile file) throws IOException {
        ByteBuffer imageBytes = ByteBuffer.wrap(file.getBytes());

        Document document = Document.builder()
                .bytes(SdkBytes.fromByteBuffer(imageBytes))
                .build();

        AnalyzeDocumentRequest request = AnalyzeDocumentRequest.builder()
                .document(document)
                .featureTypes(FeatureType.TABLES, FeatureType.FORMS)
                .build();

        AnalyzeDocumentResponse response = textractClient.analyzeDocument(request);
        List<Block> blocks = response.blocks();

        Map<String, Block> blockMap = blocks.stream()
                .collect(Collectors.toMap(Block::id, b -> b));

        Map<String, Object> extractedData = new HashMap<>();

        // Extract tables
        List<Map<String, Object>> tables = extractTables(blocks, blockMap);
        if (!tables.isEmpty()) {
            extractedData.put("tables", tables);
        }

        // Extract forms (key-value pairs)
        List<Map<String, String>> forms = extractFromForms(blocks, blockMap);
        if (!forms.isEmpty()) {
            extractedData.put("forms", forms);
        }

        // Extract plain text
        StringBuilder plainText = new StringBuilder();
        for (Block block : blocks) {
            if (block.blockType().equals(BlockType.LINE)) {
                plainText.append(block.text()).append("\n");
            }
        }
        if (!plainText.toString().isBlank()) {
            extractedData.put("text", plainText.toString().trim());
        }

        return extractedData;
    }


}
