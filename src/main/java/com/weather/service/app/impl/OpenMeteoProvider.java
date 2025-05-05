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
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenMeteoProvider implements WeatherProviderOutbound {

    private static final String QUERY_PARAM_LATITUDE = "latitude";
    private static final String QUERY_PARAM_LONGITUDE = "longitude";
    private static final String CURRENT_WEATHER_PARAM = "current_weather";
    private static final String TEMPERATURE_UNIT_PARAM = "temperature_unit";
    private static final String PATH_OBJECT = "current_weather";
    private static final String TEMPERATURE_KEY = "temperature";

    private final WeatherProviderProperties weatherProviderProperties;
    private final RestTemplate restTemplate;
    private final WeatherResponseParser weatherResponseParser;

    @Override
    public BigDecimal execute(City city) {
        log.info("Getting temperature for city: {}, {} from Open-Meteo", city.getName(), city.getCountry());

        String url = weatherProviderProperties.getOpenmeteo().getUrl();
        Map<String, String> defaultParams = weatherProviderProperties.getOpenmeteo().getDefaultParams();

        UriComponentsBuilder uriBuilder = buildUri(city, url, defaultParams);

        try {
            String response = restTemplate.getForObject(uriBuilder.toUriString(), String.class);
            return weatherResponseParser.extractTemperature(response, PATH_OBJECT, TEMPERATURE_KEY, getProviderName());
        } catch (Exception e) {
            log.error("Error fetching weather data from OpenMeteo for city {}: {}", city.getName(), e.getMessage(), e);
        }
        return null;
    }

    private UriComponentsBuilder buildUri(City city, String url, Map<String, String> defaultParams) {
        return UriComponentsBuilder.fromHttpUrl(url)
                .queryParam(QUERY_PARAM_LATITUDE, city.getLatitude())
                .queryParam(QUERY_PARAM_LONGITUDE, city.getLongitude())
                .queryParam(CURRENT_WEATHER_PARAM, defaultParams.get(CURRENT_WEATHER_PARAM))
                .queryParam(TEMPERATURE_UNIT_PARAM, defaultParams.get(TEMPERATURE_UNIT_PARAM));
    }

    @Override
    public String getProviderName() {
        return WeatherProvider.OPEN_METEO.name();
    }
}
