package com.srllc.tesseract_ocr.common.utils;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.Relationship;
import software.amazon.awssdk.services.textract.model.RelationshipType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TableExtractionUtil {

    public static List<Map<String, Object>> extractTables(List<Block> blocks, Map<String, Block> blockMap) {
        List<Map<String, Object>> tables = new ArrayList<>();

        for (Block block : blocks) {
            if (BlockType.TABLE.equals(block.blockType())) {
                Map<String, Object> tableData = new HashMap<>();

                // Store rows
                Map<Integer, Map<Integer, String>> rows = new HashMap<>();

                for (Relationship relationship : block.relationships()) {
                    if (relationship.type().equals(RelationshipType.CHILD)) {
                        for (String cellId : relationship.ids()) {
                            Block cell = blockMap.get(cellId);
                            if (BlockType.CELL.equals(cell.blockType())) {
                                int rowIndex = cell.rowIndex();
                                int columnIndex = cell.columnIndex();

                                String cellText = getText(cell, blockMap);

                                rows
                                        .computeIfAbsent(rowIndex, k -> new HashMap<>())
                                        .put(columnIndex, cellText);
                            }
                        }
                    }
                }

                tableData.put("table", rows);
                tables.add(tableData);
            }
        }
        return tables;
    }

    public static String getText(Block cell, Map<String, Block> blockMap) {
        StringBuilder text = new StringBuilder();

        if (cell.relationships() != null) {
            for (Relationship relationship : cell.relationships()) {
                if (relationship.type().equals(RelationshipType.CHILD)) {
                    for (String id : relationship.ids()) {
                        Block wordBlock = blockMap.get(id);
                        if (BlockType.WORD.equals(wordBlock.blockType())) {
                            text.append(wordBlock.text()).append(" ");
                        }
                    }
                }
            }
        }

        return text.toString().trim();
    }
}
