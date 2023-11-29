package onder.umut.tradingviewbinanceconnector.tradingview.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    @NotNull
    @NotBlank
    @NotEmpty
    private String symbol;

    @NotNull
    @NotBlank
    @NotEmpty
    private String position;

    @NotNull
    @NotBlank
    @NotEmpty
    private String signalAction;

    @NotNull
    private Double entryPrice;

    @NotNull
    private Double takeProfit;

    @NotNull
    private Integer leverage;

    @NotNull
    @Min(1)
    private Integer positionSize;
}
