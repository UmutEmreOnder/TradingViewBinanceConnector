package onder.umut.tradingviewbinanceconnector.binance.account.service;

import java.util.Map;

public interface AccountService {
    Double getBalance();

    void changeInitialLeverage(Integer leverage, String symbol);

    Double getPositionAmount(String symbol);
}
