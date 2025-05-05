package com.weather.service.infrastructure.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherResponseParser {

    private final ObjectMapper objectMapper;

    public BigDecimal extractTemperature(String response, String firstKey, String secondKey, String providerName) {
        try {
            JsonNode root = objectMapper.readTree(response);
            double temperature = root.path(firstKey).path(secondKey).asDouble();

            if (temperature == 0.0) {
                log.warn("{} returned zero temperature. Ignoring.", providerName);
                return null;
            }

            return BigDecimal.valueOf(temperature);
        } catch (IOException e) {
            log.error("Error parsing temperature from {} response: {}", providerName, e.getMessage());
            return null;
        }
    }
}
