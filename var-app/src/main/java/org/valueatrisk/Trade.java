package org.valueatrisk;

import java.math.BigDecimal;
import java.util.List;

public record Trade(
    String id,
    List<BigDecimal> historicalPnL
) {}
