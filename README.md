# Value at Risk Calculator

Application to compute the Value at Risk (VaR) using historical values.
The historical VaR computation method uses actual historical values of a trade of a portfolio(multiple trades) ranking those values from worst to best and selecting the loss at the desined percentile as the VaR estimate.

## Features
- File-based input processing
- Single trade and portfolio VaR calculations
- BigDecimal precision

## Build application

```bash
./gradlew build
```

## Run application with input parameters
```bash
./gradlew run --args="<input_confidence_level> <input_file>"
```
Example:
```bash
./gradlew run --args="0.95 data/portfolio.txt"
```

If the <input_file> only contains 1 line, the VaR will compute at trade level.
If the <input_file> contains multiple lines, the VaR will be computed at portfolio level.

### Parameters constraints

- <input_confidence_level> should be within 0 and 1
- <input_file> should not be empty and each line should have the following format:
ID1,value1,value2,value3
ID2,value1,value2,value3

Please note that the number of historical values for each trades should be the same to compute the portfolio VaR.

You can refer to the example in /data/portfolio.txt


## Remarks on portfolio VaR calculation

The portfolio VaR is often less than the sum of the individual trade VaR calculations. 