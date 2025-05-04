package com.weather.service.app;

import com.weather.service.adapter.rest.dto.WeatherInfoResponseDTO;
import com.weather.service.adapter.rest.mapper.WeatherInfoMapper;
import com.weather.service.app.api.GetWeatherDataInbound;
import com.weather.service.app.api.repository.CityRepository;
import com.weather.service.app.api.repository.WeatherInfoRepository;
import com.weather.service.app.impl.GetWeatherDataUseCase;
import com.weather.service.domain.City;
import com.weather.service.domain.WeatherInfo;
import com.weather.service.infrastructure.exception.CityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetWeatherDataInboundTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private WeatherInfoRepository weatherInfoRepository;

    @Mock
    private WeatherInfoMapper weatherInfoMapper;

    private GetWeatherDataInbound getWeatherDataInbound;

    @BeforeEach
    void setUp() {
        getWeatherDataInbound = new GetWeatherDataUseCase(cityRepository, weatherInfoRepository, weatherInfoMapper);
    }

    @Test
    void shouldReturnWeatherDataForCityAndDate() {
        String city = "Moscow";
        String country = "Russia";
        LocalDate date = LocalDate.now();
        String timezone = "Europe/Moscow";
        ZoneId clientZone = ZoneId.of(timezone);

        City mockCity = new City();
        mockCity.setName(city);
        mockCity.setCountry(country);

        WeatherInfo mockWeatherInfo = new WeatherInfo();
        mockWeatherInfo.setCity(mockCity);
        mockWeatherInfo.setTemperature(new BigDecimal("20.0"));
        mockWeatherInfo.setTimestamp(ZonedDateTime.now());

        WeatherInfoResponseDTO expectedWeather = WeatherInfoResponseDTO.builder()
                .city(city)
                .temperature(new BigDecimal("20.0"))
                .temperatureUnit("CELSIUS")
                .timestamp(ZonedDateTime.now())
                .build();

        when(cityRepository.findByNameAndCountry(city, country))
                .thenReturn(Optional.of(mockCity));
        when(weatherInfoRepository.findWeatherInfoForCityBetween(any(), any(), any()))
                .thenReturn(List.of(mockWeatherInfo));
        when(weatherInfoMapper.toWeatherInfoResponseDTO(any(), eq(clientZone)))
                .thenReturn(expectedWeather);

        List<WeatherInfoResponseDTO> result = getWeatherDataInbound.execute(city, country, date, timezone);

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .contains(expectedWeather);

        verify(cityRepository).findByNameAndCountry(city, country);
        verify(weatherInfoRepository).findWeatherInfoForCityBetween(any(), any(), any());
        verify(weatherInfoMapper).toWeatherInfoResponseDTO(any(), eq(clientZone));
    }

    @Test
    void shouldReturnEmptyListWhenNoDataAvailable() {
        String city = "Moscow";
        String country = "Russia";
        LocalDate date = LocalDate.now();
        String timezone = "Europe/Moscow";

        City mockCity = new City();
        mockCity.setName(city);
        mockCity.setCountry(country);

        when(cityRepository.findByNameAndCountry(city, country))
                .thenReturn(Optional.of(mockCity));
        when(weatherInfoRepository.findWeatherInfoForCityBetween(any(), any(), any()))
                .thenReturn(List.of());

        List<WeatherInfoResponseDTO> result = getWeatherDataInbound.execute(city, country, date, timezone);

        assertThat(result)
                .isNotNull()
                .isEmpty();

        verify(cityRepository).findByNameAndCountry(city, country);
        verify(weatherInfoRepository).findWeatherInfoForCityBetween(any(), any(), any());
        verifyNoInteractions(weatherInfoMapper);
    }

    @Test
    void shouldThrowExceptionWhenCityNotFound() {
        String city = "Unknown";
        String country = "Unknown";
        LocalDate date = LocalDate.now();
        String timezone = "Europe/Moscow";

        when(cityRepository.findByNameAndCountry(city, country))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> getWeatherDataInbound.execute(city, country, date, timezone))
                .isInstanceOf(CityNotFoundException.class)
                .hasMessageContaining("City not found");

        verify(cityRepository).findByNameAndCountry(city, country);
        verifyNoInteractions(weatherInfoRepository, weatherInfoMapper);
    }

    @Test
    void shouldThrowExceptionWhenCityIsNull() {
        String city = null;
        String country = "Russia";
        LocalDate date = LocalDate.now();
        String timezone = "Europe/Moscow";

        assertThatThrownBy(() -> getWeatherDataInbound.execute(city, country, date, timezone))
                .isInstanceOf(CityNotFoundException.class)
                .hasMessageContaining("City not found");

        verify(cityRepository).findByNameAndCountry(null, country);
        verifyNoInteractions(weatherInfoRepository, weatherInfoMapper);
    }

    @Test
    void shouldThrowExceptionWhenCountryIsNull() {
        String city = "Moscow";
        String country = null;
        LocalDate date = LocalDate.now();
        String timezone = "Europe/Moscow";

        assertThatThrownBy(() -> getWeatherDataInbound.execute(city, country, date, timezone))
                .isInstanceOf(CityNotFoundException.class)
                .hasMessageContaining("City not found");

        verify(cityRepository).findByNameAndCountry(city, null);
        verifyNoInteractions(weatherInfoRepository, weatherInfoMapper);
    }
}
