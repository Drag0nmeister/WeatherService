package com.weather.service.adapter.rest.mapper;

import com.weather.service.adapter.rest.dto.WeatherInfoResponseDTO;
import com.weather.service.domain.WeatherInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface WeatherInfoMapper {

    @Mapping(target = "city", source = "weatherInfo.city.name")
    @Mapping(target = "temperatureUnit", expression = "java(weatherInfo.getTemperatureUnit().getValue())")
    @Mapping(target = "timestamp", expression = "java(weatherInfo.getTimestamp().withZoneSameInstant(zoneId))")
    WeatherInfoResponseDTO toWeatherInfoResponseDTO(WeatherInfo weatherInfo, ZoneId zoneId);
}

