package onder.umut.tradingviewbinanceconnector.tradingview.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onder.umut.tradingviewbinanceconnector.binance.trade.service.TradeService;
import onder.umut.tradingviewbinanceconnector.tradingview.dto.Alert;
import onder.umut.tradingviewbinanceconnector.tradingview.service.AlertService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AlertServiceImpl implements AlertService {
    private final TradeService tradeService;
    @Override
    public void processAlert(Alert alert) {
        log.info("Received alert: {}", alert);

        String symbol = alert.getSymbol().split("\\.")[0];
        alert.setSymbol(symbol);

        if (alert.getSignalAction().equals("flat")) {
            tradeService.closePosition(alert);
        } else {
            tradeService.openPosition(alert);
        }
    }
}
