package com.weather.service.app.impl;

import com.weather.service.app.api.WeatherProviderOutbound;
import com.weather.service.domain.City;
import com.weather.service.domain.WeatherProvider;
import com.weather.service.infrastructure.config.WeatherProviderProperties;
import com.weather.service.infrastructure.util.WeatherResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenWeatherMapProvider implements WeatherProviderOutbound {

    private static final String LATITUDE_PARAM = "lat";
    private static final String LONGITUDE_PARAM = "lon";
    private static final String UNITS_PARAM = "units";
    private static final String API_KEY_PARAM = "appid";
    private static final String MAIN_KEY = "main";
    private static final String TEMP_KEY = "temp";

    private final WeatherProviderProperties weatherProviderProperties;
    private final RestTemplate restTemplate;
    private final WeatherResponseParser weatherResponseParser;

    @Override
    public BigDecimal execute(City city) {
        log.info("Getting temperature for city: {}, {} from OpenWeatherMap", city.getName(), city.getCountry());

        String url = weatherProviderProperties.getOpenweathermap().getUrl();
        String apiKey = weatherProviderProperties.getOpenweathermap().getApiKey();
        String units = weatherProviderProperties.getOpenweathermap().getUnits();

        UriComponentsBuilder uriBuilder = buildUri(city, url, apiKey, units);

        try {
            String response = restTemplate.getForObject(uriBuilder.toUriString(), String.class);
            return weatherResponseParser.extractTemperature(response, MAIN_KEY, TEMP_KEY, getProviderName());
        } catch (Exception e) {
            log.error("Error fetching weather data from OpenWeatherMap for city {}: {}", city.getName(), e.getMessage(), e);
        }
        return null;
    }

    private UriComponentsBuilder buildUri(City city, String url, String apiKey, String units) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam(LATITUDE_PARAM, city.getLatitude())
                .queryParam(LONGITUDE_PARAM, city.getLongitude())
                .queryParam(UNITS_PARAM, units)
                .queryParam(API_KEY_PARAM, apiKey);
    }

    @Override
    public String getProviderName() {
        return WeatherProvider.OPEN_WEATHER_MAP.name();
    }
}
