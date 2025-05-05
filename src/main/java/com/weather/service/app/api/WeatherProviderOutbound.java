package com.weather.service.app.api;

import com.weather.service.domain.City;

import java.math.BigDecimal;

/**
 * Интерфейс для получения информации о погоде от погодных провайдеров.
 */
public interface WeatherProviderOutbound {

    /**
     * Возвращает температуру.
     */
    BigDecimal execute(City city);

    /**
     * Возвращает имя провайдера.
     */
    String getProviderName();
}
