package com.weather.service.app.api.repository;

import com.weather.service.domain.City;
import com.weather.service.domain.WeatherInfo;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface WeatherInfoRepository {

    Optional<WeatherInfo> findTopByCityOrderByTimestampDesc(City city);

    List<WeatherInfo> findWeatherInfoForCityBetween(City city, ZonedDateTime startOfDay, ZonedDateTime endOfDay);

    WeatherInfo save(WeatherInfo weatherInfo);
}
