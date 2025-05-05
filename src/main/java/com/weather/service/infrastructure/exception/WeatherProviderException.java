package com.weather.service.infrastructure.exception;

public class WeatherProviderException extends InternalServerErrorException {

    public WeatherProviderException(String provider, String message) {
        super(String.format("Weather provider %s error: %s", provider, message));
    }
}
