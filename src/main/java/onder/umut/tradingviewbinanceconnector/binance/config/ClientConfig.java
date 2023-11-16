package onder.umut.tradingviewbinanceconnector.binance.config;

import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ClientConfig {
    private final BinanceConfig binanceConfig;

    @Bean
    public UMFuturesClientImpl cmFuturesClient() {
        return new UMFuturesClientImpl(binanceConfig.getApiKey(), binanceConfig.getSecretKey(), "https://fapi.binance.com");
    }
}
