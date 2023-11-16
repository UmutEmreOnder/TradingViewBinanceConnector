package onder.umut.tradingviewbinanceconnector.tradingview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    @NotNull
    @NotBlank
    @NotEmpty
    private String symbol;

    @NotNull
    @NotBlank
    @NotEmpty
    private String action;
}
