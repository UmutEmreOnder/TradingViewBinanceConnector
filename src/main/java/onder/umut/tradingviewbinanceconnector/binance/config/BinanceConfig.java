package onder.umut.tradingviewbinanceconnector.binance.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "app.binance")
@Validated
@Getter
@Setter
public class BinanceConfig {
    @NotNull
    @NotBlank
    @NotEmpty
    private String apiKey;

    @NotNull
    @NotBlank
    @NotEmpty
    private String secretKey;
}
