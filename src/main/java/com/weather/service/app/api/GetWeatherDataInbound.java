package com.weather.service.app.api;

import com.weather.service.adapter.rest.dto.WeatherInfoResponseDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс для получения погодных данных.
 */
public interface GetWeatherDataInbound {

    /**
     * Возвращает список погодных данных на определенную дату для определенного города.
     */
    List<WeatherInfoResponseDTO> execute(String city, String country, LocalDate date,String timezone);
}
