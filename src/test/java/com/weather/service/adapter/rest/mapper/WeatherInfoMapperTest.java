package com.weather.service.adapter.rest.mapper;

import com.weather.service.adapter.rest.dto.WeatherInfoResponseDTO;
import com.weather.service.domain.City;
import com.weather.service.domain.TemperatureUnit;
import com.weather.service.domain.WeatherInfo;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class WeatherInfoMapperTest {

    private final WeatherInfoMapper mapper = Mappers.getMapper(WeatherInfoMapper.class);

    @Test
    void shouldMapWeatherInfoToDTO() {
        City city = new City();
        city.setName("Moscow");
        city.setCountry("Russia");

        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setCity(city);
        weatherInfo.setTemperature(new BigDecimal("20.0"));
        weatherInfo.setTemperatureUnit(TemperatureUnit.CELSIUS);
        weatherInfo.setTimestamp(ZonedDateTime.now());

        ZoneId clientZone = ZoneId.of("Europe/Moscow");

        WeatherInfoResponseDTO result = mapper.toWeatherInfoResponseDTO(weatherInfo, clientZone);

        assertThat(result)
                .isNotNull()
                .satisfies(dto -> {
                    assertThat(dto.getCity()).isEqualTo("Moscow");
                    assertThat(dto.getTemperature()).isEqualByComparingTo(new BigDecimal("20.0"));
                    assertThat(dto.getTemperatureUnit()).isEqualTo("°C");
                    assertThat(dto.getTimestamp()).isNotNull();
                });
    }

    @Test
    void shouldThrowNPEWhenTemperatureUnitIsNull() {
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setTemperatureUnit(null); // важно

        ZoneId clientZone = ZoneId.of("Europe/Moscow");

        assertThatThrownBy(() -> mapper.toWeatherInfoResponseDTO(weatherInfo, clientZone))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("getValue");
    }
}
