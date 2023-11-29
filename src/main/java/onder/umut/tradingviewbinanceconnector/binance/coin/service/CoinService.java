package onder.umut.tradingviewbinanceconnector.binance.coin.service;

import onder.umut.tradingviewbinanceconnector.tradingview.dto.Alert;

public interface CoinService {
    void changeInitialLeverage(Integer leverage, String symbol);

    void fitToPrecision(Alert alert);
}
