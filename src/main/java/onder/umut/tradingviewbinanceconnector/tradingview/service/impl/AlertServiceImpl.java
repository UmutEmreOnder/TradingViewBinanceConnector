package onder.umut.tradingviewbinanceconnector.tradingview.service.impl;

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
    private String lastAction = "";

    @Override
    public void processAlert(Alert alert) {
        log.info("Received alert: {}", alert);

        if (lastAction.equals(alert.getAction())) {
            log.info("Skipping alert because last action '{}' is the same as current action '{}'", lastAction, alert.getAction());
            return;
        }

        lastAction = alert.getAction();
        tradeService.closeAllPositions();

        switch (alert.getAction()) {
            case "buy" -> tradeService.openLongPosition(alert.getSymbol());
            case "sell" -> tradeService.openShortPosition(alert.getSymbol());
            default -> log.error("Unknown action '{}' for alert: {}", alert.getAction(), alert);
        }
    }
}
