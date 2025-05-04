package com.weather.service.infrastructure.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "providers")
@Getter
@Setter
public class WeatherProviderProperties {

    @Valid
    private OpenWeatherMapConfig openweathermap;
    @Valid
    private OpenMeteoConfig openmeteo;

    @Getter
    @Setter
    public static class OpenWeatherMapConfig {
        @NotNull
        private String apiKey;
        @NotNull
        private String url;
        @NotNull
        private String units;
    }

    @Getter
    @Setter
    public static class OpenMeteoConfig {
        @NotNull
        private String url;
        @NotEmpty
        private Map<String, String> defaultParams;
    }
}
