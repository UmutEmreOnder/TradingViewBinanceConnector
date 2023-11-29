package onder.umut.tradingviewbinanceconnector.binance.coin.service.impl;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onder.umut.tradingviewbinanceconnector.binance.coin.service.CoinService;
import onder.umut.tradingviewbinanceconnector.tradingview.dto.Alert;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoinServiceImpl implements CoinService {
    HashMap<String, Double> stepSizeMap = new HashMap<>();
    private final UMFuturesClientImpl client;

    @Override
    public void changeInitialLeverage(Integer leverage, String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        log.info("Changing initial leverage for symbol: {} to: {}", symbol, leverage);
        parameters.put("symbol", symbol);
        parameters.put("leverage", leverage);

        try {
            String result = client.account().changeInitialLeverage(parameters);
            log.info(result);
        } catch (BinanceConnectorException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    @Override
    public void fitToPrecision(Alert alert) {
        Double stepSize = getCoinPrecision(alert.getSymbol());

        if (stepSize == null) {
            log.error("Step size is null for symbol: {}", alert.getSymbol());
            return;
        }

        BigDecimal stepSizeBigDecimal = BigDecimal.valueOf(stepSize);
        int scale = Math.max(0, stepSizeBigDecimal.stripTrailingZeros().scale());

        BigDecimal entryPrice = BigDecimal.valueOf(alert.getEntryPrice());
        BigDecimal takeProfit = BigDecimal.valueOf(alert.getTakeProfit());

        alert.setEntryPrice(entryPrice.subtract(entryPrice.remainder(stepSizeBigDecimal)).setScale(scale, RoundingMode.DOWN).doubleValue());
        alert.setTakeProfit(takeProfit.subtract(takeProfit.remainder(stepSizeBigDecimal)).setScale(scale, RoundingMode.DOWN).doubleValue());
    }
    private Double getCoinPrecision(String symbol) {
        if (stepSizeMap.containsKey(symbol)) {
            log.info("Step size for symbol: {} is already fetched", symbol);
            log.info("Step size: {}", stepSizeMap.get(symbol));
            return stepSizeMap.get(symbol);
        }

        try {
            String result = client.market().exchangeInfo();
            extractStepSize(result);

            log.info("Step size for symbol: {} is fetched", symbol);
            log.info("Step size: {}", stepSizeMap.get(symbol));
            return stepSizeMap.get(symbol);
        } catch (BinanceConnectorException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }

        return null;
    }

    private void extractStepSize(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray symbolsArray = jsonObject.getJSONArray("symbols");

        for (int i = 0; i < symbolsArray.length(); i++) {
            JSONObject symbolObject = symbolsArray.getJSONObject(i);
            String symbol = symbolObject.getString("symbol");
            JSONArray filtersArray = symbolObject.getJSONArray("filters");

            for (int j = 0; j < filtersArray.length(); j++) {
                JSONObject filterObject = filtersArray.getJSONObject(j);
                if ("LOT_SIZE".equals(filterObject.getString("filterType"))) {
                    String stepSize = filterObject.getString("stepSize");
                    stepSizeMap.put(symbol, Double.parseDouble(stepSize) * 10);
                    break;
                }
            }
        }
    }
}
