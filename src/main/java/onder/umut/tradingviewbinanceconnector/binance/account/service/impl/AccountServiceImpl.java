package onder.umut.tradingviewbinanceconnector.binance.account.service.impl;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onder.umut.tradingviewbinanceconnector.binance.account.service.AccountService;
import onder.umut.tradingviewbinanceconnector.binance.config.BinanceConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UMFuturesClientImpl client;
    private final BinanceConfig binanceConfig;

    @Override
    public Double getBalance() {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        try {
            String result = client.account().futuresAccountBalance(parameters);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(result);

            for (JsonNode node : rootNode) {
                if ("USDT".equals(node.get("asset").asText())) {
                    String balance = node.get("balance").asText();
                    log.info("USDT Balance: " + balance);
                    return Double.parseDouble(balance);
                }
            }
        } catch (BinanceConnectorException | JsonProcessingException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}", e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }

        return null;
    }

    @Override
    public Double getPositionAmount(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        try {
            String result = client.account().positionInformation(parameters);
            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String posSymbol = jsonObject.getString("symbol");
                double positionAmt = jsonObject.getDouble("positionAmt");

                if (posSymbol.equals(symbol)) {
                    log.info("Open position: " + symbol + ", Amount: " + positionAmt);
                    return positionAmt;
                }
            }
        } catch (BinanceConnectorException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}", e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }

        return null;
    }

    @Override
    public void closeAllOrdersInWaitTime(String symbol) {
        log.info("Closing all open orders for symbol: {} if not filled in {} minutes", symbol, binanceConfig.getWaitTime());
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Double positionAmount = getPositionAmount(symbol);

                if (positionAmount == null || positionAmount == 0.0) {
                    log.info("No open position found for symbol: {}", symbol);
                    closeAllOrdersImmediately(symbol);
                }
            }
        };

        long delay = binanceConfig.getWaitTime() * 60 * 1000L;

        timer.schedule(task, delay);
    }

    @Override
    public void closeAllOrdersImmediately(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

        log.info("Closing all open orders for symbol: {}", symbol);

        parameters.put("symbol", symbol);

        try {
            String result = client.account().cancelAllOpenOrders(parameters);
            log.info(result);
        } catch (BinanceConnectorException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}",
                    e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }
}
