package com.weather.service.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WeatherProvider {

    OPEN_WEATHER_MAP("OpenWeatherMap"),
    OPEN_METEO("Open-Meteo"),
    COMPOSITE("Composite");

    private final String value;

    WeatherProvider(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
