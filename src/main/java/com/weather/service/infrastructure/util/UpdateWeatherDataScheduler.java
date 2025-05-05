package com.weather.service.infrastructure.util;

import com.weather.service.app.api.WeatherProviderOutbound;
import com.weather.service.app.api.repository.CityRepository;
import com.weather.service.app.api.repository.WeatherInfoRepository;
import com.weather.service.domain.City;
import com.weather.service.domain.TemperatureUnit;
import com.weather.service.domain.WeatherInfo;
import com.weather.service.domain.WeatherProvider;
import com.weather.service.infrastructure.exception.WeatherProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Component
@Slf4j
public class UpdateWeatherDataScheduler {

    private final CityRepository cityRepository;
    private final WeatherInfoRepository weatherInfoRepository;
    private final WeatherProviderOutbound compositeWeatherProvider;

    public UpdateWeatherDataScheduler(CityRepository cityRepository, WeatherInfoRepository weatherInfoRepository, @Qualifier("compositeWeatherProvider") WeatherProviderOutbound compositeWeatherProvider) {
        this.cityRepository = cityRepository;
        this.weatherInfoRepository = weatherInfoRepository;
        this.compositeWeatherProvider = compositeWeatherProvider;
    }

    @Scheduled(cron = "${app.weather.update-job.cron}")
    @Transactional
    public void updateWeatherData() {
        log.info("Starting weather info update");
        ZonedDateTime now = ZonedDateTime.now();

        Long updatedCount = cityRepository.findAll().stream()
                .filter(city -> updateWeatherForCity(city, now))
                .count();
        log.info("Weather update finished. Updated {} cities.", updatedCount);
    }

    private boolean updateWeatherForCity(City city, ZonedDateTime timestamp) {
        String cityName = city.getName();
        try {
            BigDecimal temperature = compositeWeatherProvider.execute(city);
            if (temperature != null) {
                WeatherInfo providerData = new WeatherInfo();
                providerData.setCity(city);
                providerData.setTemperature(temperature);
                providerData.setTemperatureUnit(TemperatureUnit.CELSIUS);
                providerData.setProvider(WeatherProvider.COMPOSITE.name());
                providerData.setTimestamp(timestamp);

                weatherInfoRepository.save(providerData);
                log.info("Updated weather data for city: {}", cityName);
                return true;
            } else {
                log.info("No valid weather data for city: {}", cityName);
            }
        } catch (WeatherProviderException e) {
            log.error("Weather provider failed: {}", e.getMessage(), e);
            // TODO: отправить алерт когда выйдет в прод
        } catch (Exception e) {
            log.error("Failed to update weather data for city {}: {}", cityName, e.getMessage(), e);
        }
        return false;
    }
}
