package com.weather.service.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TemperatureUnit {
    CELSIUS("°C"),
    FAHRENHEIT("°F");

    private final String value;

    TemperatureUnit(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
