package com.weather.service.app.impl;

import com.weather.service.adapter.rest.dto.WeatherInfoResponseDTO;
import com.weather.service.adapter.rest.mapper.WeatherInfoMapper;
import com.weather.service.app.api.repository.CityRepository;
import com.weather.service.app.api.GetWeatherDataInbound;
import com.weather.service.app.api.repository.WeatherInfoRepository;
import com.weather.service.infrastructure.exception.CityNotFoundException;
import com.weather.service.domain.City;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetWeatherDataUseCase implements GetWeatherDataInbound {

    private final CityRepository cityRepository;
    private final WeatherInfoRepository weatherInfoRepository;
    private final WeatherInfoMapper weatherInfoMapper;

    @Override
    @Transactional
    public List<WeatherInfoResponseDTO> execute(String city, String country, LocalDate date, String timezone) {
        log.info("Getting weather info for city: {}, {}, date: {}", city, country, date);

        City findCity = cityRepository.findByNameAndCountry(city, country)
                .orElseThrow(() -> new CityNotFoundException(city, country));
        ZoneId clientZone = ZoneId.of(timezone);

        return (date == null) ? getLatestWeather(findCity, clientZone) : getWeatherForDate(findCity, clientZone, date, city, country);
    }


    private List<WeatherInfoResponseDTO> getLatestWeather(City city, ZoneId clientZone) {
        log.info("No date provided. Returning most recent data for city: {}, {}", city.getName(), city.getCountry());

        return weatherInfoRepository.findTopByCityOrderByTimestampDesc(city)
                .map(info -> weatherInfoMapper.toWeatherInfoResponseDTO(info, clientZone))
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }

    private List<WeatherInfoResponseDTO> getWeatherForDate(City city, ZoneId clientZone, LocalDate date, String cityName, String country) {
        ZonedDateTime startOfDay = date.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        List<WeatherInfoResponseDTO> results = weatherInfoRepository.findWeatherInfoForCityBetween(city, startOfDay, endOfDay)
                .stream()
                .map(info -> weatherInfoMapper.toWeatherInfoResponseDTO(info, clientZone))
                .toList();

        log.info("Found {} weather records for city: {}, {}, date: {}", results.size(), cityName, country, date);
        return results;
    }
}
