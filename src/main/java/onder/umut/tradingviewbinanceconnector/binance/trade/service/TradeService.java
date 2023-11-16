package onder.umut.tradingviewbinanceconnector.binance.trade.service;

public interface TradeService {
    void closeAllPositions();
    void openLongPosition(String symbol);
    void openShortPosition(String symbol);
}
