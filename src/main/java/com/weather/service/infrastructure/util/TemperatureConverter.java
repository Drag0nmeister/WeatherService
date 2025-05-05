package com.weather.service.infrastructure.util;

import com.weather.service.domain.TemperatureUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class TemperatureConverter {

    public BigDecimal convertTo(BigDecimal value, TemperatureUnit fromUnit, TemperatureUnit targetUnit) {
        if (fromUnit == targetUnit) {
            return value;
        }

        BigDecimal result = convertTemperature(value, fromUnit, targetUnit);
        log.info("Converted {} {} to {} {}", value, fromUnit, result, targetUnit);
        return result;
    }

    private BigDecimal convertTemperature(BigDecimal value, TemperatureUnit fromUnit, TemperatureUnit targetUnit) {
        return switch (targetUnit) {
            case CELSIUS -> fromUnit == TemperatureUnit.FAHRENHEIT ? fahrenheitToCelsius(value) : value;
            case FAHRENHEIT -> fromUnit == TemperatureUnit.CELSIUS ? celsiusToFahrenheit(value) : value;
        };
    }

    private BigDecimal fahrenheitToCelsius(BigDecimal fahrenheit) {
        return fahrenheit.subtract(BigDecimal.valueOf(32))
                .multiply(BigDecimal.valueOf(5))
                .divide(BigDecimal.valueOf(9), RoundingMode.HALF_UP);
    }

    private BigDecimal celsiusToFahrenheit(BigDecimal celsius) {
        return celsius.multiply(BigDecimal.valueOf(9))
                .divide(BigDecimal.valueOf(5), RoundingMode.HALF_UP)
                .add(BigDecimal.valueOf(32));
    }
}
