package onder.umut.tradingviewbinanceconnector.binance.trade.service.impl;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onder.umut.tradingviewbinanceconnector.binance.account.service.AccountService;
import onder.umut.tradingviewbinanceconnector.binance.config.BinanceConfig;
import onder.umut.tradingviewbinanceconnector.binance.trade.service.TradeService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeServiceImpl implements TradeService {
    private final BinanceConfig binanceConfig;
    private final AccountService accountService;
    private final UMFuturesClientImpl client;

    @Override
    public void closeAllPositions() {
        HashMap<String, Double> positions = getPositions();

        for (Map.Entry<String, Double> entry : positions.entrySet()) {
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
            String symbol = entry.getKey();
            Double amount = entry.getValue();
            String side = amount > 0 ? "SELL" : "BUY";

            parameters.put("symbol", symbol);
            parameters.put("side", side);
            parameters.put("type", "MARKET");
            parameters.put("quantity", amount * -1);

            try {
                client.account().newOrder(parameters);
                log.info("Position closed for {} with amount {}", symbol, amount);
            } catch (BinanceConnectorException e) {
                log.error("fullErrMessage: {}", e.getMessage(), e);
            } catch (BinanceClientException e) {
                log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}", e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
            }
        }
    }

    @Override
    public void openLongPosition(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        Double maxQuantity = calculateMaxQuantity(symbol);

        parameters.put("symbol", symbol);
        parameters.put("side", "BUY");
        parameters.put("type", "MARKET");
        parameters.put("quantity", maxQuantity);

        try {
            client.account().newOrder(parameters);
            log.info("Long position opened for {} with amount {}", symbol, maxQuantity);
        } catch (BinanceConnectorException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}", e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    @Override
    public void openShortPosition(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        Double maxQuantity = calculateMaxQuantity(symbol);

        parameters.put("symbol", symbol);
        parameters.put("side", "SELL");
        parameters.put("type", "MARKET");
        parameters.put("quantity", maxQuantity);

        try {
            client.account().newOrder(parameters);
            log.info("Short position opened for {} with amount {}", symbol, maxQuantity);
        } catch (BinanceConnectorException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}", e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    private Double calculateMaxQuantity(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        String result = client.market().markPrice(parameters);
        JSONArray jsonArray = new JSONArray(result);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String symbolFromApi = jsonObject.getString("symbol");
            if (symbolFromApi.equals(symbol)) {
                double markPrice = jsonObject.getDouble("markPrice");
                double maxQuantity = (accountService.getBalance() * binanceConfig.getPositionPercentage() / 100)
                        * binanceConfig.getLeverage() / markPrice;
                double roundedMaxQuantity = Math.floor(maxQuantity * 100) / 100;
                log.info("Rounded max quantity for {} is {}", symbol, roundedMaxQuantity);
                return roundedMaxQuantity;
            }
        }

        return null;
    }

    private HashMap<String, Double> getPositions() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        HashMap<String, Double> positions = new HashMap<>();

        try {
            String result = client.account().positionInformation(parameters);
            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String symbol = jsonObject.getString("symbol");
                double positionAmt = jsonObject.getDouble("positionAmt");

                if (positionAmt != 0) {
                    log.info("Open position: " + symbol + ", Amount: " + positionAmt);
                    positions.put(symbol, positionAmt);
                }
            }
        } catch (BinanceConnectorException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}", e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }

        return positions;
    }
}
