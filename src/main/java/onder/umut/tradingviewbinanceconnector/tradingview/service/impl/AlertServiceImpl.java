package onder.umut.tradingviewbinanceconnector.tradingview.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onder.umut.tradingviewbinanceconnector.tradingview.dto.Alert;
import onder.umut.tradingviewbinanceconnector.tradingview.service.AlertService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AlertServiceImpl implements AlertService {
    private String lastAction = "";

    @Override
    public void processAlert(Alert alert) {
        log.info("Received alert: {}", alert);

        if (lastAction.equals(alert.getAction())) {
            log.info("Skipping alert: {}", alert);
            return;
        }

        lastAction = alert.getAction();
        switch (alert.getAction()) {
            case "buy" -> log.info("Executing buy action for alert: {}", alert);
            case "sell" -> log.info("Executing sell action for alert: {}", alert);
            default -> log.error("Unknown action '{}' for alert: {}", alert.getAction(), alert);
        }
    }
}
