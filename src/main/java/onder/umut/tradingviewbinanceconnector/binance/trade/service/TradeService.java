package onder.umut.tradingviewbinanceconnector.binance.trade.service;

import onder.umut.tradingviewbinanceconnector.tradingview.dto.Alert;

public interface TradeService {
    void openPosition(Alert alert);

    void closePosition(Alert alert);
}
