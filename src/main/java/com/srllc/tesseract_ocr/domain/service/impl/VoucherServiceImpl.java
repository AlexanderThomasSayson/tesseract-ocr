package com.srllc.tesseract_ocr.domain.service.impl;

import com.srllc.tesseract_ocr.common.mapper.VoucherMapper;
import com.srllc.tesseract_ocr.common.utils.ImageProcessingUtil;
import com.srllc.tesseract_ocr.domain.dao.VoucherDao;
import com.srllc.tesseract_ocr.domain.dto.VoucherDto;
import com.srllc.tesseract_ocr.domain.entity.Voucher;
import com.srllc.tesseract_ocr.domain.service.VoucherService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Document;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherDao voucherDao;
    private final TextractClient textractClient;
    private final VoucherMapper voucherMapper;

    public VoucherServiceImpl(VoucherDao voucherDao,
                              TextractClient textractClient,
                              VoucherMapper voucherMapper) {
        this.voucherDao = voucherDao;
        this.textractClient = textractClient;
        this.voucherMapper = voucherMapper;
    }

    @Override
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
        String amount = "500";

        for (int i = 0; i < ticketNos.size(); i++) {
            VoucherDto dto = new VoucherDto();
            dto.setTicketNo(ticketNos.get(i));
            dto.setAmount(amount);
            dto.setSerialNo(i < serialNos.size() ? serialNos.get(i) : null);

            Voucher entity = voucherMapper.toEntity(dto);
            voucherDao.save(entity);
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
}
