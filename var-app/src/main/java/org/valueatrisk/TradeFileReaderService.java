package org.valueatrisk;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TradeFileReaderService {


    public List<Trade> readTradesFromFile(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Input file is empty");
        }
        return lines.stream()
                .filter(line -> !line.trim().isEmpty())
                .map(trimmedLine -> parseTradeLine(trimmedLine))
                .collect(Collectors.toList());
    }

    private Trade parseTradeLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid trade format in file. Expected 'ISIN,value1,value2,...'");
        }
        String id = parts[0].trim();
        List<BigDecimal> values = Arrays.stream(parts, 1, parts.length)
                .map(String::trim)
                .map(BigDecimal::new)
                .collect(Collectors.toList());
        return new Trade(id, values);
    }
}
