package com.srllc.tesseract_ocr.common.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class VoucherUtils {

    public static List<String> extractTicketNos(List<String> lines) {
        Pattern ticketPattern = Pattern.compile("MHS\\s?\\d+");
        return lines.stream()
                .flatMap(line -> {
                    Matcher matcher = ticketPattern.matcher(line);
                    List<String> matches = new ArrayList<>();
                    while (matcher.find()) {
                        matches.add(matcher.group().replace(" ", ""));
                    }
                    return matches.stream();
                })
                .toList();
    }

    public static List<String> extractSerialNos(List<String> lines) {
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
                .toList();
    }
}
