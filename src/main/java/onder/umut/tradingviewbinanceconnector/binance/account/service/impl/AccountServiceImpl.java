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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UMFuturesClientImpl client;
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

    @Override
    public Double getBalance() {
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
}
