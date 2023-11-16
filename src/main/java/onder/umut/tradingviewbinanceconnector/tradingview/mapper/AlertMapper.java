package onder.umut.tradingviewbinanceconnector.tradingview.mapper;

import onder.umut.tradingviewbinanceconnector.tradingview.dto.Alert;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlertMapper {
    default Alert mapTo(String body) {
        String[] parts = body.split(" ");
        return new Alert(parts[0], parts[2]);
    }
}
