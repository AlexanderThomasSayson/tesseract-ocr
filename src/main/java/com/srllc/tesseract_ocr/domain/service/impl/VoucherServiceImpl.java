package com.srllc.tesseract_ocr.domain.service.impl;

import com.srllc.tesseract_ocr.common.mapper.VoucherMapper;
import com.srllc.tesseract_ocr.common.utils.ImageProcessingUtil;
import com.srllc.tesseract_ocr.domain.dao.TicketDao;
import com.srllc.tesseract_ocr.domain.dao.VoucherDao;
import com.srllc.tesseract_ocr.domain.dto.VoucherDto;
import com.srllc.tesseract_ocr.domain.entity.Voucher;
import com.srllc.tesseract_ocr.domain.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

import static com.srllc.tesseract_ocr.common.utils.VoucherUtils.extractSerialNos;
import static com.srllc.tesseract_ocr.common.utils.VoucherUtils.extractTicketNos;

@Slf4j
@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherDao voucherDao;
    private final TextractClient textractClient;
    private final VoucherMapper voucherMapper;
    private final TicketDao ticketDao;

    public VoucherServiceImpl(VoucherDao voucherDao,
                              TextractClient textractClient,
                              VoucherMapper voucherMapper, TicketDao ticketDao) {
        this.voucherDao = voucherDao;
        this.textractClient = textractClient;
        this.voucherMapper = voucherMapper;
        this.ticketDao = ticketDao;
    }

    @Override
    public List<String> extractTextFromFile(MultipartFile file) throws IOException {

        // with OpenCV preprocessing
        log.info("Starting text extraction from file: {}", file.getOriginalFilename());

        byte[] processedImageBytes = ImageProcessingUtil.preprocessImage(file);
        log.debug("Image preprocessing completed.");

        ByteBuffer imageBytes = ByteBuffer.wrap(processedImageBytes);

        Document document = Document.builder()
                .bytes(SdkBytes.fromByteBuffer(imageBytes))
                .build();

        DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                .document(document)
                .build();

        log.debug("Sending request to AWS Textract.");
        DetectDocumentTextResponse response = textractClient.detectDocumentText(request);
        log.debug("Received response from AWS Textract.");

        List<String> extractedText = response.blocks().stream()
                .filter(block -> block.blockType().equals(BlockType.LINE))
                .map(Block::text)
                .toList();

        log.info("Extracted {} lines of text from image.", extractedText.size());

        List<String> ticketNos = extractTicketNos(extractedText);
        log.info("Extracted {} ticket numbers: {}", ticketNos.size(), ticketNos);

        List<String> serialNos = extractSerialNos(extractedText);
        log.info("Extracted {} serial numbers: {}", serialNos.size(), serialNos);

        String amount = "500";

        for (int i = 0; i < ticketNos.size(); i++) {
            String ticketNo = ticketNos.get(i);

            boolean existsInTicketTable = ticketDao.existsByTicketNumber(ticketNo);
            log.debug("Ticket number '{}' exists in Ticket table: {}", ticketNo, existsInTicketTable);

            VoucherDto dto = new VoucherDto();
            dto.setTicketNo(ticketNo);
            dto.setAmount(amount);
            dto.setSerialNo(i < serialNos.size() ? serialNos.get(i) : null);
            dto.setExistsInTicketTable(existsInTicketTable); // Add this field to your DTO if needed

            Voucher entity = voucherMapper.toEntity(dto);
            voucherDao.save(entity);
            log.debug("Saved voucher to database: {}", dto);
        }

        log.info("Text extraction and voucher saving completed.");
        return extractedText;
    }


}
