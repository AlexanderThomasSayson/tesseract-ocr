package com.srllc.tesseract_ocr.common.utils;

import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.EntityType;
import software.amazon.awssdk.services.textract.model.Relationship;
import software.amazon.awssdk.services.textract.model.RelationshipType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AwsTextractProcessor {


    public static List<Map<String, Object>> extractContent(List<Block> blocks) {
        Map<String, Block> blockMap = blocks.stream()
                .collect(Collectors.toMap(Block::id, b -> b));

        boolean hasTables = blocks.stream().anyMatch(b -> b.blockType() == BlockType.TABLE);
        boolean hasForms = blocks.stream().anyMatch(b -> b.blockType() == BlockType.KEY_VALUE_SET);

        if (hasTables) {
            return extractTables(blocks, blockMap);
        } else if (hasForms) {
            return extractForms(blocks);
        } else {
            return extractLines(blocks);
        }
    }

    public static List<Map<String, Object>> extractTables(List<Block> blocks, Map<String, Block> blockMap) {
        List<Map<String, Object>> tableData = new ArrayList<>();

        for (Block block : blocks) {
            if (block.blockType() == BlockType.TABLE) {
                Map<String, Object> table = new HashMap<>();
                table.put("tableId", block.id());

                List<Map<String, Object>> rows = new ArrayList<>();
                for (Block cell : blocks) {
                    if (cell.blockType() == BlockType.CELL && block.id().equals(cell.relationships().get(0).ids().get(0))) {
                        Map<String, Object> cellData = new HashMap<>();
                        cellData.put("rowIndex", cell.rowIndex());
                        cellData.put("columnIndex", cell.columnIndex());
                        cellData.put("text", getTextForBlock(cell, blockMap));
                        rows.add(cellData);
                    }
                }
                table.put("rows", rows);
                tableData.add(table);
            }
        }

        return tableData;
    }

    public static List<Map<String, Object>> extractForms(List<Block> blocks) {
        List<Map<String, Object>> forms = new ArrayList<>();

        for (Block block : blocks) {
            if (block.blockType() == BlockType.KEY_VALUE_SET && block.entityTypes().contains("KEY")) {
                Map<String, Object> kv = new HashMap<>();
                kv.put("key", block.text());
                // In production, find and map VALUE block from relationships
                forms.add(kv);
            }
        }

        return forms;
    }

    public static List<Map<String, Object>> extractLines(List<Block> blocks) {
        List<Map<String, Object>> lines = new ArrayList<>();

        for (Block block : blocks) {
            if (block.blockType() == BlockType.LINE) {
                Map<String, Object> line = new HashMap<>();
                line.put("text", block.text());
                line.put("confidence", block.confidence());
                lines.add(line);
            }
        }

        return lines;
    }

    private static String getTextForBlock(Block block, Map<String, Block> blockMap) {
        if (block.relationships() == null) return "";

        List<String> words = new ArrayList<>();
        for (Relationship relationship : block.relationships()) {
            if (relationship.type() == RelationshipType.CHILD) {
                for (String id : relationship.ids()) {
                    Block child = blockMap.get(id);
                    if (child != null && child.blockType() == BlockType.WORD) {
                        words.add(child.text());
                    }
                }
            }
        }
        return String.join(" ", words);
    }




    public static List<Map<String, String>> extractFromForms(List<Block> blocks, Map<String, Block> blockMap) {
        List<Map<String, String>> forms = new ArrayList<>();

        // Map to hold key-value pairs
        Map<String, String> currentForm = null;

        // Loop through all blocks and extract key-value pairs
        for (Block block : blocks) {
            if (block.blockType().equals(BlockType.KEY_VALUE_SET)) {
                // Identify the key (label) and value
                String key = null;
                String value = null;

                // Check if it's a key (label)
                if (block.entityTypes().contains(EntityType.KEY)) {
                    key = block.text();
                }

                // Check if it's a value
                if (block.entityTypes().contains(EntityType.VALUE)) {
                    value = block.text();
                }

                // Add key-value pairs to the current form
                if (key != null && value != null) {
                    currentForm = new HashMap<>();
                    currentForm.put(key, value);
                    forms.add(currentForm);
                }
            }
        }

        return forms;
    }

}
