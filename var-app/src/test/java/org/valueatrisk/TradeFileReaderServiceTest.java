package org.valueatrisk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.collect.Lists;

public class TradeFileReaderServiceTest {

    TradeFileReaderService service;

    @BeforeEach
    void setUp() {
        service = new TradeFileReaderService();
    }

    @Test void readTradesFromFile_withValidFile_returnsTrades() throws IOException {
        // Create a temporary file with test content
        Path testFile = Files.createTempFile("trades", ".txt");
        Files.write(testFile, List.of(
            "ISIN1,100.99,0.75",
            "ISIN2 ,300.25,             400.00",
            "", "   "
        ));

        List<Trade> trades = service.readTradesFromFile(testFile.toString());

        assertEquals(trades.size(), 2);
        assertEquals(trades.get(0).id(), "ISIN1");
        assertEquals(trades.get(1).id(), "ISIN2");
        List<BigDecimal> expectedValuesTrade1 = Lists.newArrayList(new BigDecimal("100.99"), new BigDecimal("0.75"));
        assertEquals(trades.get(0).historicalPnL(), expectedValuesTrade1);
        List<BigDecimal> expectedValuesTrade2 = Lists.newArrayList(new BigDecimal("300.25"), new BigDecimal("400.00"));
        assertEquals(trades.get(1).historicalPnL(), expectedValuesTrade2);
    }


    @Test void readTradesFromFile_withEmptyFile_throwsException(@TempDir Path tempDir) throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.createFile(emptyFile);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.readTradesFromFile(emptyFile.toString())
        );
        
        assertEquals("Input file is empty", exception.getMessage());
    }

    @Test void readTradesFromFile_nonExistentFile_throwsException() {
        String nonExistentPath = "nonexistent/file/path.csv";
        
        IOException exception = assertThrows(
            IOException.class,
            () -> service.readTradesFromFile(nonExistentPath)
        );
        
        assertTrue(exception.getMessage().contains(nonExistentPath));
    }

    @Test void readTradesFromFile_invalidTradeFormat_throwsException() throws IOException {
        Path testFile = Files.createTempFile("trades", ".csv");
        Files.write(testFile, List.of(
            "ISIN1,100.50",
            "invalid",
            "ISIN2,200.75"
        ));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.readTradesFromFile(testFile.toString())
        );
        
        assertEquals("Invalid trade format in file. Expected 'ISIN,value1,value2,...'", exception.getMessage());
    }

    @Test void readTradesFromFile_invalidNumericValue_throwsException() throws IOException {
        Path testFile = Files.createTempFile("trades", ".csv");
        Files.write(testFile, List.of(
            "ISIN1,100.50",
            "ISIN2,invalid"
        ));

        assertThrows(
            NumberFormatException.class,
            () -> service.readTradesFromFile(testFile.toString())
        );
    }
}
