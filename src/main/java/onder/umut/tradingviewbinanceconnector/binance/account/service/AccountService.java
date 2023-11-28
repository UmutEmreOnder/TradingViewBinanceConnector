package onder.umut.tradingviewbinanceconnector.binance.account.service;

public interface AccountService {
    Double getBalance();

    void changeInitialLeverage(Integer leverage, String symbol);

    Double getPositionAmount(String symbol);

    void closeAllOrdersInWaitTime(String symbol);

    void closeAllOrdersImmediately(String symbol);
}
