package onder.umut.tradingviewbinanceconnector.tradingview.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onder.umut.tradingviewbinanceconnector.tradingview.dto.Alert;
import onder.umut.tradingviewbinanceconnector.tradingview.service.AlertService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/alert")
@Slf4j
@RequiredArgsConstructor
public class WebhookController {
    private final AlertService alertService;

    @PostMapping
    public void alert(@RequestBody Alert alert) {
        alertService.processAlert(alert);
    }
}
