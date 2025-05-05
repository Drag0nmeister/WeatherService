package com.weather.service.app.impl;

import com.weather.service.app.api.WeatherProviderOutbound;
import com.weather.service.domain.City;
import com.weather.service.domain.WeatherProvider;
import com.weather.service.infrastructure.exception.WeatherProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompositeWeatherProvider implements WeatherProviderOutbound {

    private final List<WeatherProviderOutbound> weatherProviders;

    @Override
    public BigDecimal execute(City city) {
        List<BigDecimal> temperatures = collectTemperatures(city);

        if (temperatures.isEmpty()) {
            throwNoValidTemperatureException(city);
        }

        return averageTemperature(temperatures);
    }

    private List<BigDecimal> collectTemperatures(City city) {
        List<BigDecimal> temperatures = new ArrayList<>();

        for (WeatherProviderOutbound provider : weatherProviders) {
            try {
                BigDecimal temperature = provider.execute(city);
                if (temperature != null) {
                    log.info("Provider {} returned {}°C for city {}", provider.getProviderName(), temperature, city.getName());
                    temperatures.add(temperature);
                }
            } catch (Exception e) {
                log.warn("Provider {} failed for city {}: {}", provider.getProviderName(), city.getName(), e.getMessage());
            }
        }

        return temperatures;
    }

    private void throwNoValidTemperatureException(City city) {
        String message = "No valid temperature data available for city: " + city.getName();
        log.error(message);
        throw new WeatherProviderException(getProviderName(), message);
    }

    private BigDecimal averageTemperature(List<BigDecimal> temperatures) {
        BigDecimal sum = temperatures.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(temperatures.size()), 2, RoundingMode.HALF_UP);
    }

    @Override
    public String getProviderName() {
        return WeatherProvider.COMPOSITE.name();
    }
}
