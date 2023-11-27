package onder.umut.tradingviewbinanceconnector.binance.trade.service.impl;

import com.binance.connector.futures.client.exceptions.BinanceClientException;
import com.binance.connector.futures.client.exceptions.BinanceConnectorException;
import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onder.umut.tradingviewbinanceconnector.binance.account.service.AccountService;
import onder.umut.tradingviewbinanceconnector.binance.trade.service.TradeService;
import onder.umut.tradingviewbinanceconnector.tradingview.dto.Alert;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeServiceImpl implements TradeService {
    private final AccountService accountService;
    private final UMFuturesClientImpl client;

    @Override
    public void openPosition(Alert alert) {
        log.info("Opening long position for symbol: {}", alert.getSymbol());
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        Double balance = accountService.getBalance();

        if (balance < alert.getPositionSize()) {
            log.error("Not enough balance to open long position for symbol: {}", alert.getSymbol());
            return;
        }

        Double quantity = calculateQuantity(alert.getEntryPrice(), alert.getPositionSize(), alert.getLeverage());
        accountService.changeInitialLeverage(alert.getLeverage(), alert.getSymbol());

        parameters.put("symbol", alert.getSymbol());
        parameters.put("side", alert.getPosition().toUpperCase());
        parameters.put("type", "LIMIT");
        parameters.put("quantity", quantity);
        parameters.put("timeInForce", "GTC");
        parameters.put("price", alert.getEntryPrice());


        try {
            client.account().newOrder(parameters);
            accountService.closeAllOrders(alert.getSymbol()); // If the order is not filled, cancel it after x minutes

            log.info("Long position opened for {} with amount {}", alert.getSymbol(), quantity);
        } catch (BinanceConnectorException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}", e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    @Override
    public void closePosition(Alert alert) {
        log.info("Closing position for symbol: {}", alert.getSymbol());
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        Double quantity = accountService.getPositionAmount(alert.getSymbol());

        if (quantity == null || quantity == 0.0) {
            log.error("No open position found for symbol: {}", alert.getSymbol());
            return;
        }

        parameters.put("symbol", alert.getSymbol());
        parameters.put("side", alert.getPosition().toUpperCase());
        parameters.put("type", "MARKET");
        parameters.put("quantity", quantity);

        try {
            client.account().newOrder(parameters);
            log.info("Position closed for symbol: {}", alert.getSymbol());
        } catch (BinanceConnectorException e) {
            log.error("fullErrMessage: {}", e.getMessage(), e);
        } catch (BinanceClientException e) {
            log.error("fullErrMessage: {} \nerrMessage: {} \nerrCode: {} \nHTTPStatusCode: {}", e.getMessage(), e.getErrMsg(), e.getErrorCode(), e.getHttpStatusCode(), e);
        }
    }

    private Double calculateQuantity(Double entryPrice, Integer positionSize, Integer leverage) {
        double quantity = positionSize * leverage / entryPrice;
        return Math.round(quantity * 100.0) / 100.0;
    }
}
