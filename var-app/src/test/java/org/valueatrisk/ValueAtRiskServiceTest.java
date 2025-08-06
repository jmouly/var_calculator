package org.valueatrisk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

public class ValueAtRiskServiceTest {
    ValueAtRiskService service;

    @BeforeEach
    void setUp() {
        service = new ValueAtRiskService();
    }

    @Test void getTradeVaR_withNullTrade_ShouldThrowError() {
        BigDecimal confidenceInput = new BigDecimal(0.5);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.getTradeVaR(confidenceInput,null)
        );
        assertEquals("Trade cannot be null. Please verify input file", exception.getMessage());

    }

    @Test void getTradeVaR_withWrongConfidenceInput_ShouldThrowError() {
        BigDecimal confidenceInput = new BigDecimal(-1);
        List<BigDecimal> pnlSerieInput = new ArrayList<>();
        Trade trade = new Trade("TESLA1", pnlSerieInput);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.getTradeVaR(confidenceInput,trade)
        );
        assertEquals("Confidence level input must be between 0 and 1", exception.getMessage());

        BigDecimal confidenceInput2 = new BigDecimal(1.1);

        exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.getTradeVaR(confidenceInput2,trade)
        );
        assertEquals("Confidence level input must be between 0 and 1", exception.getMessage());

    }

    @Test void getTradeVaR_withWrongPnlSerieInput_ShouldThrowError() {
        BigDecimal confidenceInput = new BigDecimal(0.5);
        Trade trade = new Trade("TESLA1", null);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.getTradeVaR(confidenceInput,trade)
        );
        assertEquals("Empty serie of historical PnL values for trade", exception.getMessage());

        List<BigDecimal> pnlSerieInput = new ArrayList<>();
        Trade trade2 = new Trade("TESLA2", pnlSerieInput);

        exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.getTradeVaR(confidenceInput,trade2)
        );
        assertEquals("Empty serie of historical PnL values for trade", exception.getMessage());

    }

    @Test void getTradeVaR_withValidTarde_ShouldReturnVaR() {
        BigDecimal confidenceInput = new BigDecimal(0.5);
        List<BigDecimal> pnlSerieInput = Lists.newArrayList(BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN);
        Trade trade = new Trade("TESLA1", pnlSerieInput);

        BigDecimal result = service.getTradeVaR(confidenceInput,trade);

        BigDecimal expected = new BigDecimal(10);
        
        assertEquals(0, expected.compareTo(result));
    }

    @Test void getPortfolioVaR_withWrongPorfolioInput_ShouldThrowError() {
        BigDecimal confidenceInput = new BigDecimal(0.5);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.getPortfolioVaR(confidenceInput,null)
        );
        assertEquals("Portfolio PnL series cannot be null or empty", exception.getMessage());

        List<Trade> porfolio = new ArrayList<>();        
        exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.getPortfolioVaR(confidenceInput,porfolio)
        );
        
        assertEquals("Portfolio PnL series cannot be null or empty", exception.getMessage());
    }

        @Test void getPortfolioVaR_withWrongInvalidSizePorfolio_ShouldThrowError() {
        BigDecimal confidenceInput = new BigDecimal(0.5);
        Trade trade1 = new Trade("TESLA1", new ArrayList<>());
        List<BigDecimal> historicalPnL = Lists.newArrayList(BigDecimal.ONE);
        Trade trade2 = new Trade("TESLA2", historicalPnL);

        List<Trade> porfolio = Lists.newArrayList(trade1, trade2);

         IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.getPortfolioVaR(confidenceInput,porfolio)
        );
        
        assertEquals("All trades must have the same number of historical values", exception.getMessage());
    }

    @Test void getPortfolioVaR_withValidPortfolio_ShouldReturnVaR() {
        BigDecimal confidenceInput = new BigDecimal(0.95);
        List<BigDecimal> pnlSerieInput1 = Lists.newArrayList(BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN);
        Trade trade1 = new Trade("TESLA1", pnlSerieInput1);

        List<BigDecimal> pnlSerieInput2 = Lists.newArrayList(new BigDecimal(22.3), new BigDecimal(56.5), new BigDecimal(-14.22), new BigDecimal(3.99));
        Trade trade2 = new Trade("TESLA2", pnlSerieInput2);
        
        List<Trade> portfolio = Lists.newArrayList(trade1, trade2);

        BigDecimal result = service.getPortfolioVaR(confidenceInput,portfolio);

        BigDecimal expected = new BigDecimal("-13.220000");
        
        assertEquals(0, expected.compareTo(result));
    }
}
