package com.weather.service.infrastructure.exception;

public class CityNotFoundException extends InternalServerErrorException {

    public CityNotFoundException(String cityName, String country) {
        super(String.format("City not found: %s, %s", cityName, country));
    }
}
