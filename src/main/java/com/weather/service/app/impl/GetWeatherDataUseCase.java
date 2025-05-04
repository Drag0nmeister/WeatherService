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

        if (date == null) {
            log.info("No date provided. Returning the most recent weather data for city: {}, {}", city, country);
            return weatherInfoRepository.findTopByCityOrderByTimestampDesc(findCity)
                    .map(info -> weatherInfoMapper.toWeatherInfoResponseDTO(info, clientZone))
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        }

        ZonedDateTime startOfDay = date.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        List<WeatherInfoResponseDTO> result = weatherInfoRepository.findWeatherInfoForCityBetween(findCity, startOfDay, endOfDay)
                .stream()
                .map(info -> weatherInfoMapper.toWeatherInfoResponseDTO(info, clientZone))
                .toList();

        log.info("Found {} weather records for city: {}, {}, date: {}", result.size(), city, country, date);
        return result;
    }
}
