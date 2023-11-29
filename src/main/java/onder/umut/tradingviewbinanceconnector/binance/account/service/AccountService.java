package onder.umut.tradingviewbinanceconnector.binance.account.service;

public interface AccountService {
    Double getBalance();

    Double getPositionAmount(String symbol);

    void closeAllOrdersInWaitTime(String symbol);

    void closeAllOrdersImmediately(String symbol);
}
