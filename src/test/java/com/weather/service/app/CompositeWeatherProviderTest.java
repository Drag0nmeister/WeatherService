package com.weather.service.app;

import com.weather.service.app.api.WeatherProviderOutbound;
import com.weather.service.app.impl.CompositeWeatherProvider;
import com.weather.service.domain.City;
import com.weather.service.infrastructure.exception.WeatherProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompositeWeatherProviderTest {

    @Mock
    private WeatherProviderOutbound openWeather;
    @Mock
    private WeatherProviderOutbound openMeteo;
    @Mock
    private WeatherProviderOutbound exoticBroken;

    private CompositeWeatherProvider composite;
    private City moscow;

    @BeforeEach
    void setUp() {
        composite = new CompositeWeatherProvider(List.of(openWeather, openMeteo, exoticBroken));
        moscow = new City();
        moscow.setName("Moscow");
        moscow.setCountry("Russia");
    }


    @Test
    void shouldReturnAverageWhenAllSucceed() {
        when(openWeather.getProviderName()).thenReturn("OWM");
        when(openMeteo.getProviderName()).thenReturn("OM");
        when(openWeather.execute(moscow)).thenReturn(new BigDecimal("18.0"));
        when(openMeteo.execute(moscow)).thenReturn(new BigDecimal("20.0"));
        when(exoticBroken.getProviderName()).thenReturn("EX");
        when(exoticBroken.execute(moscow)).thenReturn(new BigDecimal("22.0"));

        BigDecimal actual = composite.execute(moscow);

        assertThat(actual).isEqualByComparingTo("20.00");
        verify(openWeather).execute(moscow);
        verify(openMeteo).execute(moscow);
        verify(exoticBroken).execute(moscow);
    }

    @Test
    void shouldIgnoreFailingProviders() {
        when(openWeather.getProviderName()).thenReturn("OWM");
        when(openMeteo.getProviderName()).thenReturn("OM");
        when(exoticBroken.getProviderName()).thenReturn("EX");

        when(openWeather.execute(moscow)).thenReturn(new BigDecimal("19.0"));
        when(openMeteo.execute(moscow)).thenThrow(new RuntimeException("timeout"));
        when(exoticBroken.execute(moscow)).thenReturn(new BigDecimal("21.0"));

        BigDecimal actual = composite.execute(moscow);

        assertThat(actual).isEqualByComparingTo("20.00");
    }

    @Test
    void shouldThrowIfAllFail() {
        when(openWeather.getProviderName()).thenReturn("OWM");
        when(openMeteo.getProviderName()).thenReturn("OM");
        when(exoticBroken.getProviderName()).thenReturn("EX");

        when(openWeather.execute(moscow)).thenThrow(new RuntimeException("err1"));
        when(openMeteo.execute(moscow)).thenThrow(new RuntimeException("err2"));
        when(exoticBroken.execute(moscow)).thenThrow(new RuntimeException("err3"));

        assertThatThrownBy(() -> composite.execute(moscow))
                .isInstanceOf(WeatherProviderException.class)
                .hasMessageContaining("No valid temperature data");
    }
}
