package org.valueatrisk;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ValueAtRiskService {

    public BigDecimal getTradeVaR(BigDecimal confidenceLvl, Trade trade) {
        if (trade == null) {
            throw new IllegalArgumentException("Trade cannot be null. Please verify input file");
        }

        return calculateVaR(confidenceLvl, trade.historicalPnL());
    }

    public BigDecimal getPortfolioVaR(BigDecimal confidenceLvl, List<Trade> portfolio) {
        if (portfolio == null || portfolio.isEmpty()) {
            throw new IllegalArgumentException("Portfolio PnL series cannot be null or empty");
        }

        int numTradeVal = portfolio.get(0).historicalPnL().size();
        boolean inconsistentData = portfolio.stream()
                .anyMatch(t -> t.historicalPnL().size() != numTradeVal);


        if (inconsistentData) {
            throw new IllegalArgumentException("All trades must have the same number of historical values");
        }

        // Reduce the list of trades to a daily PnL serie across all trades
        List<BigDecimal> portfolioDailyPnl = IntStream.range(0, numTradeVal)
                .mapToObj(i -> portfolio.stream()
                        .map(t -> t.historicalPnL().get(i))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .collect(Collectors.toList());

        return calculateVaR(confidenceLvl, portfolioDailyPnl);
    }
    
    private BigDecimal calculateVaR(BigDecimal confidenceLvl, List<BigDecimal> histpnl) {
        if(confidenceLvl.compareTo(BigDecimal.ZERO) <= 0 || confidenceLvl.compareTo(BigDecimal.ONE) >= 1) {
            throw new IllegalArgumentException("Confidence level input must be between 0 and 1");
        }

        if(histpnl == null || histpnl.isEmpty()) {
            throw new IllegalArgumentException("Empty serie of historical PnL values for trade");
        }

        List<BigDecimal> sortedPnl = histpnl.stream().sorted().collect(Collectors.toList());
        BigDecimal size = new BigDecimal(sortedPnl.size());
        BigDecimal lossPercentage = BigDecimal.ONE.subtract(confidenceLvl);
        BigDecimal index = lossPercentage.multiply(size);

        int indexRdn = index.setScale(0, RoundingMode.FLOOR).intValue();

        // The VaR is the loss at this index
        BigDecimal tradeVaR = sortedPnl.get(indexRdn).negate();
        return tradeVaR.setScale(4, RoundingMode.HALF_UP);
    }

    // public BigDecimal getPorfolioVaR(BigDecimal confidenceLvl, List<List<BigDecimal>> porfolioVal) {
    //     if (porfolioVal == null || porfolioVal.isEmpty()) {
    //         throw new IllegalArgumentException("Portfolio PnL series cannot be null or empty");
    //     }
        
    //     // Check all trades in porfolio have the same number of values in their PnL serie
    //     int numTradeVal = porfolioVal.get(0).size();
    //     boolean inconsistentData = porfolioVal.stream()
    //             .anyMatch(trade -> trade.size() != numTradeVal);
        
    //     if (inconsistentData) {
    //         throw new IllegalArgumentException("All trades must have the same number of historical values");
    //     }

    //     // Reduce the list of list to a daily PnL serie across all trades
    //     List<BigDecimal> portfolioDailyPnl = IntStream.range(0, numTradeVal)
    //             .mapToObj(i -> porfolioVal.stream()
    //                     .map(trade -> trade.get(i))
    //                     .reduce(BigDecimal.ZERO, BigDecimal::add))
    //             .collect(Collectors.toList());

    //     return calculateVaR(confidenceLvl, portfolioDailyPnl);
    // }
}
